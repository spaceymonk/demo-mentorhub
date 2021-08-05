let showToast = function (msgType = "bg-info", msgText = "Done.") {
    $('.toast').removeClass (function (index, className) {
        return (className.match (/(^|\s)bg-\S+/g) || []).join(' ');
    });
    $('.toast').addClass(msgType);
    $('.toast-body').text(msgText);
    $('.toast').toast('show');
}