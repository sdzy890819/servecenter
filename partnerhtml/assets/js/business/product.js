$(document).ready(function () {

    load(1, $("#pageSize").find("option:selected").val(), {});
    loadProductTypeNo();
    $("#pageSize").change(function () {
        load(1, $(this).val());
    });
    $(document).on("click", "a[page]", function () {
        if (!$(this).parent().hasClass("disabled") && !$(this).parent().hasClass("active")) {
            load($(this).attr("page"), $("#pageSize").val(), searchVo());
        }
    });
    $("#add").click(function () {
        $("#myModalLabel").text("商品新增");
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

    $("#txt_productImage1").click(function () {
        clickImageAndUpload($(this));
    });

    $("#txt_productImage2").click(function () {
        clickImageAndUpload($(this));
    });

    $("#txt_productImage3").click(function () {
        clickImageAndUpload($(this));
    });

    $("#txt_productCoverImage").click(function () {
        clickImageAndUpload($(this));
    });
});
    function clickImageAndUpload(fileObj) {
        if (typeof (fileObj) == "undefined" || fileObj.size <= 0) {
            bootbox.alert("请选择图片");
            return;
        }
        var formData = new FormData();
        var file = fileObj;
        formData.append("file", file.files[0]);
        $.ajax({
            url: "/v1/content/upload/image",
            data: formData,
            type: "post",
            dataType: "json",
            cache: false,//上传文件无需缓存
            processData: false,//用于对data参数进行序列化处理 这里必须false
            contentType: false, //必须
            success: function (result) {
                if (result.code == 0) {
                    bootbox.alert("上传完成");
                    file.value = result.data;
                }
            },
        })
    }


    $("#search_btn").click(function () {
        load(1, $("#pageSize").find("option:selected").val(), searchVo());
    });

    $(document).on("click", "#listUpdate", function () {
        updateClick($(this).attr("update"));
    });

    $(document).on("click", "#listDelete", function () {
        delBtnClick($(this).attr("delete"));
    });


    function loadProductTypeNo() {
        $.ajax({
            async: false,
            type: "GET",
            contentType: "application/json; charset=utf-8",
            url: '/v1/content/product/type/all',
            dataType: 'json',
            success: function (data) {
                if (data.code == 0) {
                    loadTypes(data.data);
                }
            },
            error: error
        });
    }

    function loadTypes(data) {
        $("#txt_productTypeNo").html("<option value=''></option>");
        data.forEach(function (val, index) {
            $("#txt_productTypeNo").html("<option value='" + val.sn + "'>" + val.productTypeName + "</option>");
        });
    }

    function searchVo() {
        var searchVo = {};
        searchVo.productName = $("#search_productName").val();
        searchVo.status = $("#search_status").val();
        return searchVo;
    }

    function updateClick(id) {
        $("#myModalLabel").text("商品修改");
        $('#myModal').modal();
        $("#txt_id").val(id);
        get($("#txt_id").val());
    }

    function delBtnClick(id) {
        bootbox.confirm("确定要删除此商品么", function () {
            del(id);
            load($("li.paginate_button.active").find("a").text(), $("#pageSize").find("option:selected").val());
        })
    }

    function getVal() {
        var content = {};
        content.id = $("#txt_id").val();
        content.productName = $("#txt_productName").val();
        content.productDescription = $("#txt_productDescription").val();
        content.productCoverImage = $("#txt_productCoverImage").val();
        content.productTypeNo = $("#txt_productTypeNo").val();
        content.primeCosts = $("#txt_primeCosts").val();
        content.salePrice = $("#txt_salePrice").val();
        content.status = $("#txt_status").val();
        content.productModel = $("#txt_productModel").val();
        content.productImages = [];
        content.productImages[0] = $("#txt_productImages1").val();
        content.productImages[1] = $("#txt_productImages2").val();
        content.productImages[2] = $("#txt_productImages3").val();
        return content;
    }

    function setVal(data) {
        $("#txt_id").val(data.id);
        $("#txt_productName").val(data.productName);
        $("#txt_productDescription").val(data.productDescription);
        $("#txt_productCoverImage").val(data.productCoverImage);
        $("#txt_productTypeNo").val(data.productTypeNo);
        $("#txt_primeCosts").val(data.primeCosts);
        $("#txt_salePrice").val(data.salePrice);
        $("#txt_status").val(data.status);
        $("#txt_productModel").val(data.productModel);
        data.productImages.forEach(function (val, index) {
            $("#txt_productImage" + (index + 1) ).val(val.productImage);
        })
    }

    function get(id) {
        $.ajax({
            async: false,
            type: "GET",
            contentType: "application/json; charset=utf-8",
            url: '/v1/content/product/detail/' + id,
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
            url: '/v1/content/product/create',
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
            url: '/v1/content/product/update',
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
            url: '/v1/content/product/delete/' + id,
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
            url: '/v1/content/product/search?page=' + currentPage + "&pageSize=" + pageSize,
            data: JSON.stringify(search),
            dataType: 'json',
            success: function (data) {
                if (data.code == 0) {
                    if (isNotNull(data.data.data)) {
                        var ab = "";
                        data.data.data.forEach(function (val, index) {
                            ab = ab + writeData(["<img src='" + val.productCoverImage + "' width='100px'/>", val.productName, val.productModel, val.primeCosts, val.salePrice, val.status ? "已上架" : "已下架",
                                val.createTimeStr, "<a href='#' id='listUpdate' update='" + val.id + "' class='btn btn-info btn-sm' >修改</a><a href='#' id='listDelete' delete='\" + val.id + \"' class='btn btn-danger btn-sm' >删除</a>"]);
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


