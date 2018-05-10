package cn.e3mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.common.pojo.EasyUIDataGridResult;
import cn.e3.common.utils.E3Result;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;

/**
 * 内容管理
 * */
@Controller
public class ContentController {

	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/content/save")
	@ResponseBody
	public E3Result addContent(TbContent content) {
		E3Result result = contentService.addContent(content);
		return result;
	}
	
	//返回一个内容分类下的所有内容
	@RequestMapping("/content/query/list")
	@ResponseBody
	public EasyUIDataGridResult listContent(@RequestParam int categoryId,@RequestParam int page,@RequestParam int rows) {
		EasyUIDataGridResult result = contentService.getContentList(categoryId,page,rows);
		return result;
	}
	
	//删除某条内容/content/delete
	@RequestMapping("/content/delete")
	@ResponseBody
	public E3Result deleteContent(@RequestParam int ids) {
		E3Result result = contentService.deleteContent(ids);
		return result;
	}
	
	//修改某条内容 	前端原因导致传入少个值，暂时没有分析处理
	@RequestMapping("/rest/content/edit")
	@ResponseBody
	public E3Result editContent(TbContent content) {
		E3Result result = contentService.editContent(content);
		return result;
	}
}

