package org.motechproject.tama.repository;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.PatientAlert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static java.util.Collections.reverseOrder;
import static org.hamcrest.CoreMatchers.equalTo;

@Repository
public class AllSymptomReportingAlerts {

    private AlertService alertService;

    private AllPatients allPatients;

    @Autowired
    public AllSymptomReportingAlerts(AlertService alertService, AllPatients allPatients) {
        this.alertService = alertService;
        this.allPatients = allPatients;
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
                final List<PatientAlert> patientAlerts = Lambda.convert(alertService.getBy(patient.getId(), null, alertStatus, null, 100), alertPatientAlertConverter);
                return patientAlerts;
            }
        };
        return sort(flatten(convert(allPatients.findByClinic(clinicId), patientListConverter)), on(PatientAlert.class).getAlert().getDateTime(), reverseOrder());
    }

}
