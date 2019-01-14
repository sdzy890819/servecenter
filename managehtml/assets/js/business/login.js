$(document).ready(function () {

    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/webapi/user-info',
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                index();
            } else {
                bootbox.alert(data.msg);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            if (jqXHR.status != 401) {
                var responseData = jqXHR.responseText;
                bootbox.alert(responseData.msg);
            }
        }
    })

    $("#loginSubmit").click(function () {
        loginSubmit();
    })

    $("#pwd").keydown(function () {
        if (event.keyCode == 13) {
            loginSubmit();
        }
    })


});

function loginSubmit() {
    var userName = $("#userName").val();
    var pwd = $("#pwd").val();
    var remember = $("#remember").is(":checked");
    $.ajax({
        async: false,
        type: "POST",
        data: '{"userName":"' + userName + '","pwd":"' + pwd + '","remember":"' + remember + '"}',
        contentType: "application/json; charset=utf-8",
        url: '/webapi/login',
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                index();
            }else {
                bootbox.alert(data.msg);
            }
        },
        error: error
    });
}