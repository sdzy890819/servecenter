package com.fdz.order.controller;

import com.fdz.common.aspect.ann.NotTracked;
import com.fdz.common.dto.SearchResult;
import com.fdz.common.security.SecurityUtils;
import com.fdz.common.utils.Page;
import com.fdz.common.utils.StringUtils;
import com.fdz.common.web.RestResponse;
import com.fdz.common.web.version.ApiVersion;
import com.fdz.order.domain.OrdersLogistics;
import com.fdz.order.dto.DeliveryInfo;
import com.fdz.order.dto.LogisticsDto;
import com.fdz.order.dto.OrdersResult;
import com.fdz.order.dto.SearchOrdersDto;
import com.fdz.order.service.OrderService;
import com.fdz.order.vo.EmsLogistics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        String partnerId = SecurityUtils.getCurrentLoginUserIdByPartner();
        if (StringUtils.isNotBlank(partnerId)) {
            ordersResult.setCostAmount(BigDecimal.ZERO);
        }
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


    @ApiOperation("订单结束")
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

    @ApiOperation("订单导出")
    @RequestMapping(value = "/exportEms", method = RequestMethod.POST)
    void exportEms(@RequestParam(value = "orderSn", required = false) String orderSn,
                   @RequestParam(value = "partnerSn", required = false) String partnerSn,
                   @RequestParam(value = "receiverMobile", required = false) String receiverMobile,
                   @RequestParam(value = "buyStartTime", required = false) String buyStartTime,
                   @RequestParam(value = "buyEndTime", required = false) String buyEndTime,
                   @RequestParam(value = "deliveryStatus", required = false, defaultValue = "0") Byte deliveryStatus,
                   @RequestParam(value = "partnerId", required = false) Long partnerId,
                   @RequestParam(value = "partnerName", required = false) String partnerName,
                   HttpServletResponse response) throws ParseException {
        SearchOrdersDto dto = new SearchOrdersDto();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isNotBlank(buyEndTime)) {
            dto.setBuyEndTime(sdf.parse(buyEndTime));
        }
        if (StringUtils.isNotBlank(buyStartTime)) {
            dto.setBuyStartTime(sdf.parse(buyStartTime));
        }
        if (deliveryStatus != 0) {
            deliveryStatus = 0;
        }
        dto.setDeliveryStatus(deliveryStatus);
        if (StringUtils.isNotBlank(orderSn)) {
            dto.setOrderSn(orderSn);
        }
        if (partnerId != null) {
            dto.setPartnerId(partnerId);
        }
        if (StringUtils.isNotBlank(partnerName)) {
            dto.setPartnerName(partnerName);
        }
        if (StringUtils.isNotBlank(partnerSn)) {
            dto.setPartnerSn(partnerSn);
        }
        if (StringUtils.isNotBlank(receiverMobile)) {
            dto.setReceiverMobile(receiverMobile);
        }
        List<EmsLogistics> emsLogistics = orderService.export(dto);
        response.setContentType("application/octet-stream;charset=ISO8859-1");
        response.setHeader("Content-disposition", "attachment; filename=" + new String(((StringUtils.isNotBlank(buyStartTime) ? buyStartTime : "2015-01-01") + "至" + (StringUtils.isNotBlank(buyEndTime) ? buyEndTime : sdf.format(new Date())) + "订单数据.xls").getBytes(), Charset.forName("ISO8859-1")));
        orderService.emsStyle(response, emsLogistics);
    }


    @ApiOperation("导入excel")
    @PostMapping("/importEms")
    @NotTracked
    RestResponse upload(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        orderService.importEms(file);
        return RestResponse.success(null);
    }

}
