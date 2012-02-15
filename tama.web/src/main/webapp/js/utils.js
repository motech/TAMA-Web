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



