package org.motechproject.tama.clinicvisits.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisitSummary;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllReportedOpportunisticInfections;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tama.patient.builder.PatientReportBuilder.newPatientReport;

public class ClinicVisitReportServiceTest {

    private ClinicVisitReportService clinicVisitReportService;

    @Mock
    private PatientService patientService;
    @Mock
    private AllClinicVisits allClinicVisits;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllLabResults allLabResults;
    @Mock
    private AllVitalStatistics allVitalStatistics;
    @Mock
    private AllReportedOpportunisticInfections allReportedOpportunisticInfections;
    @Mock
    private AllRegimens allRegimens;

    @Before
    public void setup() {
        initMocks(this);
        clinicVisitReportService = new ClinicVisitReportService(patientService, allClinicVisits, allTreatmentAdvices, allLabResults, allVitalStatistics, allReportedOpportunisticInfections, allRegimens);
    }

    @Test
    public void shouldFetchClinicReportUsingPatientId() {
        String patientId = "patientId";
        String patientDocumentId = "patientDocumentId";
        PatientReport patientReport = newPatientReport().withPatientId(patientId).withPatientDocumentId(patientDocumentId).build();
        PatientReports patientReports = new PatientReports(asList(patientReport));

        ClinicVisit clinicVisit1 = ClinicVisitBuilder.startRecording().withDefaults().withId("visit1").withTreatmentAdviceId("treatAdvice1").build();
        ClinicVisit clinicVisit2 = ClinicVisitBuilder.startRecording().withDefaults().withId("visit2").withTreatmentAdviceId("treatAdvice2").build();
        ClinicVisits clinicVisits = new ClinicVisits(asList(clinicVisit1, clinicVisit2));

        TreatmentAdvice treatmentAdvice1 = new TreatmentAdviceBuilder().withDefaults().build();
        TreatmentAdvice treatmentAdvice2 = new TreatmentAdviceBuilder().withDefaults().build();

        LabResults labResults = new LabResults(asList(new LabResult()));

        VitalStatistics vitalStatistics = new VitalStatistics();

        ReportedOpportunisticInfections opportunisticInfections1 = new ReportedOpportunisticInfections();
        ReportedOpportunisticInfections opportunisticInfections2 = new ReportedOpportunisticInfections();

        Regimen regimen = new Regimen();

        ClinicVisitSummary clinicVisitSummary1 = new ClinicVisitSummary(patientReport, clinicVisit1, treatmentAdvice1, labResults, vitalStatistics, opportunisticInfections1, regimen);
        ClinicVisitSummary clinicVisitSummary2 = new ClinicVisitSummary(patientReport, clinicVisit2, treatmentAdvice2, labResults, vitalStatistics, opportunisticInfections2, regimen);
        List<ClinicVisitSummary> clinicVisitSummaries = asList(clinicVisitSummary1, clinicVisitSummary2);

        when(patientService.getPatientReports(patientId)).thenReturn(patientReports);
        when(allClinicVisits.clinicVisits(patientDocumentId)).thenReturn(clinicVisits);
        when(allTreatmentAdvices.get(clinicVisit1.getTreatmentAdviceId())).thenReturn(treatmentAdvice1);
        when(allTreatmentAdvices.get(clinicVisit2.getTreatmentAdviceId())).thenReturn(treatmentAdvice2);
        when(allLabResults.withIds(clinicVisit1.getLabResultIds())).thenReturn(labResults);
        when(allLabResults.withIds(clinicVisit2.getLabResultIds())).thenReturn(labResults);
        when(allVitalStatistics.get(clinicVisit1.getVitalStatisticsId())).thenReturn(vitalStatistics);
        when(allVitalStatistics.get(clinicVisit2.getVitalStatisticsId())).thenReturn(vitalStatistics);
        when(allReportedOpportunisticInfections.get(clinicVisit1.getReportedOpportunisticInfectionsId())).thenReturn(opportunisticInfections1);
        when(allReportedOpportunisticInfections.get(clinicVisit2.getReportedOpportunisticInfectionsId())).thenReturn(opportunisticInfections2);
        when(allRegimens.get(treatmentAdvice1.getRegimenId())).thenReturn(regimen);
        when(allRegimens.get(treatmentAdvice2.getRegimenId())).thenReturn(regimen);

        assertEquals(clinicVisitSummaries, clinicVisitReportService.getClinicVisitReport(patientId));
    }

}
