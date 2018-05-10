package cn.e3mall.search.mapper;

import java.util.List;

import cn.e3.common.pojo.SearchItem;

public interface ItemMapper {

	List<SearchItem> getItemList();

	SearchItem getItemById(Long itemId);
}
