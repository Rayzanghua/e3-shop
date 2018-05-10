package cn.e3mall.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3.common.utils.CookieUtils;
import cn.e3.common.utils.E3Result;
import cn.e3.common.utils.JsonUtils;
import cn.e3mall.cart.service.CartService;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;

public class LoginInterceptor implements HandlerInterceptor{

	@Value("${SSO_URL}")
	private String SSO_URL;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private CartService cartService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// TODO Auto-generated method stub
		//从cookie取token
		String token = CookieUtils.getCookieValue(request, "COOKIE_TOKEN_KEY");
		//判断token是否存在
		if(StringUtils.isBlank(token)){
			//如果token不存在，未登录状态，跳转到sso系统的登录页面。用户登录成功后，跳转到当前请求的url
			response.sendRedirect(SSO_URL+"/page/login?redirect="+request.getRequestURL());
			//拦截
			return false;
		}
		//如果token存在，调用sso系统的服务，根据token取用户信息
		E3Result e3Result=tokenService.getUserByToken(token);
		//如果取不到，用户登录已经过期，需要登录
		if(e3Result.getStatus()!=200){
			response.sendRedirect(SSO_URL+"/page/login?redirect="+request.getRequestURL());
			return false;
		}
		//如果取到用户信息，是登录状态，需要把用户信息写入request
		TbUser user = (TbUser) e3Result.getData();
		request.setAttribute("user",user);
		String jsonCartList=CookieUtils.getCookieValue(request, "cart",true);
		if(StringUtils.isNoneBlank(jsonCartList)){
			cartService.mergeCart(user.getId(), JsonUtils.jsonToList(jsonCartList, TbItem.class));
		}
		//判断cookie中是否有购物车数据，如果有就合并到服务端。
		//放行
		
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
