dojo.addOnLoad(function() {

    var selectDayContainer = dojo.byId('_patientPreferencesDayOfWeeklyCall_id');
    var selectDayElementWidget = dijit.byId('_patientPreferences.dayOfWeeklyCall_id');
    var bestCallTimeRadio = dojo.query('.best_call_time_radio')[0];
    var bestCallTimeYes = dojo.byId('bestCallTimeYes');
    var bestCallTimeNo = dojo.byId('bestCallTimeNo');
    var bestCallTimeContainer = dojo.query('.best_call_time')[0];
    var bestCallTimeElementWidget = dijit.byId('_patientPreferences.bestCallTime.timeOfDayAsString_id');
    var ampmElement = dojo.byId('ampm');

    var toggleSelectDayElement = function(element) {
        if (element.value == 'Weekly'){
            selectDayElementWidget.set('required', true);
            showElement([selectDayContainer]);
            bestCallTimeYes.click();
            bestCallTimeNo.disabled = true;
        } else {
            selectDayElementWidget.setValue('');
            selectDayElementWidget.set('required', false);
            hideElement([selectDayContainer]);
            bestCallTimeNo.disabled = false;
            if (bestCallTimeElementWidget.value == '')
                bestCallTimeNo.click();
            else
                bestCallTimeYes.click();
        }
    }

    var toggleBestCallElement = function(element) {
        if (element.value == 'Yes'){
            ampmElement.disabled = false;
            showElement([bestCallTimeContainer]);
            bestCallTimeElementWidget.set('required', true);
        } else {
            bestCallTimeElementWidget.setValue('');
            ampmElement.disabled = true;
            bestCallTimeElementWidget.set('required', false);
            hideElement([bestCallTimeContainer]);
        }
    }

    dojo.forEach(dojo.query(".daily_reminder input[type='radio']"), function(element, index) {
        dojo.connect(element, "onclick", function() {
            toggleSelectDayElement(element);
        });
    });

    dojo.forEach(dojo.query(".best_call_time_radio input[type='radio']"), function(element, index) {
        dojo.connect(element, "onclick", function() {
            toggleBestCallElement(element);
        });
    });

    toggleSelectDayElement(dojo.query(".daily_reminder input[type='radio'][checked='checked']")[0]);
});