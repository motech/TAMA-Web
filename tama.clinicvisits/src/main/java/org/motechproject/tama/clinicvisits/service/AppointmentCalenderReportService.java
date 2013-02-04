package org.motechproject.tama.clinicvisits.service;

import org.motechproject.tama.clinicvisits.contract.AppointmentCalenderReport;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.domain.PatientReports;
import org.motechproject.tama.patient.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentCalenderReportService {

    private PatientService patientService;
    private AllClinicVisits allClinicVisits;

    @Autowired
    public AppointmentCalenderReportService(PatientService patientService, AllClinicVisits allClinicVisits) {
        this.patientService = patientService;
        this.allClinicVisits = allClinicVisits;
    }

    public AppointmentCalenderReport appointmentCalendarReport(String patientId) {
        PatientReports patientReports = patientService.getPatientReports(patientId);
        ClinicVisits clinicVisits = allClinicVisits.clinicVisits(patientReports.getPatientDocIds());
        return new AppointmentCalenderReport(patientReports, clinicVisits);
    }
}
