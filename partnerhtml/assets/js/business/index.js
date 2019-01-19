$(document).ready(function () {
    load(1, 20);
    loadPartnerInfo(setTableData);

});

function loadPartnerInfo(callback) {
    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/v1/order/payment-record/partner/account',
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                callback(data.data);
            }
        },
        error: error
    });
}

function setTableData(data) {
    $("#show_partnerName").html(data.partnerName);
    $("#show_contract").html(data.partnerName);
    $("#show_contractMobile").html(data.contractMobile);
    $("#show_amount").html(data.amount);
    $("#show_freezingAmount").html(data.freezingAmount);
}

function load(currentPage, pageSize) {
    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/v1/order/payment-record/partner/index?page=' + currentPage + "&pageSize=" + pageSize,
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                $("#body").html("");                     if (isNotNull(data.data.data)) {
                    var ab = "";
                    data.data.data.forEach(function (val, index) {
                        ab = ab + writeData([val.paySn,val.payTimeStr,val.paymentTypeStr,val.amount, val.payStatusStr]);
                    })
                    $("#body").html(ab);
                }
                if (isNotNull(data.data.page)) {
                    var curr = data.data.page.page;
                    var pageCount = data.data.page.pageCount;
                    var dis = "";
                    var nextDis = "";
                    if (curr == 1) {
                        dis = "disabled";
                    }
                    if (curr >= pageCount) {
                        nextDis = "disabled";
                    }

                    var pageHtml = "<li class=\"paginate_button previous " + dis + "\" tabindex=\"0\" id=\"dataTables-example_previous\">\n" +
                        "                                                    <a href=\"#\" page=\"" + (curr - 1) + "\">Previous</a></li>\n";
                    var pageSp = 10;
                    var half = 5;
                    var x = 1;
                    var y = x;

                    if (curr >= half && (curr + half) < pageCount) {
                        x = curr - half + 1;
                        y = curr + half;
                    } else if (curr >= half && (curr + half) >= pageCount) {
                        x = curr - half + 1;
                        y = pageCount;
                    } else if (curr < half && pageSp >= pageCount) {
                        y = pageCount;
                    } else if (curr < half && pageSp < pageCount) {
                        y = pageSp;
                    }
                    for (var i = x; i <= y; i++) {
                        var active = "";
                        if (curr == i) {
                            active = "active";
                        }
                        pageHtml = pageHtml +
                            "                                                <li class=\"paginate_button " + active + "\" tabindex=\"0\">\n" +
                            "                                                    <a href=\"#\" page=\"" + i + "\">" + i + "</a></li>\n";
                    }

                    pageHtml = pageHtml +
                        "                                                <li class=\"paginate_button next " + nextDis + "\" tabindex=\"0\" id=\"dataTables-example_next\">\n" +
                        "                                                    <a href=\"#\"page=\"" + (curr + 1) + "\">Next</a></li>";
                    $("#pageBar").html(pageHtml);
                    $("#dataTables-example_info").html("Showing " + data.data.page.start + " to " + (data.data.page.end >= data.data.page.count ? data.data.page.count : data.data.page.end) + " of " + data.data.page.count + " entries");
                }
            }
        },
        error: error
    });
}



