function isNull(params) {
    return params == null || params == "" || params == undefined;
}

function isNotNull(params) {
    return params != null && params != "" && params != undefined;
}

function error(jqXHR, textStatus, errorThrown) {
    if (jqXHR.status != 401) {
        var responseData = JSON.parse(jqXHR.responseText);
        bootbox.alert(responseData.msg);
    } else {
        login();
    }
}

function login() {
    window.location.href = "/login.html";
}

function index() {
    window.location.href = "/index.html?menuName=index";
}

function getQueryString() {
    var qs = location.search.substr(1), // 获取url中"?"符后的字串
        args = {}, // 保存参数数据的对象
        items = qs.length ? qs.split("&") : [], // 取得每一个参数项,
        item = null,
        len = items.length;

    for (var i = 0; i < len; i++) {
        item = items[i].split("=");
        var name = decodeURIComponent(item[0]),
            value = decodeURIComponent(item[1]);
        if (name) {
            args[name] = value;
        }
    }
    return args;
}

function formatDateTime(inputTime) {
    var date = new Date(inputTime);
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    m = m < 10 ? ('0' + m) : m;
    var d = date.getDate();
    d = d < 10 ? ('0' + d) : d;
    var h = date.getHours();
    h = h < 10 ? ('0' + h) : h;
    var minute = date.getMinutes();
    var second = date.getSeconds();
    minute = minute < 10 ? ('0' + minute) : minute;
    second = second < 10 ? ('0' + second) : second;
    return y + '-' + m + '-' + d+' '+h+':'+minute+':'+second;
}