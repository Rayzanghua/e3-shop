package cn.e3mall.cart.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3.common.utils.CookieUtils;
import cn.e3.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;

public class LoginInterceptor implements HandlerInterceptor{

	@Autowired
	private TokenService tokenService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// TODO Auto-generated method stub
		//1.从cookie中取token
		String token = CookieUtils.getCookieValue(request, "COOKIE_TOKEN_KEY");
		//2.如果没有token，未登录状态，直接放行
		if(StringUtils.isBlank(token)){
			return true;
		}
		//3.取到token，需要调用sso系统服务，根据token取用户信息
		E3Result e3Result=tokenService.getUserByToken(token);
		//4.没有取到用户信息。登录过期，直接放行
		if(e3Result.getStatus()!=200){
			return true;
		}
		//5.取到用户信息，登录状态
		TbUser user=(TbUser) e3Result.getData();
		//6.把用户信息放到request中。只需要在Controller中判断request是否包含user信息
		request.setAttribute("user", user);
		return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}


}
