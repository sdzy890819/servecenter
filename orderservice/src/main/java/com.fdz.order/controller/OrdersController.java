package com.fdz.order.controller;

import com.fdz.common.dto.SearchResult;
import com.fdz.common.security.SecurityUtils;
import com.fdz.common.utils.Page;
import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.order.domain.OrdersLogistics;
import com.fdz.order.dto.DeliveryInfo;
import com.fdz.order.dto.LogisticsDto;
import com.fdz.order.dto.OrdersResult;
import com.fdz.order.dto.SearchOrdersDto;
import com.fdz.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@ApiVersion("1")
@RequestMapping("/order")
@Api("订单信息")
public class OrdersController {

    @Resource
    private OrderService orderService;

    @ApiOperation("搜索订单")
    @PostMapping("/search")
    RestResponse<SearchResult<List<OrdersResult>>> search(@RequestBody SearchOrdersDto dto,
                                                          @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                          @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        Page pageObj = new Page(page, pageSize);
        List<OrdersResult> results = orderService.searchOrders(dto, pageObj);
        SearchResult<List<OrdersResult>> data = new SearchResult<>();
        data.setPage(pageObj);
        data.setData(results);
        data.setSearchCondition(dto);
        return RestResponse.success(data);
    }

    @ApiOperation("Partner搜索订单")
    @PostMapping("/partner/search")
    RestResponse<SearchResult<List<OrdersResult>>> partnerSearch(@RequestBody SearchOrdersDto dto,
                                                                 @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                                                 @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        Long partnerId = SecurityUtils.checkLoginAndGetUserByPartner();
        if (dto == null) {
            dto = new SearchOrdersDto();
        }
        dto.setPartnerId(partnerId);
        return search(dto, page, pageSize);
    }

    @ApiOperation("订单详情")
    @GetMapping("/detail/{orderSn}")
    RestResponse<OrdersResult> detail(@PathVariable("orderSn") String orderSn) {
        OrdersResult ordersResult = orderService.findOrdersResult(orderSn);
        return RestResponse.success(ordersResult);
    }

    @ApiOperation("物流详情")
    @GetMapping("/logistics/{orderSn}")
    RestResponse<OrdersLogistics> logistics(@PathVariable("orderSn") String orderSn) {
        OrdersLogistics ordersLogistics = orderService.findOrdersLogisticsByOrderSn(orderSn);
        return RestResponse.success(ordersLogistics);
    }

    @ApiOperation("商家确认发货")
    @PostMapping("/business-delivery")
    RestResponse businessDelivery(@RequestBody LogisticsDto dto) {
        orderService.businessDelivery(dto);
        return RestResponse.success(null);
    }

    @ApiOperation("商家确认发货")
    @GetMapping("/business-delivery/{orderSn}")
    RestResponse businessDelivery(@PathVariable("orderSn") String orderSn) {
        orderService.businessDelivery(orderSn);
        return RestResponse.success(null);
    }


    @ApiOperation("确认收货")
    @GetMapping("/receive/{orderSn}")
    RestResponse receive(@PathVariable("orderSn") String orderSn) {
        orderService.receive(orderSn);
        return RestResponse.success(null);
    }

    @ApiOperation("最近几天的订单信息")
    @GetMapping("/delivery-info/{days}")
    RestResponse<List<DeliveryInfo>> deliveryInfo(@PathVariable("days") Integer days) {
        List<DeliveryInfo> list = orderService.statistics(days);
        return RestResponse.success(list);
    }

}
