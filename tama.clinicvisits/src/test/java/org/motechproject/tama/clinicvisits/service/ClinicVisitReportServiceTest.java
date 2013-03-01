package org.motechproject.tama.clinicvisits.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.builder.DrugDosageContractBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisitSummary;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.contract.DrugDosageContract;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllReportedOpportunisticInfections;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.refdata.domain.*;
import org.motechproject.tama.refdata.repository.AllDosageTypes;
import org.motechproject.tama.refdata.repository.AllMealAdviceTypes;
import org.motechproject.tama.refdata.repository.AllOpportunisticInfections;
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
    @Mock
    private AllDosageTypes allDosageTypes;
    @Mock
    private AllMealAdviceTypes allMealAdviceTypes;
    @Mock
    private AllOpportunisticInfections allOpportunisticInfections;

    @Before
    public void setup() {
        initMocks(this);
        clinicVisitReportService = new ClinicVisitReportService(patientService, allClinicVisits, allTreatmentAdvices, allLabResults, allVitalStatistics, allReportedOpportunisticInfections, allRegimens, allDosageTypes, allMealAdviceTypes, allOpportunisticInfections);
    }

    @Test
    public void shouldFetchClinicReportUsingPatientId() {
        String patientId = "patientId";
        String patientDocumentId = "patientDocumentId";
        PatientReport patientReport = newPatientReport().withPatientId(patientId).withPatientDocumentId(patientDocumentId).build();
        PatientReports patientReports = new PatientReports(asList(patientReport));

        DateTime visitDate1 = new DateTime(2013, 1, 1, 10, 0);
        DateTime visitDate2 = new DateTime(2013, 1, 10, 10, 0);
        ClinicVisit clinicVisit1 = ClinicVisitBuilder.startRecording().withDefaults().withId("visit1").withVisitDate(visitDate1).withTreatmentAdviceId("treatAdvice1").withReportedOpportunisticInfection("RO1").build();
        ClinicVisit clinicVisit2 = ClinicVisitBuilder.startRecording().withDefaults().withId("visit2").withVisitDate(visitDate2).withTreatmentAdviceId("treatAdvice2").withReportedOpportunisticInfection("RO2").build();
        ClinicVisits clinicVisits = new ClinicVisits(asList(clinicVisit1, clinicVisit2));

        DrugCompositionGroup group1 = new DrugCompositionGroup() {{ setName("dc1"); setId("dc1"); }};
        DrugCompositionGroup group2 = new DrugCompositionGroup() {{ setName("dc2"); setId("dc2"); }};

        org.motechproject.tama.patient.domain.DrugDosage drugDosage1 = new org.motechproject.tama.patient.domain.DrugDosage() {{
            setDrugName("Drug");
            setDosageTypeId("Type1");
            setMorningTime("08:00 AM");
            setMealAdviceId("MA1");
        }};
        org.motechproject.tama.patient.domain.DrugDosage drugDosage2 = new org.motechproject.tama.patient.domain.DrugDosage() {{
            setDrugName("Drug");
            setDosageTypeId("Type1");
            setEveningTime("09:00 PM");
            setMealAdviceId("MA2");
        }};

        TreatmentAdvice treatmentAdvice1 = new TreatmentAdviceBuilder().withDefaults().withDrugCompositionGroupId(group1.getId()).withDrugDosages(drugDosage1).build();
        TreatmentAdvice treatmentAdvice2 = new TreatmentAdviceBuilder().withDefaults().withDrugCompositionGroupId(group2.getId()).withDrugDosages(drugDosage2).build();

        LabResults labResults = new LabResults(asList(new LabResult()));

        VitalStatistics vitalStatistics = new VitalStatistics();

        Regimen regimen = new Regimen();
        regimen.addCompositionGroup(group1, group2);


        DrugDosageContract dosage1 = new DrugDosageContractBuilder().withMorningTime("08:00 AM").withMealAdvice("MealAdvice1").build();
        DrugDosageContract dosage2 = new DrugDosageContractBuilder().withMorningTime(null).withEveningTime("09:00 PM").withMealAdvice("MealAdvice2").build();

        final OpportunisticInfection infection1 = new OpportunisticInfection() {{ setId("1"); setName("A"); }};
        final OpportunisticInfection infection2 = new OpportunisticInfection() {{ setId("2"); setName("B"); }};
        final OpportunisticInfection infection3 = new OpportunisticInfection() {{ setId("3"); setName("C"); }};
        final OpportunisticInfection infection4 = new OpportunisticInfection() {{ setId("4"); setName("X"); }};
        final OpportunisticInfection infection5 = new OpportunisticInfection() {{ setId("5"); setName("Y"); }};
        final OpportunisticInfection infection6 = new OpportunisticInfection() {{ setId("6"); setName("Z"); }};

        ReportedOpportunisticInfections reportedOpportunisticInfections1 = new ReportedOpportunisticInfections() {{
            addOpportunisticInfection(infection1);
            addOpportunisticInfection(infection2);
            addOpportunisticInfection(infection3);
        }};

        ReportedOpportunisticInfections reportedOpportunisticInfections2 = new ReportedOpportunisticInfections() {{
            addOpportunisticInfection(infection4);
            addOpportunisticInfection(infection5);
            addOpportunisticInfection(infection6);
        }};

        ClinicVisitSummary clinicVisitSummary1 = new ClinicVisitSummary(patientReport, visitDate1, labResults, vitalStatistics, "A,B,C", regimen, "dc1", dosage1, null );
        ClinicVisitSummary clinicVisitSummary2 = new ClinicVisitSummary(patientReport, visitDate2, labResults, vitalStatistics, "X,Y,Z", regimen, "dc2", dosage2, null);
        List<ClinicVisitSummary> clinicVisitSummaries = asList(clinicVisitSummary1, clinicVisitSummary2);

        when(patientService.getPatientReports(patientId)).thenReturn(patientReports);
        when(allClinicVisits.clinicVisits(patientDocumentId)).thenReturn(clinicVisits);
        when(allTreatmentAdvices.get(clinicVisit1.getTreatmentAdviceId())).thenReturn(treatmentAdvice1);
        when(allTreatmentAdvices.get(clinicVisit2.getTreatmentAdviceId())).thenReturn(treatmentAdvice2);
        when(allLabResults.withIds(clinicVisit1.getLabResultIds())).thenReturn(labResults);
        when(allLabResults.withIds(clinicVisit2.getLabResultIds())).thenReturn(labResults);
        when(allVitalStatistics.get(clinicVisit1.getVitalStatisticsId())).thenReturn(vitalStatistics);
        when(allVitalStatistics.get(clinicVisit2.getVitalStatisticsId())).thenReturn(vitalStatistics);
        when(allReportedOpportunisticInfections.get(clinicVisit1.getReportedOpportunisticInfectionsId())).thenReturn(reportedOpportunisticInfections1);
        when(allReportedOpportunisticInfections.get(clinicVisit2.getReportedOpportunisticInfectionsId())).thenReturn(reportedOpportunisticInfections2);
        when(allOpportunisticInfections.get("1")).thenReturn(infection1);
        when(allOpportunisticInfections.get("2")).thenReturn(infection2);
        when(allOpportunisticInfections.get("3")).thenReturn(infection3);
        when(allOpportunisticInfections.get("4")).thenReturn(infection4);
        when(allOpportunisticInfections.get("5")).thenReturn(infection5);
        when(allOpportunisticInfections.get("6")).thenReturn(infection6);

        when(allRegimens.get(treatmentAdvice1.getRegimenId())).thenReturn(regimen);
        when(allRegimens.get(treatmentAdvice2.getRegimenId())).thenReturn(regimen);

        when(allDosageTypes.get("Type1")).thenReturn(new DosageType("Type1"));
        when(allDosageTypes.get("Type2")).thenReturn(new DosageType("Type2"));

        when(allMealAdviceTypes.get("MA1")).thenReturn(new MealAdviceType("MealAdvice1"));
        when(allMealAdviceTypes.get("MA2")).thenReturn(new MealAdviceType("MealAdvice2"));

        assertEquals(clinicVisitSummaries, clinicVisitReportService.getClinicVisitReport(patientId));
    }

}
