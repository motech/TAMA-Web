dojo.addOnLoad(function() {
    var otherCheckbox = dojo.byId('otherOpportunisticInfection');

    var displayDescriptionIfSelected = function () {
        var otherDescriptionDiv = dojo.byId('otherOpportunisticInfectionInfo');

        var otherDescriptionWidget;

        if(dijit.byId('_opportunisticInfectionsUIModel.otherDetails_id') != undefined)
            otherDescriptionWidget = dijit.byId('_opportunisticInfectionsUIModel.otherDetails_id');
        else
            otherDescriptionWidget = dijit.byId('_otherDetails_id');

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