package cn.e3mall.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3.common.jedis.JedisClient;
import cn.e3.common.pojo.EasyUIDataGridResult;
import cn.e3.common.utils.E3Result;
import cn.e3.common.utils.IDUtils;
import cn.e3.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.mapper.TbItemParamMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.pojo.TbItemExample.Criteria;
import cn.e3mall.service.ItemService;

/**
 * 商品管理Service
 * <p>Title: ItemServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemDescMapper itemDescMapper;
	
	@Autowired
	private TbItemParamMapper itemParamMapper;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Resource
	private Destination topicDestination;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${ITEM_INFO_PRE}")
	private String ITEM_INFO_PRE;
	
	@Value("${ITEM_INFO_EXPIRE}")
	private Integer  ITEM_INFO_EXPIRE;
	
	@Override
	public TbItem getItemById(long itemId) {
		try {
			//查询缓存
			String json = jedisClient.get(ITEM_INFO_PRE + ":" + itemId + ":BASE");
			if (StringUtils.isNotBlank(json)) {
				//把json转换为java对象
				TbItem item = JsonUtils.jsonToPojo(json, TbItem.class);
				return item;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//根据主键查询
		//TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		TbItemExample example = new TbItemExample();
		Criteria criteria = example.createCriteria();
		//设置查询条件
		criteria.andIdEqualTo(itemId);
		//执行查询
		List<TbItem> list = itemMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			TbItem item = list.get(0);
			try {
				//把数据保存到缓存
				jedisClient.set(ITEM_INFO_PRE + ":" + itemId + ":BASE", JsonUtils.objectToJson(item));
				//设置缓存的有效期
				jedisClient.expire(ITEM_INFO_PRE + ":" + itemId + ":BASE", ITEM_INFO_EXPIRE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return item;
		}
		return null;
	}

	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		
		//设置分页信息
		PageHelper.startPage(page, rows);
		//执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		//创建一个返回值对象
		EasyUIDataGridResult result=new EasyUIDataGridResult();
		result.setRows(list);
		//取分页信息
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		
		//创建返回结果对象
		long total=pageInfo.getTotal();
		result.setTotal(total);
		
		return result;
	}

	@Override
	public E3Result addItem(TbItem item, String desc) {
		// 1、生成商品id
		final long itemId = IDUtils.genItemId();
		// 2、补全TbItem对象的属性
		item.setId(itemId);
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte) 1);
		Date date = new Date();
		item.setCreated(date);
		item.setUpdated(date);
		// 3、向商品表插入数据
		itemMapper.insert(item);
		// 4、创建一个TbItemDesc对象
		TbItemDesc itemDesc = new TbItemDesc();
		// 5、补全TbItemDesc的属性
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(date);
		itemDesc.setUpdated(date);
		// 6、向商品描述表插入数据
		itemDescMapper.insert(itemDesc);
		//发送商品添加消息
		jmsTemplate.send(topicDestination,new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(itemId+"");
			}
		});
		// 7、E3Result.ok()
		return E3Result.ok();
	}

	@Override
	public TbItemDesc getItemDescById(long itemId) {
		try {
			String json = jedisClient.get(ITEM_INFO_PRE + ":" + itemId + ":DESC");
			//判断缓存是否命中
			if (StringUtils.isNotBlank(json) ) {
				//转换为java对象
				TbItemDesc itemDesc = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				return itemDesc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		try {
			jedisClient.set(ITEM_INFO_PRE + ":" + itemId + ":DESC", JsonUtils.objectToJson(itemDesc));
			//设置过期时间
			jedisClient.expire(ITEM_INFO_PRE + ":" + itemId + ":DESC", ITEM_INFO_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDesc;
	}

	@Override
	public E3Result delItem(Long ids) {
		itemMapper.deleteByPrimaryKey(ids);
		return E3Result.ok();
	}

	@Override
	public E3Result updateTbItem(TbItem item) {
		itemMapper.updateByPrimaryKey(item);
		return E3Result.ok();
	}

	//可以添加redis缓存
	@Override
	public E3Result itemDesc(Long itemId) {
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		return E3Result.ok(itemDesc);
	}

	//暂时不清楚
	@Override
	public E3Result itemParam(Long itemId) {
		return E3Result.ok(itemParamMapper.selectByPrimaryKey(itemId));
	}

	@Override
	public E3Result reshelf(Long ids) {
		itemMapper.statusChange((byte)1,ids);
		return E3Result.ok();
	}

	@Override
	public E3Result instock(Long ids) {
		itemMapper.statusChange((byte)2,ids);
		return E3Result.ok();
	}

	


}
