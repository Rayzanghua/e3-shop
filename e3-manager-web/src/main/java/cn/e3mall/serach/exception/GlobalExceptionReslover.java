package cn.e3mall.serach.exception;

import java.io.IOException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import cn.e3.common.utils.E3Result;

@ControllerAdvice
public class GlobalExceptionReslover extends ResponseEntityExceptionHandler {

	@ExceptionHandler({Exception.class})
    @ResponseBody
    public E3Result handleDataNotFoundException(RuntimeException ex) throws IOException {
		E3Result result=new E3Result(400,"请求错误",null);
		return result;
    }

}

