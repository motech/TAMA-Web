package org.motechproject.tama.clinicvisits.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.contract.AppointmentCalenderReport;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.domain.PatientReports;
import org.motechproject.tama.patient.service.PatientService;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tama.patient.builder.PatientReportBuilder.newPatientReport;

public class AppointmentCalenderReportServiceTest {

    private AppointmentCalenderReportService appointmentCalenderReportService;

    @Mock
    private AllClinicVisits allClinicVisits;
    @Mock
    private PatientService patientService;

    @Before
    public void setup() {
        initMocks(this);
        appointmentCalenderReportService = new AppointmentCalenderReportService(patientService, allClinicVisits);
    }

    @Test
    public void shouldFetchAppointmentCalendarReportUsingUniquePatientId() {
        String patientId = "patientId";

        ClinicVisits clinicVisits = new ClinicVisits();
        PatientReports report = new PatientReports(asList(newPatientReport().withPatientId(patientId).withPatientDocumentId("patientDocumentId").build()));
        AppointmentCalenderReport appointmentCalenderReport = new AppointmentCalenderReport(report, clinicVisits);

        when(patientService.getPatientReports(patientId)).thenReturn(report);
        when(allClinicVisits.clinicVisits(report.getPatientDocIds())).thenReturn(clinicVisits);

        assertEquals(appointmentCalenderReport, appointmentCalenderReportService.appointmentCalendarReport(patientId));
    }

    @Test
    public void shouldFetchAppointmentCalendarReportUsingNonUniquePatientId() {
        String patientId = "patientId";

        ClinicVisits clinicVisits1 = new ClinicVisits();
        ClinicVisits clinicVisits2 = new ClinicVisits();

        ClinicVisits mergedClinicVisits = new ClinicVisits();
        mergedClinicVisits.addAll(clinicVisits1);
        mergedClinicVisits.addAll(clinicVisits2);

        PatientReports reports = new PatientReports(
                asList(
                        newPatientReport().withPatientId(patientId).withPatientDocumentId("patientDocumentId1").build(),
                        newPatientReport().withPatientId(patientId).withPatientDocumentId("patientDocumentId2").build()
                )
        );
        AppointmentCalenderReport appointmentCalenderReport = new AppointmentCalenderReport(reports, mergedClinicVisits);

        when(patientService.getPatientReports(patientId)).thenReturn(reports);
        when(allClinicVisits.clinicVisits(asList("patientDocumentId1", "patientDocumentId2"))).thenReturn(mergedClinicVisits);

        assertEquals(appointmentCalenderReport, appointmentCalenderReportService.appointmentCalendarReport(patientId));
    }
}
