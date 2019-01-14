$(document).ready(function () {

    get(getQueryString()['id']);

    function setVal(data) {
        var dataMap = [];
        var tmp = {};
        tmp.key = "商品订单号";
        tmp.val = data.orderSn;
        dataMap[0] = tmp;
        var tmp2 = {};
        tmp2.key = "机构订单号";
        tmp2.val = data.partnerSn;
        dataMap[1] = tmp2;
        var tmp3 = {};
        tmp3.key = "金额";
        tmp3.val = data.amount;
        dataMap[2] = tmp3;
        var tmp4 = {};
        tmp4.key = "购买时间";
        tmp4.val = data.buyTimeStr;
        dataMap[3] = tmp4;
        var tmp5 = {};
        tmp5.key = "商家发货时间";
        tmp5.val = data.businessDeliveryTimeStr;
        dataMap[4] = tmp5;
        var tmp6 = {};
        tmp6.key = "确认时间";
        tmp6.val = data.confirmTime;
        dataMap[5] = tmp6;
        var tmp7 = {};
        tmp7.key = "签收时间";
        tmp7.val = data.endTime;
        dataMap[6] = tmp7;

        var tmp8 = {};
        tmp8.key = "发货状态";
        tmp8.val = data.deliveryStatus;
        dataMap[7] = tmp8;

        var tmp9 = {};
        tmp9.key = "订单状态";
        tmp9.val = data.statusStr;
        dataMap[8] = tmp9;

        var tmp10 = {};
        tmp10.key = "收货人";
        tmp10.val = data.ordersLogisticsResult.receiver;
        dataMap[9] = tmp10;

        var tmp11 = {};
        tmp11.key = "收货人地址";
        tmp11.val = data.ordersLogisticsResult.receiverAddress;
        dataMap[10] = tmp11;
        var tmp12 = {};
        tmp12.key = "收货人联系电话";
        tmp12.val = data.ordersLogisticsResult.receiverMobile;
        dataMap[11] = tmp12;

        var tmp13 = {};
        tmp13.key = "物流";
        tmp13.val = data.ordersLogisticsResult.logistics;
        dataMap[12] = tmp13;

        var tmp14 = {};
        tmp14.key = "物流单号";
        tmp14.val = data.ordersLogisticsResult.logisticsSn;
        dataMap[13] = tmp14;

        var tmp15 = {};
        tmp15.key = "物流状态";
        tmp15.val = data.ordersLogisticsResult.logisticsStatus;
        dataMap[14] = tmp15;

        var tmp16 = {};
        tmp16.key = "发货状态";
        tmp16.val = data.ordersLogisticsResult.deliveryStatus;
        dataMap[15] = tmp16;

        var tmp17 = {};
        tmp17.key = "商家发货状态";
        tmp17.val = data.ordersLogisticsResult.businessDeliveryStatus;
        dataMap[16] = tmp17;

        if (data.ordersProductResults != null) {
            data.ordersProductResults.forEach(function (val, index) {
                var tmp18 = {};
                tmp18.key = "产品名称";
                tmp18.val = val.productName;
                dataMap[17] = tmp18;

                var tmp19 = {};
                tmp19.key = "产品数量";
                tmp19.val = val.productNum;
                dataMap[18] = tmp19;

                var tmp20 = {};
                tmp20.key = "产品类型";
                tmp20.val = val.productTypeName;
                dataMap[19] = tmp20;

                var tmp21 = {};
                tmp21.key = "产品封面";
                tmp21.val = val.productCoverImage;
                dataMap[20] = tmp21;

                var tmp22 = {};
                tmp22.key = "销售价格";
                tmp22.val = val.salePrice;
                dataMap[21] = tmp22;

                var tmp23 = {};
                tmp23.key = "产品型号";
                tmp23.val = val.productModel;
                dataMap[22] = tmp23;

                var tmp24 = {};
                tmp24.key = "产品自定销售价格";
                tmp24.val = val.productSalePrice;
                dataMap[23] = tmp24;
            });
        }

        writeDataByMap(dataMap);
    }

    function get(id) {
        $.ajax({
            async: false,
            type: "GET",
            contentType: "application/json; charset=utf-8",
            url: '/v1/order/logistics/' + id,
            dataType: 'json',
            success: function (data) {
                if (data.code == 0) {
                    setVal(data.data);
                }
            },
            error: error
        });
    }

});


