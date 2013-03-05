package org.motechproject.tama.symptomreporting.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.alerts.contract.UpdateCriteria;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.contract.AlertService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.repository.AllAuditEvents;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientAlertSearchService;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.tama.symptomreporting.domain.SymptomReportingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class SymptomReportingAlertService {

    public static final String AUDIT_FORMAT = "Updating alert[%s] : setting [%s]";

    private AllPatients allPatients;
    private AlertService alertService;
    private PatientAlertSearchService patientAlertSearchService;
    private AllAuditEvents allAuditEvents;
    private PatientAlertService patientAlertService;
    private SymptomReportingProperties symptomReportingProperties;

    @Autowired
    public SymptomReportingAlertService(AllPatients allPatients, AlertService alertService, PatientAlertSearchService patientAlertSearchService, AllAuditEvents allAuditEvents, PatientAlertService patientAlertService, SymptomReportingProperties symptomReportingProperties) {
        this.allPatients = allPatients;
        this.alertService = alertService;
        this.patientAlertSearchService = patientAlertSearchService;
        this.allAuditEvents = allAuditEvents;
        this.patientAlertService = patientAlertService;
        this.symptomReportingProperties = symptomReportingProperties;
    }

    public void createSymptomsReportingAlert(String patientDocId) {
        Patient patient = allPatients.get(patientDocId);
        Map<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
        data.put(PatientAlert.PATIENT_CALL_PREFERENCE, patient.getPatientPreferences().getDisplayCallPreference());
        data.put(PatientAlert.ALERT_STATUS, TamaAlertStatus.Open.name());
        data.put(PatientAlert.CONNECTED_TO_DOCTOR, TAMAConstants.ReportedType.NA.name());
        alertService.create(patientDocId, "", "", AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, TAMAConstants.NO_ALERT_PRIORITY, data);
    }

    public void updateDoctorConnectedToDuringSymptomCall(String patientDocId, String doctorName) {
        PatientAlerts allAlerts = patientAlertSearchService.search(patientDocId);
        PatientAlert lastReportedAlert = allAlerts.lastSymptomReportedAlert();
        if (lastReportedAlert == null) return;
        Map<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.CONNECTED_TO_DOCTOR, TAMAConstants.ReportedType.Yes.toString());
        data.put(PatientAlert.DOCTOR_NAME, doctorName);
        patientAlertService.updateData(lastReportedAlert.getAlert(), data, patientDocId);
    }

    public void appendSymptomToAlert(String patientDocId, String symptomId) {
        PatientAlerts allAlerts = patientAlertSearchService.search(patientDocId);
        PatientAlert lastReportedAlert = allAlerts.lastSymptomReportedAlert();
        String description = lastReportedAlert.getDescription();
        String symptomDescription = symptomReportingProperties.symptomDescription(symptomId);
        String newDescription = StringUtils.isEmpty(description) ? symptomDescription : StringUtils.join(Arrays.asList(description, symptomDescription), ", ");
        UpdateCriteria updateCriteria = new UpdateCriteria().description(newDescription).status(org.motechproject.server.alerts.domain.AlertStatus.NEW);
        allAuditEvents.recordAlertEvent(patientDocId, String.format(AUDIT_FORMAT, lastReportedAlert.getAlertId(), updateCriteria));
        alertService.update(lastReportedAlert.getAlertId(), updateCriteria);
    }

    public void updateAdviceOnSymptomsReportingAlert(String patientDocId, String adviceGiven, int priority) {
        PatientAlerts allAlerts = patientAlertSearchService.search(patientDocId);
        PatientAlert lastReportedAlert = allAlerts.lastSymptomReportedAlert();
        UpdateCriteria updateCriteria = new UpdateCriteria().name(adviceGiven).status(org.motechproject.server.alerts.domain.AlertStatus.NEW).priority(priority);
        allAuditEvents.recordAlertEvent(patientDocId, String.format(AUDIT_FORMAT, lastReportedAlert.getAlertId(), updateCriteria));
        alertService.update(lastReportedAlert.getAlertId(), updateCriteria);
    }

    public void setConnectedToDoctorStatusOnSymptomsReportingAlert(String patientDocId, TAMAConstants.ReportedType reportedType) {
        PatientAlerts allAlerts = patientAlertSearchService.search(patientDocId);
        PatientAlert lastReportedAlert = allAlerts.lastSymptomReportedAlert();
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.CONNECTED_TO_DOCTOR, reportedType.name());
        UpdateCriteria updateCriteria = new UpdateCriteria().status(org.motechproject.server.alerts.domain.AlertStatus.NEW).data(data);
        allAuditEvents.recordAlertEvent(patientDocId, String.format(AUDIT_FORMAT, lastReportedAlert.getAlertId(), updateCriteria));
        alertService.update(lastReportedAlert.getAlertId(), updateCriteria);
    }
}