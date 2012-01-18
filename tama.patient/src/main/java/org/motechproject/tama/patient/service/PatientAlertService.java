package org.motechproject.tama.patient.service;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PatientAlertService {

    public final static String RED_ALERT_MESSAGE_NO_RESPONSE = "No response was recorded";

    private AllPatients allPatients;
    private AlertService alertService;
    private PatientAlertSearchService patientAlertSearchService;
    private Logger logger = Logger.getLogger(PatientAlertService.class);

    @Autowired
    public PatientAlertService(AllPatients allPatients, AlertService alertService, PatientAlertSearchService patientAlertSearchService) {
        this.allPatients = allPatients;
        this.alertService = alertService;
        this.patientAlertSearchService = patientAlertSearchService;
    }

    public void createAlert(String externalId, Integer priority, String name, String description, PatientAlertType patientAlertType, Map<String, String> data) {
        data.put(PatientAlert.PATIENT_ALERT_TYPE, patientAlertType.name());
        Patient patient = allPatients.get(externalId);
        if (patient != null) {
            data.put(PatientAlert.PATIENT_CALL_PREFERENCE, patient.getPatientPreferences().getDisplayCallPreference());
        }
        if (PatientAlertType.SymptomReporting.equals(patientAlertType)) {
            data.put(PatientAlert.SYMPTOMS_ALERT_STATUS, SymptomsAlertStatus.Open.name());
        }
        alertService.create(externalId, name, description, AlertType.MEDIUM, AlertStatus.NEW, priority, data);
    }

    public PatientAlert readAlert(String alertId) {
        final Alert alert = alertService.get(alertId);
        alertService.changeStatus(alert.getId(), AlertStatus.READ);
        return PatientAlert.newPatientAlert(alert, allPatients.get(alert.getExternalId()));
    }

    public boolean updateAlert(String alertId, String symptomsAlertStatus, String notes, String doctorsNotes, String patientAlertType) {
        try {
            if (PatientAlertType.SymptomReporting.name().equals(patientAlertType)) {
                alertService.setData(alertId, PatientAlert.SYMPTOMS_ALERT_STATUS, symptomsAlertStatus);
                alertService.setData(alertId, PatientAlert.DOCTORS_NOTES, doctorsNotes);
            }
            alertService.setData(alertId, PatientAlert.NOTES, notes);
        } catch (RuntimeException e) {
            logger.error(e);
            return false;
        }
        return true;
    }

    public void updateDoctorConnectedToDuringSymptomCall(String patientId, String doctorName) {
        PatientAlerts allAlerts = patientAlertSearchService.search(patientId);
        PatientAlert lastReportedAlert = allAlerts.lastSymptomReportedAlert();
        if (lastReportedAlert == null) return;
        alertService.setData(lastReportedAlert.getAlertId(), PatientAlert.CONNECTED_TO_DOCTOR, TAMAConstants.ReportedType.Yes.toString());
        alertService.setData(lastReportedAlert.getAlertId(), PatientAlert.DOCTOR_NAME, doctorName);
    }

    public PatientAlerts getReadAlertsFor(String clinicId, String patientId, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        return getAlertsFor(clinicId, patientId, patientAlertType, startDate, endDate, AlertStatus.READ);
    }

    public PatientAlerts getUnreadAlertsFor(String clinicId, String patientId, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        return getAlertsFor(clinicId, patientId, patientAlertType, startDate, endDate, AlertStatus.NEW);
    }

    public PatientAlerts getFallingAdherenceAlerts(String patientDocumentId, final DateTime startDate, final DateTime endDate) {
        PatientAlerts allAlerts = patientAlertSearchService.search(patientDocumentId, startDate, endDate, null);
        return allAlerts.filterByAlertType(PatientAlertType.FallingAdherence);
    }

    public PatientAlerts getAdherenceInRedAlerts(String patientDocumentId, final DateTime startDate, final DateTime endDate) {
        PatientAlerts allAlerts = patientAlertSearchService.search(patientDocumentId, startDate, endDate, null);
        return allAlerts.filterByAlertType(PatientAlertType.AdherenceInRed);
    }

    private PatientAlerts getAlertsFor(String clinicId, String patientId, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate, AlertStatus alertStatus) {
        Patient patient = patientId == null ? null : allPatients.findByPatientIdAndClinicId(patientId, clinicId);
        if (StringUtils.isNotEmpty(patientId) && patient == null) {
            return new PatientAlerts();
        }
        String patientDocId = StringUtils.isEmpty(patientId) ? null : patient.getId();
        PatientAlerts allAlerts = patientAlertSearchService.search(patientDocId, startDate, endDate, alertStatus);
        return allAlerts.filterByClinic(clinicId).filterByAlertType(patientAlertType);
    }
}