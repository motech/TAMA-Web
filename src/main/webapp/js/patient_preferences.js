dojo.addOnLoad(function() {

    var selectDayContainer = dojo.byId('_patientPreferencesDayOfWeeklyCall_id');
    var selectDayElementWidget = dijit.byId('_patientPreferences.dayOfWeeklyCall_id');

    var toggleSelectDayElement = function(element) {
        if (element.value == 'Weekly'){
            selectDayElementWidget.set('required', true);;
            showElement([selectDayContainer]);
        } else {
            selectDayElementWidget.setValue('');
            selectDayElementWidget.set('required', false);;
            hideElement([selectDayContainer]);
        }
    }

    dojo.forEach(dojo.query(".daily_reminder input[type='radio']"), function(element, index) {
        dojo.connect(element, "onclick", function() {
            toggleSelectDayElement(element);
        });
    });

    toggleSelectDayElement(dojo.query(".daily_reminder input[type='radio'][checked='checked']")[0]);
});