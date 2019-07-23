$(document).ready(function () {

    load(1, $("#pageSize").find("option:selected").val(), {});
    $("#pageSize").change(function () {
        load(1, $(this).val(), searchVo());
    });
    $(document).on("click", "a[page]", function () {
        if (!$(this).parent().hasClass("disabled") && !$(this).parent().hasClass("active")) {
            load($(this).attr("page"), $("#pageSize").val(), searchVo());
        }
    });
    $("#add").click(function () {
        $("#myModalLabel").text("物流信息发货");
        $('#myModal').modal();
        $("#txt_id").val("");
    });

    $("#txt_ems_submit").click(function () {
        uploadEms(new FormData($("#txt_ems_form")[0]), "ems_file");
    });

    $("#import_ems").click(function () {
        $('#upload_logistics').modal();
    });

    $("#btn_submit").click(function () {
        if (isNotNull($("#txt_id").val())) {
            update(getVal());
            load($("li.paginate_button.active").find("a").text(), $("#pageSize").find("option:selected").val(), searchVo());
        } else {
            save(getVal());
            load($("li.paginate_button.active").find("a").text(), $("#pageSize").find("option:selected").val(), searchVo());
        }
    });


    $("#export_ems_style").click(function () {
        if ($("#search_deliveryStatus").val() != 0) {
            $("#search_deliveryStatus").val(0);
        }
        $("#searchForm").submit();
    });

    $("#search_btn").click(function () {
        load(1, $("#pageSize").find("option:selected").val(), searchVo());
    });

    $.fn.datetimepicker.dates['zh'] = {
        days: ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"],
        daysShort: ["日", "一", "二", "三", "四", "五", "六", "日"],
        daysMin: ["日", "一", "二", "三", "四", "五", "六", "日"],
        months: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
        monthsShort: ["一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"],
        meridiem: ["上午", "下午"],
        //suffix:      ["st", "nd", "rd", "th"],
        today: "今天"
    };
    $("#search_buyStartTime").datetimepicker({
        language: 'zh',
        minView: "month",
        format: "yyyy-mm-dd",
        autoclose: true,
        todayBtn: true
    });
    $("#search_buyEndTime").datetimepicker({
        language: 'zh',
        minView: "month",
        format: "yyyy-mm-dd",
        autoclose: true,
        todayBtn: true
    });

    $(document).on("click", "#listUpdate", function () {
        updateClick($(this).attr("update"));
    });

    $(document).on("click", "#listDelete", function () {
        delBtnClick($(this).attr("delete"));
    });

    $(document).on("click", "#listDelivery", function () {
        bootbox.confirm("您确认要发货吗?", function (result) {
            if (result) {
                delivery($("#listDelivery").attr("delivery"));
            }
        });
    })
});

var uploading = false;

function uploadEms(form, id) {
    if (uploading) {
        bootbox.alert("有文件正在上传, 请稍后再上传新的文件");
        return;
    }
    $.ajax({
        url: "/v1/order/importEms",
        data: form,
        type: "post",
        dataType: "json",
        cache: false,//上传文件无需缓存
        processData: false,//用于对data参数进行序列化处理 这里必须false
        contentType: false, //必须
        beforeSend: function () {
            uploading = true;
        },
        success: function (result) {
            if (result.code == 0) {
                bootbox.alert("上传完成.");
            }
            uploading = false;
        },
    })
}

function delivery(orderSn) {
    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/v1/order/business-delivery/' + orderSn,
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                setVal(data.data);
            }
        },
        error: error
    });
}


function searchVo() {
    var searchVo = {};
    searchVo.orderSn = $("#search_orderSn").val();
    searchVo.partnerSn = $("#search_partnerSn").val();
    searchVo.receiverMobile = $("#search_receiverMobile").val();
    searchVo.buyStartTime = $("#search_buyStartTime").val();
    searchVo.buyEndTime = $("#search_buyEndTime").val();
    searchVo.deliveryStatus = $("#search_deliveryStatus").val();
    searchVo.partnerName = $("#search_partnerName").val();
    searchVo.status = $("#search_status").val();
    return searchVo;
}

function updateClick(id) {
    $("#myModalLabel").text("物流信息发货");
    $('#myModal').modal();
    $("#txt_id").val(id);
    get($("#txt_id").val());
}

function delBtnClick(id) {
    bootbox.confirm("确定要删除当前订单么", function (result) {
        if (result) {
            del(id);
            load($("li.paginate_button.active").find("a").text(), $("#pageSize").find("option:selected").val(), searchVo());
        }
    })
}

function getVal() {
    var content = {};
    content.orderSn = $("#txt_id").val();
    content.logistics = $("#txt_logistics").val();
    content.logisticsSn = $("#txt_logisticsSn").val();
    content.logisticsStatus = $("#txt_logisticsStatus").val();
    content.logisticsAmount = $("#txt_logisticsAmount").val();
    return content;
}

function setVal(data) {
    $("#txt_id").val(data.orderSn);
    $("#txt_logistics").val(data.logistics);
    $("#txt_logisticsSn").val(data.logisticsSn);
    $("#txt_logisticsStatus").val(data.logisticsStatus);
    $("#txt_logisticsAmount").val(data.logisticsAmount);
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

function save(data) {
    $.ajax({
        async: false,
        type: "POST",
        contentType: "application/json; charset=utf-8",
        url: '/v1/content/partner/create',
        data: JSON.stringify(data),
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                bootbox.alert("创建成功!");
            }
        },
        error: error
    });
}

function update(data) {
    $.ajax({
        async: false,
        type: "POST",
        contentType: "application/json; charset=utf-8",
        url: '/v1/order/business-delivery',
        data: JSON.stringify(data),
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                bootbox.alert("修改成功!");
            }
        },
        error: error
    });
}

function del(id) {
    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/v1/content/partner/delete/' + id,
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                bootbox.alert("删除成功!");
            } else {
                bootbox.alert(data.msg);
            }
        },
        error: error
    });
}

function load(currentPage, pageSize, search) {
    $.ajax({
        async: false,
        type: "POST",
        contentType: "application/json; charset=utf-8",
        url: '/v1/order/search?page=' + currentPage + "&pageSize=" + pageSize,
        data: JSON.stringify(search, jsonReplacer),
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                $("#body").html("");
                if (isNotNull(data.data.data)) {
                    var ab = "";
                    data.data.data.forEach(function (val, index) {
                        ab = ab + writeData(["<a href='orders-detail.html?menuName=orders&id=" + val.orderSn + "'>" + val.partnerSn + "</a>", val.orderSn,
                            val.amount, outStr(val.ordersLogisticsResult.logistics),
                            outStr(val.ordersLogisticsResult.logisticsSn),
                            outStr(val.ordersLogisticsResult.receiver),
                            outStr(val.ordersLogisticsResult.receiverMobile),
                            outStr(val.buyTimeStr),
                            outStr(val.confirmTimeStr),
                            outStr(val.statusStr), "<a href='#' id='listUpdate' update='" + val.orderSn + "' class='btn btn-info btn-sm' >修改发货</a><a href='#' id='listDelivery' delivery='" + val.orderSn + "' class='btn btn-info btn-sm' >发货</a>"]);
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


