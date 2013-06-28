package org.motechproject.tama.patient.service;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;


import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.tama.patient.domain.PatientAlert;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tama.patient.domain.PatientAlerts;
import org.motechproject.tama.patient.domain.PatientReports;
import org.motechproject.tama.patient.reporting.PatientAlertsReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PatientAlertsReportService {


    private PatientAlertService patientAlertService;
    private PatientService patientService;


    @Autowired
    public PatientAlertsReportService(PatientAlertService patientAlertService, PatientService patientService) {
        this.patientAlertService = patientAlertService;
        this.patientService = patientService;
    }


    public PatientAlertsReport report(String patientId, DateTime startDate, DateTime endDate, String patientAlertType, String clinicId, String patientAlertStatus) {
        String ANY_TYPE_KEY_STATUS = "Any";
        PatientAlerts patientAlerts = null;
        PatientReports patientReports = patientService.getPatientReports(patientId);
        PatientAlertType alertType = null;
        if (ANY_TYPE_KEY_STATUS.equals(patientAlertType)) {
            patientAlertType = "";
        } else {
            alertType = getPatientAlertType(patientAlertType);
        }
        if (ANY_TYPE_KEY_STATUS.equals(patientAlertStatus)) {
            patientAlertStatus = "";
        }

        if ("".equals(clinicId)) {
            patientAlerts = patientAlertService.getAlertsForPatientIdAndDateRange(clinicId, patientId, alertType, startDate, endDate, null);
        } else {
            patientAlerts = patientAlertService.getAlertsForParameters(clinicId, patientId, alertType, startDate, endDate);

        }
        if (patientAlertStatus != null && !StringUtils.isEmpty(patientAlertStatus)) {
            patientAlerts = filterAlertsByStatus(patientAlerts, patientAlertStatus);
        }

        return new PatientAlertsReport(patientAlerts, patientReports);
    }

    private PatientAlerts filterAlertsByStatus(PatientAlerts patientAlerts, String patientAlertStatus) {
        ArrayList filteredAlerts = new ArrayList();
        CollectionUtils.select(patientAlerts, getSelectorForStatus(patientAlertStatus), filteredAlerts);
        return new PatientAlerts(filteredAlerts);

    }

    private Predicate getSelectorForStatus(final String status) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                PatientAlert patientAlert = (PatientAlert) o;
                return patientAlert.getAlertStatus().equals(status);
            }
        };
    }

    private PatientAlertType getPatientAlertType(String displayName) {

        PatientAlertType[] patientAlertTypes = PatientAlertType.values();

        for (int i = 0; i < patientAlertTypes.length; i++) {
            if (patientAlertTypes[i].getDisplayName().equals(displayName)) {
                return patientAlertTypes[i];

            }
        }
        return null;
    }

}
