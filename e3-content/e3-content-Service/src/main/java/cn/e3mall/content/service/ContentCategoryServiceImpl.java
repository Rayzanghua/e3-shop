package cn.e3mall.content.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3.common.pojo.EasyUITreeNode;
import cn.e3.common.utils.E3Result;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;

/**
 * 内容分类管理Service
 * */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService{

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<EasyUITreeNode> getContentCatList(long parentId) {
		//根据parentId查询子节点列表
		TbContentCategoryExample example=new TbContentCategoryExample();
		Criteria criteria=example.createCriteria();
		//设置条件
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		// 4、把列表转换成List<EasyUITreeNode>ub
				List<EasyUITreeNode> resultList = new ArrayList<>();
				for (TbContentCategory tbContentCategory : list) {
					EasyUITreeNode node = new EasyUITreeNode();
					node.setId(tbContentCategory.getId());
					node.setText(tbContentCategory.getName());
					node.setState(tbContentCategory.getIsParent()?"closed":"open");
					//添加到列表
					resultList.add(node);
				}
				return resultList;

	}

	@Override
	public E3Result addContentCategory(long parentId, String name) {
		// 1、接收两个参数：parentId、name
		// 2、向tb_content_category表中插入数据。
		// a)创建一个TbContentCategory对象
		TbContentCategory tbContentCategory = new TbContentCategory();
		// b)补全TbContentCategory对象的属性
		tbContentCategory.setIsParent(false);
		tbContentCategory.setName(name);
		tbContentCategory.setParentId(parentId);
		//排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数
		tbContentCategory.setSortOrder(1);
		//状态。可选值:1(正常),2(删除)
		tbContentCategory.setStatus(1);
		tbContentCategory.setCreated(new Date());
		tbContentCategory.setUpdated(new Date());
		// c)向tb_content_category表中插入数据
		contentCategoryMapper.insert(tbContentCategory);
		// 3、判断父节点的isparent是否为true，不是true需要改为true。
		TbContentCategory parentNode = contentCategoryMapper.selectByPrimaryKey(parentId);
		if (!parentNode.getIsParent()) {
			parentNode.setIsParent(true);
			//更新父节点
			contentCategoryMapper.updateByPrimaryKey(parentNode);
		}
		// 4、需要主键返回。
		// 5、返回E3Result，其中包装TbContentCategory对象
		return E3Result.ok(tbContentCategory);
	}

	//重命名而已
	@Override
	public E3Result updateCategory(Long id, String name) {
		contentCategoryMapper.updateName(id,name);
		return E3Result.ok();
	}

	//删除节点
	@Override
	public E3Result deleteCategory(Long id) {
		//获取该节点，父节点id
		long parentId = contentCategoryMapper.getParentId(id);
		//判断有几个节点属于该节点的子节点，总共有几个，如果只有一个，还需要修改它的is_parent
		int nodes = contentCategoryMapper.parentSonNodes(parentId);
		if(nodes==1){
			//如果只有一个该子节点，先将父节点的is_parent改为0
			contentCategoryMapper.parentChange(parentId);
		}
		//删除该节点
		contentCategoryMapper.deleteByPrimaryKey(id);
		return E3Result.ok();
	}

	@Override
	public boolean isParent(Long id) {
		return contentCategoryMapper.isParent(id)==1?true:false;
	}


}
