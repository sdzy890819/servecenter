package com.fdz.order.controller;

import com.fdz.common.web.version.ApiVersion;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiVersion("1")
@RequestMapping("/order")
@Api("提供给第三方接口")
public class OrdersController {

}
