package cn.e3mall.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.common.pojo.EasyUIDataGridResult;
import cn.e3.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemParam;
import cn.e3mall.service.ItemService;

/**
 * 商品管理Controller
 * <p>Title: ItemController</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Controller
public class ItemController {

	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item/{itemId}")
	@ResponseBody
	public TbItem getItemById(@PathVariable Long itemId) {
		TbItem tbItem = itemService.getItemById(itemId);
		return tbItem;
	}
	
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
		EasyUIDataGridResult result = itemService.getItemList(page, rows);
		return result;
	}

	/**
	 * 商品添加功能
	 * */
	@RequestMapping(value="/item/save",method=RequestMethod.POST)
	@ResponseBody
	public E3Result saveItem(TbItem item, String desc) {
		E3Result result = itemService.addItem(item, desc);
		return result;
	}

	/**
	 * 商品编辑功能
	 * 传的数据create和update还有status没有
	 * */
	@RequestMapping(value="/rest/item/update",method=RequestMethod.POST)
	@ResponseBody
	public E3Result editItem(TbItem item) {
		//前端数据无法回传，暂时先用这个
		item.setStatus((byte) 1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		return  itemService.updateTbItem(item);
	}

	/**
	 * 商品删除功能
	 * 删除功能应该是修改那个状态把
	 * */
	@RequestMapping(value="/item/delete",method=RequestMethod.POST)
	@ResponseBody
	public E3Result deleteItem(Long ids) {
		return  itemService.delItem(ids);
	}
	
	/**
	 * 商品描述查询
	 * */
	@RequestMapping(value="/rest/item/query/item/desc/{itemId}")
	@ResponseBody
	public E3Result itemDesc(@PathVariable Long itemId) {
		return  itemService.itemDesc(itemId);
	}
	
	/**
	 * 商品规格查询 暂时不清楚，应该还需要和其他表联合查询
	 * 此方法有问题
	 * */
	@RequestMapping(value="/rest/item/param/item/query/{itemId}")
	@ResponseBody
	public E3Result itemParam(@PathVariable Long itemId) {
		return  itemService.itemParam(itemId);
	}
	
	/**
	 * 商品上架功能
	 * */
	@RequestMapping(value="/rest/item/reshelf",method=RequestMethod.POST)
	@ResponseBody
	public E3Result reshelfItem(Long ids) {
		return itemService.reshelf(ids);
	}
	
	/**
	 * 商品下架功能
	 * */
	@RequestMapping(value="/rest/item/instock",method=RequestMethod.POST)
	@ResponseBody
	public E3Result instockItem(Long ids) {
		return itemService.instock(ids);
	}
}
