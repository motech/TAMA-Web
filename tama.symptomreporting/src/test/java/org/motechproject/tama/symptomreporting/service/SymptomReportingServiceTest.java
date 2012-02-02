package org.motechproject.tama.symptomreporting.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.ivr.service.SendSMSService;
import org.motechproject.tama.patient.builder.LabResultBuilder;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.*;
import org.motechproject.tama.refdata.builder.LabTestBuilder;
import org.motechproject.tama.refdata.builder.RegimenBuilder;
import org.motechproject.tama.refdata.domain.Gender;
import org.motechproject.tama.refdata.domain.LabTest;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.symptomreporting.domain.SymptomReport;
import org.motechproject.tama.symptomsreporting.decisiontree.domain.MedicalCondition;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomReportingServiceTest {
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllUniquePatientFields allUniquePatientFields;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllLabResults allLabResults;
    @Mock
    private AllRegimens allRegimens;
    @Mock
    private AllVitalStatistics allVitalStatistics;
    @Mock
    private SendSMSService sendSMSService;
    @Mock
    private Properties symptomsReportingAdviceMap;

    private final String patientId = "patientId";
    private SymptomReportingService symptomReportingService;

    @Before
    public void setUp() {
        initMocks(this);
        symptomReportingService = new SymptomReportingService(allPatients, allTreatmentAdvices, allLabResults, allRegimens, allVitalStatistics, sendSMSService, symptomsReportingAdviceMap);
    }

    @Test
    public void shouldReturnPatientMedicalConditions() {

        Patient patient = PatientBuilder.startRecording().withDefaults().withGender(Gender.newGender("Male")).withPatientId(patientId).withDateOfBirth(new LocalDate(2000, 10, 1)).build();
        when(allPatients.get(patientId)).thenReturn(patient);

        String labTestId = "labTestId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).build();
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withPatientId(patientId).withLabTestId(labTestId).withResult("60").build();
        labResult.setLabTest(labTest);
        when(allLabResults.findLatestLabResultsByPatientId(patientId)).thenReturn(new LabResults(Arrays.asList(labResult)));

        when(allVitalStatistics.findLatestVitalStatisticByPatientId(patientId)).thenReturn(new VitalStatistics(74.00, 174.00, 10, 10, 10.00, 10, patientId));

        String regimenId = "regimenId";
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(DateUtil.today()).withRegimenId(regimenId).build();
        when(allTreatmentAdvices.earliestTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);

        String regimenName = "Regimen Name";
        Regimen regimen = RegimenBuilder.startRecording().withDefaults().withId(regimenId).withName(regimenName).build();
        when(allRegimens.get(regimenId)).thenReturn(regimen);

        MedicalCondition medicalCondition = symptomReportingService.getPatientMedicalConditions(patientId);

        assertEquals(regimenName, medicalCondition.regimenName());
        assertEquals("Male", medicalCondition.gender());
        assertEquals(24.44, medicalCondition.bmi());
        assertEquals(11, medicalCondition.age());
        assertEquals(60, medicalCondition.cd4Count());
    }

    @Test
    public void shouldSendSMSToNotifyCliniciansAboutOTCAdvice() {
        SymptomReport symptomReport = mock(SymptomReport.class);
        Regimen regimen = mock(Regimen.class);
        final String patientId = "patientId";
        final String patientDocId = "patientDocId";
        final Clinic clinic = new Clinic("id");
        clinic.setClinicianContacts(new ArrayList<Clinic.ClinicianContact>() {{
            this.add(new Clinic.ClinicianContact("name1", "ph1"));
            this.add(new Clinic.ClinicianContact("name2", "ph2"));
            this.add(new Clinic.ClinicianContact("name3", "ph3"));
        }});
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withRegimenId("regimenId").withPatientId(patientDocId).build();

        Patient patient = PatientBuilder.startRecording().withMobileNumber("1234567890").withClinic(clinic).withPatientId(patientId).withId(patientDocId).build();

        when(allPatients.get(patientDocId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientDocId)).thenReturn(treatmentAdvice);
        when(allRegimens.get("regimenId")).thenReturn(regimen);
        when(regimen.getDisplayName()).thenReturn("D4T+EFV+NVP");
        when(symptomReport.getAdviceGiven()).thenReturn("adv_crocin01");
        when(symptomsReportingAdviceMap.get("adv_crocin01")).thenReturn("ADV: Some advice");
        when(symptomReport.getSymptomIds()).thenReturn(Arrays.asList("fever", "nauseavomiting", "headache"));

        symptomReportingService.notifyCliniciansAboutOTCAdvice(patientDocId, symptomReport);

        verify(sendSMSService).send(Arrays.asList("ph1", "ph2", "ph3"), "patientId:1234567890:D4T+EFV+NVP,trying to contact. fever,nauseavomiting,headache. ADV: Some advice");
    }
}