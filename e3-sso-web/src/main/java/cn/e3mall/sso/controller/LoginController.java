package cn.e3mall.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.common.utils.CookieUtils;
import cn.e3.common.utils.E3Result;
import cn.e3mall.sso.service.LoginService;

@Controller
public class LoginController {
	//http://localhost:8098/

	@Autowired
	private LoginService loginService;
	
	@Value("COOKIE_TOKEN_KEY")
	private String COOKIE_TOKEN_KEY;
	
	@RequestMapping("/page/login")
	public String showLogin(String redirect,Model model){
		model.addAttribute("redirect", redirect);
		return "login";
	}
	
	@RequestMapping(value="/user/login", method=RequestMethod.POST)
	@ResponseBody
	public E3Result login(String username, String password,
			HttpServletRequest request, HttpServletResponse response) {
		// 1、接收两个参数。
		// 2、调用Service进行登录。
		E3Result result = loginService.userLogin(username, password);
		// 3、从返回结果中取token，写入cookie。Cookie要跨域。
		//如果登录失败，直接返回
		if(result.getData()==null){
			return result;
		}
		//登录成功，需要将用户的信息写入到cookie中
		String token = result.getData().toString();
		CookieUtils.setCookie(request, response, COOKIE_TOKEN_KEY, token);
		// 4、响应数据。Json数据。E3Result，其中包含Token。
		return result;
		
	}

}
