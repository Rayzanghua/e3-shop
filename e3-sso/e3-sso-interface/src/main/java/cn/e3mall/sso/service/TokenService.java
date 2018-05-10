package cn.e3mall.sso.service;

import cn.e3.common.utils.E3Result;

/**
 * 根据token取信息
 * */
public interface TokenService {

	public E3Result getUserByToken(String token);
}
