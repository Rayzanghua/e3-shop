package cn.e3mall.service;

import java.util.List;

import cn.e3.common.pojo.EasyUITreeNode;

public interface ItemCatService {
	List<EasyUITreeNode> getItemCatList(long parentId);
}
