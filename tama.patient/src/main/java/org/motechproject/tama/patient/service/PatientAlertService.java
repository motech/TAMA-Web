package org.motechproject.tama.patient.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.server.alerts.contract.AlertService;
import org.motechproject.server.alerts.contract.UpdateCriteria;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.tama.common.repository.AllAuditEvents;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class PatientAlertService {

    public final static String RED_ALERT_MESSAGE_NO_RESPONSE = "No response was recorded";
    public static final String AUDIT_FORMAT = "Updating alert[%s] : setting [%s]";

    private AllPatients allPatients;
    private AlertService alertService;
    private PatientAlertSearchService patientAlertSearchService;
    private Logger logger = Logger.getLogger(PatientAlertService.class);
    private AllAuditEvents allAuditEvents;

    @Autowired
    public PatientAlertService(AllPatients allPatients, AlertService alertService, PatientAlertSearchService patientAlertSearchService, AllAuditEvents allAuditEvents) {
        this.allPatients = allPatients;
        this.alertService = alertService;
        this.patientAlertSearchService = patientAlertSearchService;
        this.allAuditEvents = allAuditEvents;
    }

    public void createAlert(String externalId, Integer priority, String name, String description, PatientAlertType patientAlertType) {
        createAlert(externalId, priority, name, description, patientAlertType, new HashMap<String, String>());
    }

    public void createAlert(String patientDocId, Integer priority, String name, String description, PatientAlertType patientAlertType, Map<String, String> data) {
        data.put(PatientAlert.PATIENT_ALERT_TYPE, patientAlertType.name());
        Patient patient = allPatients.get(patientDocId);
        if (patient != null) {
            data.put(PatientAlert.PATIENT_CALL_PREFERENCE, patient.getPatientPreferences().getDisplayCallPreference());
        }
        if (PatientAlertType.SymptomReporting.equals(patientAlertType)) {
            data.put(PatientAlert.SYMPTOMS_ALERT_STATUS, TamaAlertStatus.Open.name());
        }
        alertService.create(patientDocId, name, description, AlertType.MEDIUM, AlertStatus.NEW, priority, data);
    }

    public PatientAlert readAlert(String alertId) {
        final Alert alert = alertService.get(alertId);
        return PatientAlert.newPatientAlert(alert, allPatients.get(alert.getExternalId()));
    }

    public boolean updateAlertData(String alertId, String alertStatus, String notes, String doctorsNotes, String patientAlertType, String userName) {
        Alert alert = alertService.get(alertId);

        try {
            Map<String, String> newData = new HashMap<String, String>();
            if (PatientAlertType.SymptomReporting.toString().equals(patientAlertType)) {
                addData(alert, PatientAlert.DOCTORS_NOTES, doctorsNotes, newData);
            }
            addData(alert, PatientAlert.NOTES, notes, newData);
            updateAlert(alert, userName, newData, alertStatus);
        } catch (RuntimeException e) {
            logger.error(e);
            return false;
        }
        return true;
    }

    public boolean updateAlertStatus(String alertId, String userName, String alertStatus) {
        Alert alert = alertService.get(alertId);

        try {
            updateAlert(alert, userName, alertStatus);
        } catch (Exception e) {
            logger.error(e);
            return false;
        }
        return true;
    }

    private void addData(Alert alert, String key, String value, Map<String, String> data) {
        if (!StringUtils.trimToEmpty(alert.getData().get(key)).equals(value)) {
            data.put(key, value);
        }
    }

    public void updateAlert(Alert alert, String userName, Map<String, String> data) {
        updateAlert(alert, userName, data, null);
    }

    public void updateAlert(Alert alert, String userName, String alertStatus) {
        updateAlert(alert, userName, new HashMap<String, String>(), alertStatus);
    }

    public void updateAlert(Alert alert, String userName, Map<String, String> data, String alertStatus) {
        AlertStatus status = TamaAlertStatus.Open.name().equals(alertStatus) ? AlertStatus.NEW : AlertStatus.READ;

        if (data.isEmpty() && (status.equals(alert.getStatus()) || StringUtils.isBlank(alertStatus)))
            return;

        UpdateCriteria updateCriteria = new UpdateCriteria();
        if (!data.isEmpty()) {
            updateCriteria.data(data);
        }
        if (!StringUtils.isBlank(alertStatus) && !status.equals(alert.getStatus())) {
            updateCriteria.status(status);
        }
        allAuditEvents.recordAlertEvent(userName, String.format(AUDIT_FORMAT, alert.getId(), updateCriteria));
        alertService.update(alert.getId(), updateCriteria);
    }

    public PatientAlerts getReadAlertsFor(String clinicId, String patientId, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        return getAlertsFor(clinicId, patientId, patientAlertType, startDate, endDate, AlertStatus.READ);
    }

    public PatientAlerts getUnreadAlertsFor(String clinicId, String patientId, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        return getAlertsFor(clinicId, patientId, patientAlertType, startDate, endDate, AlertStatus.NEW);
    }

    public PatientAlerts getAllAlertsFor(String clinicId, String patientId, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        return getAlertsFor(clinicId, patientId, patientAlertType, startDate, endDate, null);
    }

    public PatientAlerts getFallingAdherenceAlerts(String patientDocumentId, final DateTime startDate, final DateTime endDate) {
        PatientAlerts allAlerts = patientAlertSearchService.search(patientDocumentId, startDate, endDate, null);
        return allAlerts.filterByAlertType(PatientAlertType.FallingAdherence).sortByAlertStatusAndTimeOfAlert();
    }

    public PatientAlerts getAdherenceInRedAlerts(String patientDocumentId, final DateTime startDate, final DateTime endDate) {
        PatientAlerts allAlerts = patientAlertSearchService.search(patientDocumentId, startDate, endDate, null);
        return allAlerts.filterByAlertType(PatientAlertType.AdherenceInRed).sortByAlertStatusAndTimeOfAlert();
    }

    private PatientAlerts getAlertsFor(String clinicId, String patientId, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate, AlertStatus alertStatus) {
        Patient patient = patientId == null ? null : allPatients.findByPatientIdAndClinicId(patientId, clinicId);
        if (StringUtils.isNotEmpty(patientId) && patient == null) {
            return new PatientAlerts();
        }
        String patientDocId = StringUtils.isEmpty(patientId) ? null : patient.getId();
        PatientAlerts allAlerts = patientAlertSearchService.search(patientDocId, startDate, endDate, alertStatus);
        return allAlerts.filterByClinic(clinicId).filterByAlertType(patientAlertType).sortByAlertStatusAndTimeOfAlert();
    }

    public PatientAlerts getAlertsForParameters(String clinicId, String patientId, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        Patient patient = null;
        PatientAlerts allAlerts = new PatientAlerts();
        if (StringUtils.isNotEmpty(patientId) && clinicId != null) {
            patient = patientId == null ? null : allPatients.findByPatientIdAndClinicId(patientId, clinicId);
            if (StringUtils.isNotEmpty(patientId) && patient == null) {
                return new PatientAlerts();
            }
            String patientDocId = StringUtils.isEmpty(patientId) ? null : patient.getId();
            allAlerts = patientAlertSearchService.search(patientDocId, startDate, endDate, null);

            allAlerts = allAlerts.filterByClinic(clinicId).filterByAlertType(patientAlertType).sortByAlertStatusAndTimeOfAlert();
        } else if (clinicId != null && StringUtils.isEmpty(patientId)) {
            List<Patient> patients = allPatients.findByClinic(clinicId);
            for (Patient patientAlertObject : patients) {

                String patientDocId = patientAlertObject.getId();
                PatientAlerts patientAlerts = patientAlertSearchService.search(patientDocId, startDate, endDate, null);

                Iterator iterator = patientAlerts.iterator();
                while (iterator.hasNext()) {
                    allAlerts.add((PatientAlert) iterator.next());
                }

            }

            allAlerts = allAlerts.filterByAlertType(patientAlertType).sortByAlertStatusAndTimeOfAlert();
        }
        return allAlerts;
    }

    public PatientAlerts getAlertsForPatientIdAndDateRange(String clinicId, String patientId, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate, AlertStatus alertStatus) {
        List<Patient> patients = null;
        PatientAlerts allAlerts = new PatientAlerts();
        if (StringUtils.isNotEmpty(patientId) && CollectionUtils.isEmpty(patients)) {
            patients = allPatients.findAllByPatientId(patientId);
        } else if (StringUtils.isEmpty(patientId)) {
            patients = allPatients.getAll();
        }
        for (Patient patient : patients) {
            String patientDocId = patient.getId();
            if (patientDocId != null) {
                PatientAlerts patientAlerts = patientAlertSearchService.search(patientDocId, startDate, endDate, alertStatus);
                if (!patientAlerts.isEmpty()) {
                    Iterator iterator = patientAlerts.iterator();
                    while (iterator.hasNext()) {
                        allAlerts.add((PatientAlert) iterator.next());
                    }
                }
            }
        }
        return allAlerts.filterByAlertType(patientAlertType).sortByAlertStatusAndTimeOfAlert();
    }


}