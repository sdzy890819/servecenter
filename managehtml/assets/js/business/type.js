$(document).ready(function () {

    loadProductTypeNo();
    $("#add").click(function () {
        $("#myModalLabel").text("产品分类新增");
        $('#myModal').modal();
        $("#txt_id").val("");
    });


    $("#btn_submit").click(function () {
        if (isNotNull($("#txt_id").val())) {
            update(getVal());
            loadProductTypeNo();
        } else {
            save(getVal());
            loadProductTypeNo();
        }
    });

    $(document).on("click", "#listUpdate", function () {
        updateClick($(this).attr("update"));
    });

    $(document).on("click", "#listDelete", function () {
        delBtnClick($(this).attr("delete"));
    });
});


function updateClick(id) {
    $("#myModalLabel").text("产品分类修改");
    $('#myModal').modal();
    $("#txt_id").val(id);
    get($("#txt_id").val());
}

function delBtnClick(id) {
    bootbox.confirm("确定要删除当前产品分类吗？", function (result) {
        if (result) {
            del(id);
            loadProductTypeNo();
        }
    })
}

function getVal() {
    var content = {};
    content.id = $("#txt_id").val();
    content.productTypeName = $("#txt_productTypeName").val();
    content.sn = $("#txt_sn").val();
    return content;
}

function setVal(data) {
    $("#txt_id").val(data.id);
    $("#txt_productTypeName").val(data.productTypeName);
    $("#txt_sn").val(data.sn);
}

function loadTypes(data) {
    $("#body").html("");
    if (isNotNull(data)) {
        var ab = "";
        data.forEach(function (val, index) {
            ab = ab + writeData([val.sn, val.productTypeName,
                "<a href='#' id='listUpdate' update='" + val.id + "' class='btn btn-info btn-sm' >修改</a>" +
                "<a href='#' id='listDelete' delete='" + val.id + "' class='btn btn-danger btn-sm' >删除</a>"]);
        })
        $("#body").html(ab);
    }
}

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

function get(id) {
    $.ajax({
        async: false,
        type: "GET",
        contentType: "application/json; charset=utf-8",
        url: '/v1/content/product/type/detail/' + id,
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
        url: '/v1/content/product/type/create',
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
        url: '/v1/content/product/type/update',
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
        url: '/v1/content/product/type/delete/' + id,
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



