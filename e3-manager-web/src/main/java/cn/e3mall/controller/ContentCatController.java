package cn.e3mall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.common.pojo.EasyUITreeNode;
import cn.e3.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;

@Controller
@RequestMapping("/content/category")
public class ContentCatController {

	@Autowired
	private ContentCategoryService contentCategoryService;
	
	@RequestMapping("/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentCatList(
			@RequestParam(value="id", defaultValue="0") Long parentId) {
		
		List<EasyUITreeNode> list = contentCategoryService.getContentCatList(parentId);
		return list;
	}
	
	/**
	 * 添加分类节点
	 * */
	@RequestMapping("/create")
	@ResponseBody
	public E3Result createCategory(Long parentId, String name) {
		E3Result result = contentCategoryService.addContentCategory(parentId, name);
		return result;
	}
	
	/**
	 * 节点重命名
	 * */
	@RequestMapping("/update")
	@ResponseBody
	public E3Result updateCategory(Long id, String name) {
		E3Result result = contentCategoryService.updateCategory(id, name);
		return result;
	}
	
	/**
	 * 删除节点
	 * */
	@RequestMapping("/delete")
	@ResponseBody
	public E3Result deleteCategory(Long id) {
		//判断是否有子节点，如果有则不能删除
		if(contentCategoryService.isParent(id)){
			return new E3Result(300, "", null);
		}
		//删除节点
		E3Result result = contentCategoryService.deleteCategory(id);
		return result;
	}
	
}

