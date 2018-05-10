package cn.e3mall.content.service;

import java.util.List;

import cn.e3.common.pojo.EasyUITreeNode;
import cn.e3.common.utils.E3Result;

public interface ContentCategoryService {

	List<EasyUITreeNode> getContentCatList(long parentId);
	
	E3Result addContentCategory(long parentId,String name);

	E3Result updateCategory(Long id, String name);

	E3Result deleteCategory(Long id);

	boolean isParent(Long id);

}
