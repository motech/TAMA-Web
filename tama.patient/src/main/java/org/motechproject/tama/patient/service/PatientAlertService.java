package org.motechproject.tama.patient.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertCriteria;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static java.util.Collections.reverseOrder;

@Component
public class PatientAlertService {

    public final static String RED_ALERT_MESSAGE_NO_RESPONSE = "No response was recorded";

    private AllPatients allPatients;
    private AlertService alertService;
    private Logger logger = Logger.getLogger(PatientAlertService.class);

    @Autowired
    public PatientAlertService(AllPatients allPatients, AlertService alertService) {
        this.allPatients = allPatients;
        this.alertService = alertService;
    }

    public PatientAlert readAlert(String alertId) {
        final Alert alert = alertService.get(alertId);
        alertService.changeStatus(alert.getId(), AlertStatus.READ);
        return PatientAlert.newPatientAlert(alert, allPatients.get(alert.getExternalId()));
    }

    public PatientAlerts getReadAlertsFor(String clinicId, String patientId, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        return getAlertsOfSpecificTypeAndStatusAndDateRange(clinicId, patientId, AlertStatus.READ, patientAlertType, startDate, endDate);
    }

    public PatientAlerts getUnreadAlertsFor(String clinicId, String patientId, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        return getAlertsOfSpecificTypeAndStatusAndDateRange(clinicId, patientId, AlertStatus.NEW, patientAlertType, startDate, endDate);
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
        alertService.create(externalId, name, description, AlertType.MEDIUM, AlertStatus.NEW, priority, data);
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
                return Lambda.convert(alertService.search(new AlertCriteria().byExternalId(patient.getId()).byStatus(alertStatus)), alertPatientAlertConverter);
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

    public PatientAlerts getAlertsOfSpecificTypeAndStatusAndDateRange(String clinicId, String patientId, AlertStatus alertStatus, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        List<Patient> patients = Collections.emptyList();
        if (patientId != null) {
            Patient patient = allPatients.findByPatientIdAndClinicId(patientId, clinicId);
            if (patient != null) {
                patients = Arrays.asList(patient);
            }
        } else {
            patients = allPatients.findByClinic(clinicId);
        }
        PatientAlerts allAlerts = new PatientAlerts(getAlerts(patients, alertStatus));
        Predicate selectorForAlertTypeAndDateRange = getSelectorForAlertTypeAndDateRangeIfPresent(patientAlertType, startDate, endDate);
        return filterAlertsByPredicate(allAlerts, selectorForAlertTypeAndDateRange);
    }

    private PatientAlerts getAlertsOfSpecificTypeAndForDateRange(String patientID, PatientAlertType patientAlertType, DateTime startDate, DateTime endDate) {
        PatientAlerts allAlerts = getAllAlertsBy(patientID);
        Predicate selectorForAlertTypeAndDateRange = getSelectorForAlertTypeAndDateRange(patientAlertType, startDate, endDate);
        return filterAlertsByPredicate(allAlerts, selectorForAlertTypeAndDateRange);
    }

    private PatientAlerts filterAlertsByPredicate(PatientAlerts allAlerts, Predicate predicate) {
        ArrayList<PatientAlert> filteredAlerts = new ArrayList<PatientAlert>();
        CollectionUtils.select(allAlerts, predicate, filteredAlerts);
        return new PatientAlerts(filteredAlerts);
    }

    private Predicate getSelectorForAlertTypeAndDateRangeIfPresent(final PatientAlertType patientAlertType, final DateTime startDate, final DateTime endDate) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                PatientAlert patientAlert = (PatientAlert) o;
                DateTime alertTime = patientAlert.getAlert().getDateTime();
                boolean isOfRequiredAlertType = patientAlertType == null || (patientAlert.getAlert().getData() != null && patientAlertType.name().equals(patientAlert.getAlert().getData().get(PatientAlert.PATIENT_ALERT_TYPE)));
                boolean isAfterStartDate = startDate == null || alertTime.isAfter(startDate);
                boolean isBeforeEndDate = endDate == null || !alertTime.toLocalDate().isAfter(endDate.toLocalDate());
                return isOfRequiredAlertType && isAfterStartDate && isBeforeEndDate;
            }
        };
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