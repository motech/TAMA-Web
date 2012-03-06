var clearElement = function(_elementWidget) {
    if (_elementWidget != null)
        _elementWidget.setValue('');
}

var setRequiredForElement = function(_elementWidget, requiredValue) {
    if (_elementWidget != null)
        _elementWidget.set('required', requiredValue);
}

var showElement = function(_elementArray) {
    for (var i = 0; i < _elementArray.length; i++){
        if (_elementArray[i] != null)
            _elementArray[i].setAttribute('style', 'display:block');
    }
}

var hideElement = function(_elementArray) {
    for (var i = 0; i < _elementArray.length; i++){
        if (_elementArray[i] != null)
            _elementArray[i].setAttribute('style', 'display:none');
    }
}

var openPanel = function(_panelWidgetArray) {
    for (var i = 0; i < _panelWidgetArray.length; i++){
        var _panelWidget = _panelWidgetArray[i];
        if (_panelWidget.open == false)
            _panelWidget.toggle();
    }
}

var closePanel = function(_panelWidgetArray) {
    for (var i = 0; i < _panelWidgetArray.length; i++){
        var _panelWidget = _panelWidgetArray[i];
        if (_panelWidget.open == true)
            _panelWidget.toggle();
    }
}

function formHasErrors() {
    return dojo.query('.errors').length > 0;
}

var DateFormatter = function (date) {
    this.date = date;
}

DateFormatter.prototype = {
    zeroPad : function(n){
        return n>=10 ? n : '0'+n;
    },

    slashFormat: function(){
        return this.zeroPad(this.date.getDate()) + '/' + this.zeroPad(this.date.getMonth()+1) + '/' + this.date.getFullYear();
    },

    dashFormat: function(){
        return this.zeroPad(this.date.getDate()) + '-' + this.zeroPad(this.date.getMonth()+1) + '-' + this.date.getFullYear();
    }
}

var Banner = function(targetDivId){
    this.targetDivId = targetDivId;
    this.element = dojo.byId(targetDivId);
    this.defaultDisplay = this.element.style.display;
}

Banner.prototype = {
    hide: function(){
        this.element.style.display = "none";
    },

    show: function(){
        this.element.style.display = this.defaultDisplay;
    },

    setMessage: function(msg){
        this.element.innerHTML = msg;
        this.show();
    }
}


var AjaxCall = function() {}
AjaxCall.get = function(args) {
    dojo.xhrGet(AjaxCall.createCallParams(args));
}

AjaxCall.post = function(args) {
    dojo.xhrPost(AjaxCall.createCallParams(args));
}

AjaxCall.createCallParams = function(args) {
    return {
        url: args.url,
        content: args.content,
        handleAs: "json",
        load: function (jsonData, ioArgs) {
            if(typeof(args.load) == "function") { args.load(jsonData, ioArgs); }
        },
        error: function(result, ioArgs) {
            if(result.status==601) {
                window.location.reload();
            }
            if(typeof(args.error) == "function") { args.error(result, ioArgs); }
        },
        handle: function (result, ioArgs) {
            if(typeof(args.handle) == "function") { args.handle(result, ioArgs); }
        }
    }
}
