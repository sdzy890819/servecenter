package com.fdz.order.controller;

import com.fdz.common.aspect.ann.Lock;
import com.fdz.common.web.RestResponse;
import com.fdz.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
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
    @PostMapping("/finish_order")
    @Lock(key = "FINISH_ORDER", unlock = false, unit = TimeUnit.DAYS)
    RestResponse finishOrder() throws IOException {
        orderService.orderReceive();
        return RestResponse.success("任务已经提交.开始执行");
    }

}
