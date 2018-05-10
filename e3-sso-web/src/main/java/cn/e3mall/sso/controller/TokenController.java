package cn.e3mall.sso.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.common.utils.E3Result;
import cn.e3.common.utils.JsonUtils;
import cn.e3mall.sso.service.TokenService;

@Controller
public class TokenController {

	@Autowired
	private TokenService tokenService;

	@RequestMapping(value="/user/token/{token}",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public Object getUserByToken(@PathVariable String token,String callback) {
		//从redis中查询token是否还存在，如果存在返回json对象，不存在返回空
		E3Result result = tokenService.getUserByToken(token);
		//响应结果之前，判断是否为jsonp请求
		if(StringUtils.isNoneBlank(callback)){
			//把结果封装成一个js语句
		MappingJacksonValue mappingJacksonValue=new MappingJacksonValue(result);
		mappingJacksonValue.setJsonpFunction(callback);
		return mappingJacksonValue;
		}
		return result;
	}

}
