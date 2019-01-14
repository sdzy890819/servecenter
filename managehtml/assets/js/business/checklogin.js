$(document).ready(function () {
    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/webapi/user-info',
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                $("#nickName").append(data.data.companyName + "-" + data.data.nickName);
                $("#welcomeUserName").append(data.data.companyName + "【 " + data.data.companyKey + " 】" + ", 员工:" + data.data.nickName);
                $("#wangzhan_url").attr('href', data.data.url);
                $("#copyright_1").html(data.data.companyName);
                if (data.data.admin) {
                    $("#main-menu").append("<li><a href=\"company.html?menuName=company\" menuName=\"company\"><i class=\"fa fa-edit\"></i> 公司管理 </a></li>");
                }
                $.ajax({
                    async: false,
                    type: "GET",
                    contentType: "application/json; charset=utf-8",
                    url: '/webapi/column/all',
                    dataType: 'json',
                    success: function (data1) {
                        if (data1.code == 0) {
                            var list = data1.data;
                            $("#columnMenu").append("<li><a href=\"addColumn.html?menuName=column\"><i class=\"fa fa-edit\"></i>添加新栏目</a></li>");
                            list.forEach(function (val, index) {
                                $("#columnMenu").append("<li><a href=\"content.html?menuName=column&columnId=" + val.id + "&columnName=" + val.name + "\"><i class=\"fa fa-edit\"></i>" + val.name + "</a></li>");
                            })
                        } else {
                            alert(data.msg);
                        }
                    },
                    error: error
                });
            }
        },
        error: error
    });
    var menu = getQueryString()["menuName"];
    $('a[menuName|="' + menu + '"]').addClass("active-menu");
    $("#logout").click(function () {
        $.ajax({
            async: false,
            type: "GET",
            contentType: "application/json; charset=utf-8",
            url: '/webapi/exit',
            dataType: 'json',
            success: function (data) {
                if (data.code == 0) {
                    login();
                }
            },
            error: error
        });
    });

    $("#publicUpdateUser").click(function () {
        $("#publicUpdateUserModal").modal();
    });

    $("#publicUpdateUserButton").click(function () {
        var data = {};
        data.pwd = $("#txt_pwd").val();
        data.newPwd = $("#txt_newPwd").val();
        data.newPwd2 = $("#txt_newPwd2").val();
        $.ajax({
            async: false,
            type: "POST",
            contentType: "application/json; charset=utf-8",
            url: '/v1/content/manager/update-pwd',
            data: data,
            dataType: 'json',
            success: function (data) {
                if (data.code == 0) {
                    bootbox.alert("修改密码成功!");
                } else {
                    bootbox.alert(data.message);
                }
            },
            error: error
        });
    });
});

function writeData(data) {
    var html = "<tr>"
    if (isNotNull(data)) {
        data.forEach(function (val, index) {
            html = html + "<td class='center'>" + val + "</td>";
        })
    }
    html = html + "</tr>";
    return html;
}

function writeDataByMap(data) {
    var html = "";
    if (isNotNull(data)) {
        data.forEach(function (val, index) {
            html = "<tr>"
            html = html + "<td class='center'>" + val.key + "</td>";
            html = html + "<td class='center'>" + val.val + "</td>";
            html = html + "</tr>";
        })
    }
    return html;
}
