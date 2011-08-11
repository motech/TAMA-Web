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

