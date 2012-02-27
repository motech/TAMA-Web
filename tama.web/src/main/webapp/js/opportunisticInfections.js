dojo.addOnLoad(function() {
    var otherCheckbox = dojo.byId('otherOpportunisticInfection');

    var displayDescriptionIfSelected = function () {
        var otherDescriptionDiv = dojo.byId('otherOpportunisticInfectionInfo');
        var otherDescriptionWidget = dijit.byId('_opportunisticInfections.otherOpportunisticInfectionDetails_id');
        if (otherCheckbox.checked) {
            setRequiredForElement(otherDescriptionWidget, true);
            showElement([otherDescriptionDiv]);
        }
        else {
            setRequiredForElement(otherDescriptionWidget, false);
            hideElement([otherDescriptionDiv]);

        }
    }
    dojo.connect(otherCheckbox, "onclick", displayDescriptionIfSelected);
    displayDescriptionIfSelected();
});