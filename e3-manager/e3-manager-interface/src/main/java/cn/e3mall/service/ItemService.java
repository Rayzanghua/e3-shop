package cn.e3mall.service;

import cn.e3.common.pojo.EasyUIDataGridResult;
import cn.e3.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemParam;

public interface ItemService {

	TbItem getItemById(long itemId);
	
	EasyUIDataGridResult getItemList(int page,int rows);
	
	E3Result addItem(TbItem item,String desc);
	
	TbItemDesc getItemDescById(long itemId);

	E3Result delItem(Long ids);

	E3Result updateTbItem(TbItem item);

	E3Result itemDesc(Long itemId);

	E3Result itemParam(Long itemId);

	E3Result reshelf(Long ids);

	E3Result instock(Long ids);
}
