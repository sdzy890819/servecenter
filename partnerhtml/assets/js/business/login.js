$(document).ready(function () {

    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/v1/content/partner/current-user',
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

    $("#login").click(function () {
        loginSubmit();
    })

    $("#login-password").keydown(function () {
        if (event.keyCode == 13) {
            loginSubmit();
        }
    })


});

function loginSubmit() {
    var userName = $("#login-username").val();
    var pwd = $("#login-password").val();
    $.ajax({
        async: false,
        type: "POST",
        data: '{"userName":"' + userName + '","password":"' + pwd + '"}',
        contentType: "application/json; charset=utf-8",
        url: '/v1/content/partner/login',
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