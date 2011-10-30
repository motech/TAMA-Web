package org.motechproject.tama.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import org.joda.time.DateTime;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.PatientAlert;
import org.motechproject.tama.domain.SymptomsAlertStatus;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static java.util.Collections.reverseOrder;
import static org.hamcrest.CoreMatchers.equalTo;

@Component
public class PatientAlertService {

    private AllPatients allPatients;
    private AlertService alertService;

    @Autowired
    public PatientAlertService(AllPatients allPatients, AlertService alertService) {
        this.allPatients = allPatients;
        this.alertService = alertService;
    }

    public PatientAlert getSymptomReportingAlert(String alertId) {
        final Alert alert = selectUnique(alertService.getBy(null, null, null, null, 100),
                having(on(Alert.class).getId(),
                        equalTo(alertId)));

        alertService.changeStatus(alert.getId(), AlertStatus.READ);
        return PatientAlert.newPatientAlert(alert, allPatients.get(alert.getExternalId()));
    }

    public List<PatientAlert> getReadAlertsForClinic(String clinicId) {
        return getAlerts(clinicId, AlertStatus.READ);
    }

    public List<PatientAlert> getUnreadAlertsForClinic(String clinicId) {
        return getAlerts(clinicId, AlertStatus.NEW);
    }

    private List<PatientAlert> getAlerts(String clinicId, final AlertStatus alertStatus) {
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
        return sort(flatten(convert(allPatients.findByClinic(clinicId), patientListConverter)), on(PatientAlert.class).getAlert().getDateTime(), reverseOrder());
    }

    public void createAlert(String externalId, Integer priority, String symptomReported, String adviceGiven) {
        HashMap<String,String> data = new HashMap<String, String>();
        data.put(PatientAlert.SYMPTOMS_ALERT_STATUS, SymptomsAlertStatus.Open.name());
        final Alert symptomsAlert = new Alert(externalId, AlertType.MEDIUM, AlertStatus.NEW, priority, data);
        final DateTime now = DateUtil.now();
        symptomsAlert.setDateTime(now);
        symptomsAlert.setDescription(symptomReported);
        symptomsAlert.setName(adviceGiven);
        alertService.createAlert(symptomsAlert);
    }

    public void updateAlert(String alertId, String symptomsAlertStatus, String notes, String doctorsNotes) {
        alertService.setData(alertId, PatientAlert.SYMPTOMS_ALERT_STATUS, symptomsAlertStatus);
        alertService.setData(alertId, PatientAlert.DOCTORS_NOTES, doctorsNotes);
        alertService.setData(alertId, PatientAlert.NOTES, notes);
    }
}