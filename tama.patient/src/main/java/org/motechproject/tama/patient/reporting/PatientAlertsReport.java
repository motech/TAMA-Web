package org.motechproject.tama.patient.reporting;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.motechproject.tama.patient.domain.PatientAlerts;
import org.motechproject.tama.patient.domain.PatientReports;
import org.motechproject.tama.patient.service.PatientService;

import java.util.List;

@EqualsAndHashCode
@Data
public class PatientAlertsReport {


    private final PatientReports patientReports;
    private final PatientAlerts patientAlerts;

    public PatientAlertsReport(PatientAlerts patientAlerts, PatientReports patientReports) {
        this.patientReports = patientReports;
        this.patientAlerts = patientAlerts;
    }
}
