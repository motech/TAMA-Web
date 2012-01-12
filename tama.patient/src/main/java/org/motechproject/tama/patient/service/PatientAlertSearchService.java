package org.motechproject.tama.patient.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertCriteria;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientAlert;
import org.motechproject.tama.patient.domain.PatientAlerts;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static java.util.Collections.reverseOrder;

public class PatientAlertSearchService {
    private AlertService alertService;

    public PatientAlertSearchService(AlertService alertService) {
        this.alertService = alertService;
    }

    public PatientAlerts search(List<Patient> patients, AlertStatus alertStatus) {
        final Converter<Patient, List<PatientAlert>> patientListConverter = convertToAListOfPatientAlerts(alertStatus);
        List<PatientAlert> patientAlerts = sort(flatten(convert(patients, patientListConverter)), on(PatientAlert.class).getAlert().getDateTime(), reverseOrder());
        return new PatientAlerts(patientAlerts);
    }

    private AlertCriteria constructAlertCriteria(Patient patient, AlertStatus alertStatus) {
        AlertCriteria alertCriteria = new AlertCriteria();
        if (patient.getId() != null){
            alertCriteria.byExternalId(patient.getId());
        }
        if (alertStatus != null){
            alertCriteria.byStatus(alertStatus);
        }
        return alertCriteria;
    }

    private Converter<Patient, List<PatientAlert>> convertToAListOfPatientAlerts(final AlertStatus alertStatus) {
        return new Converter<Patient, List<PatientAlert>>() {
            @Override
            public List<PatientAlert> convert(final Patient patient) {
                AlertCriteria alertCriteria = constructAlertCriteria(patient, alertStatus);
                return Lambda.convert(alertService.search(alertCriteria), convertToPatientAlert(patient));
            }
        };
    }

    private Converter<Alert, PatientAlert> convertToPatientAlert(final Patient patient) {
        return new Converter<Alert, PatientAlert>() {
            @Override
            public PatientAlert convert(Alert alert) {
                return PatientAlert.newPatientAlert(alert, patient);
            }
        };
    }
}
