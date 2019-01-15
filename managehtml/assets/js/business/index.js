$(document).ready(function () {
    load();
});



function load() {
    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/v1/order/delivery-info/7',
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                if (isNotNull(data.data.data)) {
                    var ab = "";
                    data.data.data.forEach(function (val, index) {
                        ab = ab + writeData([val.date, val.partnerName, val.waitDeliveryAndAmount, val.deliveryAndAmount]);
                    })
                    $("#body").html(ab);
                }
            }
        },
        error: error
    });
}



