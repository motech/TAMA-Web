package org.motechproject.tama.patient.service;

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

import java.util.*;

@Component
public class PatientAlertService {

    public final static String RED_ALERT_MESSAGE_NO_RESPONSE = "No response was recorded";

    private AllPatients allPatients;
    private AlertService alertService;
    private PatientAlertSearchService patientAlertSearchService;
    private Logger logger = Logger.getLogger(PatientAlertService.class);

    @Autowired
    public PatientAlertService(AllPatients allPatients, AlertService alertService) {
        this.allPatients = allPatients;
        this.alertService = alertService;
        this.patientAlertSearchService = new PatientAlertSearchService(alertService);
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
        PatientAlerts allAlerts = getAllAlertsBy(patientId);
        PatientAlert lastReportedAlert = allAlerts.lastSymptomReportedAlert();
        if (lastReportedAlert == null) return;
        alertService.setData(lastReportedAlert.getAlertId(), PatientAlert.CONNECTED_TO_DOCTOR, TAMAConstants.ReportedType.Yes.toString());
        alertService.setData(lastReportedAlert.getAlertId(), PatientAlert.DOCTOR_NAME, doctorName);
    }

    public PatientAlerts getReadAlertsFor(String clinicId, String patientId, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        return getAlertsOfSpecificTypeAndStatusAndDateRange(clinicId, patientId, AlertStatus.READ, patientAlertType, startDate, endDate);
    }

    public PatientAlerts getUnreadAlertsFor(String clinicId, String patientId, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        return getAlertsOfSpecificTypeAndStatusAndDateRange(clinicId, patientId, AlertStatus.NEW, patientAlertType, startDate, endDate);
    }

    public PatientAlerts getFallingAdherenceAlerts(String patientID, final DateTime startDate, final DateTime endDate) {
        PatientAlerts allAlerts = getAllAlertsBy(patientID);
        return allAlerts.filterByAlertTypeAndDateRange(PatientAlertType.FallingAdherence, startDate, endDate);
    }

    public PatientAlerts getAdherenceInRedAlerts(String patientID, final DateTime startDate, final DateTime endDate) {
        PatientAlerts allAlerts = getAllAlertsBy(patientID);
        return allAlerts.filterByAlertTypeAndDateRange(PatientAlertType.AdherenceInRed, startDate, endDate);
    }

    PatientAlerts getAllAlertsBy(final String patientId) {
        final ArrayList<Patient> patients = new ArrayList<Patient>() {{
            add(allPatients.get(patientId));
        }};
        return patientAlertSearchService.search(patients, null);
    }

    PatientAlerts getAlertsOfSpecificTypeAndStatusAndDateRange(String clinicId, String patientId, AlertStatus alertStatus, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        List<Patient> patients = Collections.emptyList();
        if (patientId != null) {
            Patient patient = allPatients.findByPatientIdAndClinicId(patientId, clinicId);
            if (patient != null) {
                patients = Arrays.asList(patient);
            }
        } else {
            patients = allPatients.findByClinic(clinicId);
        }
        PatientAlerts allAlerts = patientAlertSearchService.search(patients, alertStatus);
        return allAlerts.filterByAlertTypeAndDateRangeIfPresent(patientAlertType, startDate, endDate);
    }
}