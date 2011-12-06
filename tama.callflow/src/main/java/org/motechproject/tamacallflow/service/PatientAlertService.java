package org.motechproject.tamacallflow.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.domain.*;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static java.util.Collections.reverseOrder;
import static org.hamcrest.CoreMatchers.equalTo;

@Component
public class PatientAlertService {

    private AllPatients allPatients;
    private AlertService alertService;
    private Logger logger = Logger.getLogger(PatientAlertService.class);

    @Autowired
    public PatientAlertService(AllPatients allPatients, AlertService alertService) {
        this.allPatients = allPatients;
        this.alertService = alertService;
    }

    public PatientAlert getPatientAlert(String alertId) {
        final Alert alert = selectUnique(alertService.getBy(null, null, null, null, 100),
                having(on(Alert.class).getId(),
                        equalTo(alertId)));

        alertService.changeStatus(alert.getId(), AlertStatus.READ);
        return PatientAlert.newPatientAlert(alert, allPatients.get(alert.getExternalId()));
    }

    public PatientAlerts getReadAlertsForClinic(String clinicId) {
        return new PatientAlerts(getAlerts(allPatients.findByClinic(clinicId), AlertStatus.READ));
    }

    public PatientAlerts getUnreadAlertsForClinic(String clinicId) {
        return new PatientAlerts(getAlerts(allPatients.findByClinic(clinicId), AlertStatus.NEW));
    }

    public PatientAlerts getAllAlertsBy(final String patientId) {
        final ArrayList<Patient> patients = new ArrayList<Patient>() {{
            add(allPatients.get(patientId));
        }};
        return new PatientAlerts(getAlerts(patients, null));
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
        final Alert symptomsAlert = new Alert(externalId, AlertType.MEDIUM, AlertStatus.NEW, priority, data);
        final DateTime now = DateUtil.now();
        symptomsAlert.setDateTime(now);
        symptomsAlert.setDescription(description);
        symptomsAlert.setName(name);
        alertService.createAlert(symptomsAlert);
    }

    private List<PatientAlert> getAlerts(List<Patient> patients, final AlertStatus alertStatus) {
        final Converter<Patient, List<PatientAlert>> patientListConverter = new Converter<Patient, List<PatientAlert>>() {
            @Override
            public List<PatientAlert> convert(final Patient patient) {
                final Converter<Alert, PatientAlert> alertPatientAlertConverter = new Converter<Alert, PatientAlert>() {
                    @Override
                    public PatientAlert convert(Alert alert) {
                        return PatientAlert.newPatientAlert(alert, patient);
                    }
                };
                return Lambda.convert(alertService.getBy(patient.getId(), null, alertStatus, null, 100), alertPatientAlertConverter);
            }
        };
        return sort(flatten(convert(patients, patientListConverter)), on(PatientAlert.class).getAlert().getDateTime(), reverseOrder());
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

    public PatientAlerts getFallingAdherenceAlerts(String patientID, final DateTime startDate, final DateTime endDate) {
        return getAlertsOfSpecificTypeAndForDateRange(patientID, PatientAlertType.FallingAdherence, startDate, endDate);
    }

    public PatientAlerts getAdherenceInRedAlerts(String patientID, final DateTime startDate, final DateTime endDate) {
        return getAlertsOfSpecificTypeAndForDateRange(patientID, PatientAlertType.AdherenceInRed, startDate, endDate);
    }

    private PatientAlerts getAlertsOfSpecificTypeAndForDateRange(String patientID, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        PatientAlerts allAlerts = getAllAlertsBy(patientID);
        ArrayList<PatientAlert> filteredAlerts = new ArrayList<PatientAlert>();
        CollectionUtils.select(allAlerts, getSelectorForAlertTypeAndDateRange(patientAlertType, startDate, endDate), filteredAlerts);
        return new PatientAlerts(filteredAlerts);
    }

    private Predicate getSelectorForAlertTypeAndDateRange(final PatientAlertType patientAlertType, final DateTime startDate, final DateTime endDate) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                PatientAlert patientAlert = (PatientAlert) o;
                DateTime alertTime = patientAlert.getAlert().getDateTime();
                boolean isOfRequiredAlertType = patientAlert.getAlert().getData() != null && patientAlertType.name().equals(patientAlert.getAlert().getData().get(PatientAlert.PATIENT_ALERT_TYPE));
                return isOfRequiredAlertType && alertTime.isAfter(startDate) && alertTime.isBefore(endDate);
            }
        };
    }
}