var notice;

layui.config({
    base: '/static/assets'
}).extend({
    notice: '/lib/notice'
}).use('notice', function () {
    notice = layui.notice;
});

var notice_success = function (text) {
    notice.init({
        type: 'default',
        title: text
    })
};

var notice_error = function (text) {
    notice.init({
        type: 'danger',
        title: text
    })
};