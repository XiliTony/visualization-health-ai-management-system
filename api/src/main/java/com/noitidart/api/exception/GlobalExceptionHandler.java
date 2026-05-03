package com.noitidart.api.exception;

import com.noitidart.api.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理类
 */
@ControllerAdvice("com.noitidart.api.controller") // 检查哪一个目录里
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)  //所有错误
    @ResponseBody   //返回json串
    public Result error(CustomException e)  {
        e.printStackTrace();
        return Result.error(e.getCode(), e.getMsg());
    }
}
