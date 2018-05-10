package cn.e3mall.sso.service;

import cn.e3.common.utils.E3Result;

public interface LoginService {

	//参数是用户名和密码
	//业务逻辑
	/**
	 * 判断用户名 密码是否正确
	 * 如果不正确返回登录失败
	 * 如果正确生成token
	 * 把用户信息写入redis，key：token	value：用户信息
	 * 设置session的过期时间
	 * 把token返回
	 * */
	//返回值：E3Result,其中包含token信息
	E3Result userLogin(String username,String password);
}
