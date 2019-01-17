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


    $("#search_btn").click(function () {
        load(1, $("#pageSize").find("option:selected").val(), searchVo());
    });

    $(document).on("click", "#listUpdate", function () {
        updateClick($(this).attr("update"));
    });

    $(document).on("click", "#listDelete", function () {
        delBtnClick($(this).attr("delete"));
    });
})

    function searchVo() {
        var searchVo = {};
        searchVo.name = $("#search_name").val();
        searchVo.contacts = $("#search_contacts").val();
        searchVo.contactMobile = $("#search_contactMobile").val();
        return searchVo;
    }

    function updateClick(id) {
        $("#myModalLabel").text("合作伙伴修改");
        $('#myModal').modal();
        $("#txt_id").val(id);
        get($("#txt_id").val());
    }

    function delBtnClick(id) {
        bootbox.confirm("确定要终止与此合作伙伴的合作么", function () {
            del(id);
            load($("li.paginate_button.active").find("a").text(), $("#pageSize").find("option:selected").val());
        })
    }

    function getVal() {
        var content = {};
        content.id = $("#txt_id").val();
        data.name = $("#txt_name").val();
        data.shortName = $("#txt_shortName").val();
        data.nature = $("#txt_nature").val();
        data.code = $("#txt_code").val();
        data.contacts = $("#txt_contacts").val();
        data.contactMobile = $("#txt_contactMobile").val();
        data.serviceRate = $("#txt_serviceRate").val();
        data.publicKey = $("#txt_publicKey").val();
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
        $("#txt_serviceRate").val(data.serviceRate);
        $("#txt_publicKey").val(data.publicKey);
    }

    function get(id) {
        $.ajax({
            async: false,
            type: "GET",
            contentType: "application/json; charset=utf-8",
            url: '/v1/content/partner/id/' + id,
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
            data: JSON.stringify(search),
            dataType: 'json',
            success: function (data) {
                if (data.code == 0) {
                    if (isNotNull(data.data.data)) {
                        var ab = "";
                        data.data.data.forEach(function (val, index) {
                            ab = ab + writeData([val.uniqueKey, val.name, val.contacts, val.contactMobile, val.natureStr, val.serviceRate,
                                val.createTimeStr,
                                "<a href='#' id='listUpdate' update='" + val.id + "' class='btn btn-info btn-sm' >修改</a>" +
                                "<a href='#' id='listDelete' delete='\" + val.id + \"' class='btn btn-danger btn-sm' >删除</a>"]);
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



