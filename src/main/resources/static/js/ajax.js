var $;

layui.use('jquery',function () {
    $=layui.$;
});

var ajax_post_json=function (url,data,callback) {
    $.ajax({
        type:'post',
        url:url,
        dataType:'json',
        data:data,
        contentType:'application/json',
        success:callback,
        error:function () {
            notice_error('操作失败');
        }
    })
};