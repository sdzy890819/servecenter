$(document).ready(function () {

    get(getQueryString()['id']);


});

function setVal(data) {
    var dataMap = new Array(17);
    var tmp = {};
    tmp.key = "商品订单号";
    tmp.val = data.orderSn;
    dataMap.push(tmp);
    var tmp2 = {};
    tmp2.key = "机构订单号";
    tmp2.val = data.partnerSn;
    dataMap.push(tmp2);
    var tmp3 = {};
    tmp3.key = "金额";
    tmp3.val = data.amount;
    dataMap.push(tmp3);
    var tmp18 = {};
    tmp18.key = "进货金额";
    tmp18.val = data.costAmount;
    dataMap.push(tmp18);
    var tmp19 = {};
    tmp19.key = "平台销售金额";
    tmp19.val = data.platformAmount;
    dataMap.push(tmp19);
    var tmp20 = {};
    tmp20.key = "服务费金额";
    tmp20.val = data.infoAmount;
    dataMap.push(tmp20);
    var tmp4 = {};
    tmp4.key = "购买时间";
    tmp4.val = data.buyTimeStr;
    dataMap.push(tmp4);
    var tmp5 = {};
    tmp5.key = "商家发货时间";
    tmp5.val = data.businessDeliveryTimeStr;
    dataMap.push(tmp5);
    var tmp6 = {};
    tmp6.key = "确认时间";
    tmp6.val = data.confirmTimeStr;
    dataMap.push(tmp6);
    var tmp7 = {};
    tmp7.key = "签收时间";
    tmp7.val = data.endTimeStr;
    dataMap.push(tmp7);
    var tmp8 = {};
    tmp8.key = "发货状态";
    tmp8.val = data.deliveryStatusStr;
    dataMap.push(tmp8);

    var tmp9 = {};
    tmp9.key = "订单状态";
    tmp9.val = data.statusStr;
    dataMap.push(tmp9);

    var tmp10 = {};
    tmp10.key = "收货人";
    tmp10.val = data.ordersLogisticsResult.receiver;
    dataMap.push(tmp10);

    var tmp11 = {};
    tmp11.key = "收货人地址";
    tmp11.val = data.ordersLogisticsResult.receiverProvince + " " +
        data.ordersLogisticsResult.receiverCity + " " +
        data.ordersLogisticsResult.receiverArea + " " +
        data.ordersLogisticsResult.receiverAddress;
    dataMap.push(tmp11);
    var tmp12 = {};
    tmp12.key = "收货人联系电话";
    tmp12.val = data.ordersLogisticsResult.receiverMobile;
    dataMap.push(tmp12);

    var tmp13 = {};
    tmp13.key = "物流";
    tmp13.val = data.ordersLogisticsResult.logistics;
    dataMap.push(tmp13)

    var tmp14 = {};
    tmp14.key = "物流单号";
    tmp14.val = data.ordersLogisticsResult.logisticsSn;
    dataMap.push(tmp14);

    var tmp15 = {};
    tmp15.key = "物流状态";
    tmp15.val = data.ordersLogisticsResult.logisticsStatus;
    dataMap.push(tmp15);

    var tmp16 = {};
    tmp16.key = "发货状态";
    tmp16.val = data.ordersLogisticsResult.deliveryStatusStr;
    dataMap.push(tmp16);

    var tmp17 = {};
    tmp17.key = "商家发货状态";
    tmp17.val = data.ordersLogisticsResult.businessDeliveryStatusStr;
    dataMap.push(tmp17);
    $("#body").html(writeDataByMap(dataMap));
    var body = "";
    if (data.ordersProductResults != null) {
        data.ordersProductResults.forEach(function (val) {
            body += writeData([val.productId, val.productName, val.productModel, val.productTypeName, val.salePrice, val.productNum]);
        });
    }
    $("#body2").html(body);

}

function get(id) {
    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/v1/order/detail/' + id,
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                setVal(data.data);
            }
        },
        error: error
    });
}


