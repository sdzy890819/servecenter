$(document).ready(function () {

    load(1, $("#pageSize").find("option:selected").val(), {});
    $("#pageSize").change(function () {
        load(1, $(this).val());
    });
    $(document).on("click", "a[page]", function () {
        if (!$(this).parent().hasClass("disabled") && !$(this).parent().hasClass("active")) {
            load($(this).attr("page"), $("#pageSize").val(), searchVo());
        }
    });
    $("#add").click(function () {
        $("#myModalLabel").text("合作伙伴新增");
        $('#myModal').modal();
        $("#txt_id").val("");
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

    $("#btn_submit2").click(function () {
        if (isNotNull($("#txt2_password").val())) {
            saveUser(getUserVal());
        } else {
            bootbox.alert("保存的时候密码不可以为空，填写的密码会自动修改");
        }

    });


    $("#search_btn").click(function () {
        load(1, $("#pageSize").find("option:selected").val(), searchVo());
    });

    $(document).on("click", "#listUpdate", function () {
        updateClick($(this).attr("update"));
    });

    $(document).on("click", "#listShowKey", function () {
        showClick($(this).attr("showKey"));
    });

    $(document).on("click", "#listUser", function () {
        userClick($(this).attr("user"));
    });

    $(document).on("click", "#listDelete", function () {
        delBtnClick($(this).attr("delete"));
    });
})

function showClick(id) {
    getData(id, showKey);
}

function showKey(data) {
    var html = "自生成公钥：" + data.myPublicKey;
    var html2 = "合作方公钥：" + data.publicKey;
    bootbox.alert({
        size: "large",
        title: html,
        message: html2,
        className: 'rubberBand animated'
    });
}

function searchVo() {
    var searchVo = {};
    searchVo.name = $("#search_name").val();
    searchVo.contacts = $("#search_contacts").val();
    searchVo.contactMobile = $("#search_contactMobile").val();
    return searchVo;
}

function userClick(id) {
    $("#myModalLabel2").text("机构用户-唯一,只可修改 不会增加");
    $('#myModal2').modal();
    $("#txt2_id").val(id);
    getUser($("#txt2_id").val());
}

function updateClick(id) {
    $("#myModalLabel").text("合作伙伴修改");
    $('#myModal').modal();
    $("#txt_id").val(id);
    get($("#txt_id").val());
}

function delBtnClick(id) {
    bootbox.confirm("确定要终止与此合作伙伴的合作么", function (result) {
        if (result) {
            del(id);
            load($("li.paginate_button.active").find("a").text(), $("#pageSize").find("option:selected").val(), searchVo());
        }
    })
}

function getVal() {
    var content = {};
    content.id = $("#txt_id").val();
    content.name = $("#txt_name").val();
    content.shortName = $("#txt_shortName").val();
    content.nature = $("#txt_nature").val();
    content.code = $("#txt_code").val();
    content.contacts = $("#txt_contacts").val();
    content.contactMobile = $("#txt_contactMobile").val();
    //content.serviceRate = $("#txt_serviceRate").val();
    content.publicKey = $("#txt_publicKey").val();
    return content;
}

function setVal(data) {
    $("#txt_id").val(data.id);
    $("#txt_name").val(data.name);
    $("#txt_shortName").val(data.shortName);
    $("#txt_nature").val(data.nature);
    $("#txt_code").val(data.code);
    $("#txt_contacts").val(data.contacts);
    $("#txt_contactMobile").val(data.contactMobile);
    //$("#txt_serviceRate").val(data.serviceRate);
    $("#txt_publicKey").val(data.publicKey);
}

function getUserVal() {
    var content = {};
    content.partnerId = $("#txt2_id").val();
    content.userName = $("#txt2_userName").val();
    content.password = $("#txt2_password").val();
    content.realName = $("#txt2_realName").val();
    return content;
}

function setUserVal(data) {
    $("#txt2_userName").val(data.userName);
    //$("#txt2_password").val(data.password);
    $("#txt2_realName").val(data.realName);
}

function saveUser(data) {
    $.ajax({
        async: false,
        type: "POST",
        contentType: "application/json; charset=utf-8",
        url: '/v1/content/partner/user/update',
        data: JSON.stringify(data, jsonReplacer),
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                bootbox.alert("创建成功!");
            }
        },
        error: error
    });
}

function getUser(id) {
    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/v1/content/partner/user/' + id,
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                setUserVal(data.data);
            }
        },
        error: error
    });
}

function get(id) {
    getData(id, setVal);
}

function getData(id, func) {
    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/v1/content/partner/id/' + id,
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                func(data.data);
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
        url: '/v1/content/partner/update',
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
        url: '/v1/content/partner/search?page=' + currentPage + "&pageSize=" + pageSize,
        data: JSON.stringify(search, jsonReplacer),
        dataType: 'json',
        success: function (data) {
            if (data.code == 0) {
                $("#body").html("");
                if (isNotNull(data.data.data)) {
                    var ab = "";
                    data.data.data.forEach(function (val, index) {
                        ab = ab + writeData([val.uniqueKey, val.name, val.contacts, val.contactMobile, val.natureStr,
                            val.createTimeStr,
                            "<a href='#' id='listUser' user='" + val.id + "' class='btn btn-info btn-sm' >用户</a>" +
                            "<a href='#' id='listShowKey' showKey='" + val.id + "' class='btn btn-info btn-sm' >查看密匙</a>" +
                            "<a href='#' id='listUpdate' update='" + val.id + "' class='btn btn-info btn-sm' >修改</a>" +
                            "<a href='#' id='listDelete' delete='" + val.id + "' class='btn btn-danger btn-sm' >删除</a>"]);
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



