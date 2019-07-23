package com.fdz.order.controller;

import com.fdz.common.aspect.ann.Lock;
import com.fdz.common.web.RestResponse;
import com.fdz.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/job")
@Api("JOB信息")
public class JobController {

    @Resource
    private OrderService orderService;

    @ApiOperation("批量已收货接口")
    @GetMapping("/finish-order")
    @Lock(key = "FINISH_ORDER", unlock = false, unit = TimeUnit.DAYS)
    RestResponse finishOrder() throws IOException {
        orderService.orderReceive();
        return RestResponse.success("任务已经提交.开始执行");
    }


    @ApiOperation("批量取消接口")
    @GetMapping("/cancel_order")
    @Lock(key = "CANCAL_ORDER", unlock = false, unit = TimeUnit.DAYS)
    RestResponse cancelOrder() throws IOException {
        orderService.orderCancel();
        return RestResponse.success("任务已经提交.开始执行");
    }

}
