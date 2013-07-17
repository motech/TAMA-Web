function openAllPanels() {
    openPanel([basicInformationPanelWidget, medicalHistoryPanelWidget, preferencesPanelWidget]);
}

function warningWhenStartingOnFourDayRecall(event) {
    // The handler for the yes button in the dialog
    dialog.execute = function () {
        dojo.byId("patient").submit();
    };

    var isCallPreferenceDailyPillReminder = dojo.byId('dailyReminderCall').checked;
    if (!isCallPreferenceDailyPillReminder) {
        if(Spring.validateAll()){
            event.preventDefault();
            dialog.show();
        }
    }
}

function showMedicalHistory() {
    hideElement([fillAllButton]);
    closePanel([basicInformationPanelWidget]);
    openPanel([medicalHistoryPanelWidget]);
    hideElement([nextToMedicalHistoryButton]);
    showElement([medicalHistoryPanel, nextToPatientPreferencesButton]);
}

function showPatientPreferences() {
    closePanel([medicalHistoryPanelWidget]);
    openPanel([preferencesPanelWidget]);
    hideElement([nextToPatientPreferencesButton]);
    showElement([preferencesPanel, submitButtonDiv]);
}

  function validateMobileNumberUniqueness()
  {
        // get the form values

        var mobileNumber = dijit.byId('_mobilePhoneNumber_id').value;
        var codeExecuted = false;

        if(codeExecuted == false)
        {

        AjaxCall.get({
        url: "/tama/patients/validateMobileNumberUniqueness.json",
        content: {mobileNumber:mobileNumber},
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        sync: true,
        load: handleResults

        });
        }
  }

  function handleResults(response,ioArgs)
  {
        if(response.status == "FAIL")
        {
        if(Spring.validateAll()){
            codeExecuted=true;
            alert("Warning !   The phone number entered for the patient is already in use");
        }

        }
  }

function fillAllDetails() {
    hideElement([fillAllButton]);
    dijit.byId('_patientId_id').setValue('rand' + Math.random().toString().substring(2, 8));
    dijit.byId('_mobilePhoneNumber_id').setValue('0000' + Math.random().toString().substring(2, 8));
    dijit.byId('_dateOfBirthAsDate_id').attr('value', new Date('10/10/2010'));
    dijit.byId('_patientPreferences.passcode_id').setValue('1111');
}

dojo.addOnLoad(function() {
    basicInformationPanelWidget = dijit.byId('_title_fc_org_motechproject_tama_domain_patient_Basic_Information_id');
    medicalHistoryPanelWidget = dijit.byId('_title_fc_org_motechproject_tama_domain_patient_Medical_History_id');
    preferencesPanelWidget = dijit.byId('_title_fc_org_motechproject_tama_domain_patient_Preferences_id');
    preferencesPanel = dojo.byId('_title_fc_org_motechproject_tama_domain_patient_Preferences_id');
    medicalHistoryPanel = dojo.byId('_title_fc_org_motechproject_tama_domain_patient_Medical_History_id');
    nextToMedicalHistoryButton = dojo.byId('nextToMedicalHistorySpan');
    fillAllButton = dojo.byId('fillAllSpan');
    nextToPatientPreferencesButton = dojo.byId('nextToPatientPreferencesSpan');
    submitButtonDiv = dojo.byId('fc_org_motechproject_tama_domain_patient_submit');
    Spring.addDecoration(new Spring.ElementDecoration({elementId : 'patientPreferences.fourWeekWarningDialog', widgetType : 'dijit.Dialog', widgetAttrs : {}}));
    dialog = dijit.byId('patientPreferences.fourWeekWarningDialog');

    dojo.parser.parse();
    if (window.location.host.indexOf('localhost') < 0) {
        hideElement([fillAllButton]);
    }
    if (formHasErrors()) {
        hideElement([nextToMedicalHistoryButton]);
        hideElement([fillAllButton]);
        hideElement([nextToPatientPreferencesButton]);
        openAllPanels();
    }

    dojo.connect(dojo.byId('proceed'), 'onclick', openAllPanels);
    //dojo.connect(dojo.byId('proceed'), 'onclick', warningWhenStartingOnFourDayRecall);
    dojo.connect(dojo.byId('savePatient'), 'onclick', warningWhenStartingOnFourDayRecall);
    dojo.connect(dojo.byId('activatePatient'), 'onclick', warningWhenStartingOnFourDayRecall);

});

function encodePk(){
        return true;
}