package org.motechproject.tama.clinicvisits.contract;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.patient.domain.PatientReports;

@EqualsAndHashCode
@Data
public class AppointmentCalenderReport {

    private final PatientReports patientReports;
    private final ClinicVisits clinicVisits;

    public AppointmentCalenderReport(PatientReports patientReports, ClinicVisits clinicVisits) {
        this.patientReports = patientReports;
        this.clinicVisits = clinicVisits;
    }
}
