function orderDetail(data) {

    $("#showPartnerSn").html(data.partnerSn);
    $("#showOrderTime").html(data.buyTimeStr);
    $("#showReceiver").html(data.ordersLogisticsResult.receiver);
    $("#showReceiverMobile").html(data.ordersLogisticsResult.receiverMobile);
    $("#showReceiverAddress").html(data.ordersLogisticsResult.receiverAddress);
    var body = "";
    if (data.ordersProductResults != null) {
        data.ordersProductResults.forEach(function (val) {
            body += writeData([val.productId, val.productName, val.productModel, val.productTypeName, val.salePrice, val.platformPrice, val.productNum]);
        });
    }
    $("#productShow").html(body);


}

function getOrderDetail(id) {
    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/v1/order/detail/' + id,
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                orderDetail(data.data);
            }
        },
        error: error
    });
}


