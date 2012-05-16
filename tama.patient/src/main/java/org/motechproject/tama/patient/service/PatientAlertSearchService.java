package org.motechproject.tama.patient.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.group.Group;
import org.joda.time.DateTime;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertCriteria;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientAlert;
import org.motechproject.tama.patient.domain.PatientAlerts;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.lambdaj.Lambda.by;
import static ch.lambdaj.Lambda.on;

@Component
public class PatientAlertSearchService {
    private AlertService alertService;
    private AllPatients allPatients;

    @Autowired
    public PatientAlertSearchService(AlertService alertService, AllPatients allPatients) {
        this.alertService = alertService;
        this.allPatients = allPatients;
    }

    public PatientAlerts search(String patientDocumentId) {
        return search(patientDocumentId, null, null, null);
    }

    public PatientAlerts search(String patientDocumentId, DateTime startDate, DateTime endDate, AlertStatus alertStatus) {
        AlertCriteria alertCriteria = constructAlertCriteria(patientDocumentId, startDate, endDate, alertStatus);
        List<Alert> filteredAlertsForAPatient = alertService.search(alertCriteria);
        Group<Alert> group = Lambda.group(filteredAlertsForAPatient, by(on(Alert.class).getExternalId()));
        return convertToPatientAlerts(group);
    }

    private AlertCriteria constructAlertCriteria(String patientDocumentId, DateTime startDate, DateTime endDate, AlertStatus alertStatus) {
        AlertCriteria alertCriteria = new AlertCriteria();
        if (patientDocumentId != null){
            alertCriteria.byExternalId(patientDocumentId);
        }
        if (startDate != null && endDate != null) {
            alertCriteria.byDateRange(startDate, endDate);
        }
        if (alertStatus != null){
            alertCriteria.byStatus(alertStatus);
        }
        return alertCriteria;
    }

    private PatientAlerts convertToPatientAlerts(Group<Alert> group) {
        PatientAlerts patientAlerts = new PatientAlerts();
        for (String externalId : group.keySet()) {
            Patient patient = allPatients.get(externalId);
            for (Alert alert : group.find(externalId)) {
                patientAlerts.add(PatientAlert.newPatientAlert(alert, patient));
            }
        }
        return patientAlerts;
    }
}
