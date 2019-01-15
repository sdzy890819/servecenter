package com.fdz.common.exception.handler;

import com.fdz.common.exception.BizException;
import com.fdz.common.exception.TimeOutException;
import com.fdz.common.web.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@ControllerAdvice(annotations = {RestController.class, Service.class, Component.class, Repository.class})
@ConditionalOnMissingBean(name = {"exceptionHandler"})
public class ExceptionHandlerAdvice {

    @ExceptionHandler({BizException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public RestResponse<?> handleBizException(BizException e) {
        log.error("自定义业务错误,", e);
        return RestResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({NullPointerException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public RestResponse<?> handleNullPointerException(NullPointerException e) {
        log.error("出现空错误,", e);
        return RestResponse.error("哎呀出现错误了, 好像您有东西未传递哦.");
    }

    @ExceptionHandler({TimeOutException.class})
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ResponseBody
    public RestResponse<?> handleTimeOutException(TimeOutException e) {
        log.error("超时错误,", e);
        return RestResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ResponseBody
    public RestResponse<?> handleIllegalArgumentExcetpion(IllegalArgumentException e) {
        log.error("参数错误,", e);
        return RestResponse.error(e.getMessage());
    }


    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public RestResponse<?> handleException(Exception e) {
        log.error("全局错误,", e);
        return RestResponse.error(e.getMessage());
    }

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ResponseBody
    public RestResponse<?> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        log.error("数据异常,", e);
        return RestResponse.error("数据已存在");
    }


}
