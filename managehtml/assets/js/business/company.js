$(document).ready(function () {
    load(1, $("#pageSize").find("option:selected").val());
    $("#pageSize").change(function () {
        load(1, $(this).val());
    });
    $(document).on("click","a[page]",function(){
        if (!$(this).parent().hasClass("disabled") && !$(this).parent().hasClass("active")) {
            load($(this).attr("page"), $("#pageSize").val());
        }
    });
    $("#add").click(function () {
        $("#myModalLabel").text("新增公司");
        $('#myModal').modal();
        $("#txt_id").val("");
    });


    $("#btn_submit").click(function () {
        if (isNotNull($("#txt_id").val())) {
            update(getVal());
            load($("li.paginate_button.active").find("a").text(), $("#pageSize").find("option:selected").val());
        } else {
            save(getVal());
            load($("li.paginate_button.active").find("a").text(), $("#pageSize").find("option:selected").val());
        }
    });

});

function updateClick(id) {
    $("#myModalLabel").text("修改公司");
    $('#myModal').modal();
    $("#txt_id").val(id);
    get($("#txt_id").val());
}

function delBtnClick(id) {
    bootbox.confirm("确定要删除此公司么", function () {
        del(id);
        load($("li.paginate_button.active").find("a").text(), $("#pageSize").find("option:selected").val());
    })
}

function getVal() {
    var content = {};
    content.admin = $("#txt_admin").val();
    content.companyName = $("#txt_companyName").val();
    content.description = $("#txt_description").val();
    content.sendType = $("#txt_sendType").val();
    content.ip = $("#txt_ip").val();
    content.port = $("#txt_port").val();
    content.sendUser = $("#txt_sendUser").val();
    content.sendPwd = $("#txt_sendPwd").val();
    content.basePath = $("#txt_basePath").val();
    content.url = $("#txt_url").val();
    content.id = $("#txt_id").val();
    content.wxAppId = $("#txt_wxAppId").val();
    content.wxAppSecret = $("#txt_wxAppSecret").val();
    content.wxLogin = $("#txt_wxLogin").val();
    content.customLoginMode = $("#txt_customLoginMode").val();
    content.templatePath = $("#txt_templatePath").val();
    content.wxPay = $("#txt_wxPay").val();

    return content;
}

function setVal(data) {
    $("#txt_admin").val("" + data.admin);
    $("#txt_companyName").val(data.companyName);
    $("#txt_description").val(data.description);
    $("#txt_sendType").val(data.sendType);
    $("#txt_ip").val(data.ip);
    $("#txt_port").val(data.port);
    $("#txt_sendUser").val(data.sendUser);
    $("#txt_sendPwd").val(data.sendPwd);
    $("#txt_basePath").val(data.basePath);
    $("#txt_url").val(data.url);
    $("#txt_wxAppId").val(data.wxAppId);
    $("#txt_wxAppSecret").val(data.wxAppSecret);
    $("#txt_wxLogin").val(data.wxLogin + "");
    $("#txt_customLoginMode").val(data.customLoginMode);
    $("#txt_templatePath").val(data.templatePath);
    $("#txt_wxPay").val(data.wxPay);

}

function get(id) {
    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/webapi/company/' + id,
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
        url: '/webapi/company/create',
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
        url: '/webapi/company/update',
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
        url: '/webapi/company/delete/' + id,
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

function load(currentPage, pageSize) {
    var page = {};
    page.page = currentPage;
    page.pageSize = pageSize;
    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/webapi/company/list',
        data: page,
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                if (isNotNull(data.data.result)) {
                    var ab = "";
                    data.data.result.forEach(function (val, index) {
                        var className = "odd";
                        if (index % 2 == 0) {
                            className = "even";
                        }
                        ab = ab + "<tr class=\"gradeA " + className + "\">\n" +
                            "                                    <td class=\"sorting_1\">" + val.id + "</td>\n" +
                            "                                    <td class=\" \">" + val.companyName + "</td>\n" +
                            "                                    <td class=\" \">" + val.url + "</td>\n" +
                            "                                    <td class=\"center \">" + val.sendTypeText + "</td>\n" +
                            "                                    <td class=\"center \"><a href=\"user.html?menuName=user&companyId=" + val.id + "\" class=\"btn btn-info btn-sm\">用户</a>" +
                            "<a href=\"#\" class=\"btn btn-info btn-sm\" id=\"update\" onclick=\"updateClick(" + val.id + ")\" style=\"margin-left:5px\" >修改</a>" +
                            "<a href=\"#\" class=\"btn btn-danger btn-sm\" id=\"delBtn\" onclick=\"delBtnClick(" + val.id + ")\" style=\"margin-left:5px\" >删除</a></td>\n" +
                            "                                </tr>";
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


