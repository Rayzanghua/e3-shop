package cn.e3mall.content.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3.common.jedis.JedisClient;
import cn.e3.common.pojo.EasyUIDataGridResult;
import cn.e3.common.utils.E3Result;
import cn.e3.common.utils.JsonUtils;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbContentExample.Criteria;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${CONTENT_LIST}")
	private String CONTENT_LIST;
	
	@Override
	public E3Result addContent(TbContent content) {
		//补全属性
		content.setCreated(new Date());
		content.setUpdated(new Date());
		//插入数据
		contentMapper.insert(content);
		//缓存同步
		jedisClient.hdel(CONTENT_LIST, content.getCategoryId().toString());
		
		return E3Result.ok();
	}

	/**
	 * 根据内容分类id查询列表
	 * */
	@Override
	public List<TbContent> getContentListByCid(long cid) {
		// TODO Auto-generated method stub
		//查询缓存
		try {
			String json = jedisClient.hget(CONTENT_LIST, cid + "");
			//判断json是否为空
			if (StringUtils.isNotBlank(json)) {
				//把json转换成list
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		TbContentExample example=new TbContentExample();
		Criteria criteria=example.createCriteria();
		//设置查询条件
		criteria.andCategoryIdEqualTo(cid);
		//执行查询
		List<TbContent> list=contentMapper.selectByExample(example);
		//向缓存中添加数据
		try {
			jedisClient.hset(CONTENT_LIST, cid + "", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	@Override
	public EasyUIDataGridResult getContentList(int categoryId, int page, int rows) {
		
		//设置分页信息
		PageHelper.startPage(page, rows);
		//执行查询
		
		List<TbContent> list = contentMapper.selectByCategoryId(categoryId);
		//创建一个返回值对象
		EasyUIDataGridResult result=new EasyUIDataGridResult();
		result.setRows(list);
		//取分页信息
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		
		//创建返回结果对象
		long total=pageInfo.getTotal();
		result.setTotal(total);
		
		return result;
	}

	@Override
	public E3Result deleteContent(int ids) {
		contentMapper.deleteByPrimaryKey((long)ids);
		return E3Result.ok();
	}

	@Override
	public E3Result editContent(TbContent content) {
		contentMapper.updateByPrimaryKey(content);
		return E3Result.ok();
	}

}

