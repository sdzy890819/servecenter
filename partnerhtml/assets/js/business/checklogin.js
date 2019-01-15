$(document).ready(function () {
    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/v1/content/partner/current-user',
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                $("#nickName").append(data.data.realName);
                $("#welcomeUserName").append(data.data.parentName);
            }
        },
        error: error
    });
    $("#logout").click(function () {
        $.ajax({
            async: false,
            type: "GET",
            contentType: "application/json; charset=utf-8",
            url: '/v1/content/partner/exit',
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
            url: '/v1/content/partner/update-pwd',
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


function loadProduct(ids) {
    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/v1/content/product/all',
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                ids.forEach(function (val, index) {
                    var partnerHtml = "<option value=''>请选择</option>";
                    if (isNotNull(data.data)) {
                        data.data.forEach(function (val2, index2) {
                            partnerHtml += "<option value='" + val2.id + "'>" + val2.productName + "</option>";
                        });
                    }
                    $("#" + val).html(partnerHtml);
                });
            }
        },
        error: error
    });
}