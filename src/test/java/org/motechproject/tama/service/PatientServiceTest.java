package org.motechproject.tama.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.builder.*;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.platform.service.TamaSchedulerService;
import org.motechproject.tama.repository.*;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class PatientServiceTest {
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllUniquePatientFields allUniquePatientFields;
    @Mock
    private PillReminderService pillReminderService;
    @Mock
    private TamaSchedulerService tamaSchedulerService;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllLabResults allLabResults;
    @Mock
    private AllRegimens allRegimens;

    private final String patientId = "patientId";
    PatientService patientService;

    @Before
    public void setUp() {
        initMocks(this);
        patientService = new PatientService(tamaSchedulerService, pillReminderService, allPatients, allTreatmentAdvices, allLabResults, allRegimens, allUniquePatientFields);
    }

    @Test
    public void shouldReturnPatientForGivenId() {
        Patient expectedPatient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId).build();
        when(allPatients.get(patientId)).thenReturn(expectedPatient);

        Patient patient = patientService.getPatient(patientId);

        assertEquals(expectedPatient, patient);
    }

    @Test
    public void shouldReturnLabResultsForGivenPatient() {
        String labTestId = "labTestId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).withName("CD4").build();
        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTestId).withTestDate(new LocalDate(2011, 6, 20)).withResult("60").build();
        labResult1.setLabTest(labTest);
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTestId).withTestDate(new LocalDate(2011, 10, 20)).withResult("50").build();
        labResult2.setLabTest(labTest);

        when(allLabResults.findByPatientId(patientId)).thenReturn(new LabResults(Arrays.asList(labResult1, labResult2)));

        LabResults labResults = patientService.getLabResults(patientId);

        assertEquals(2, labResults.size());
        assertEquals(labResult1, labResults.get(0));
        assertEquals(labResult2, labResults.get(1));
    }

    @Test
    public void shouldReturnTreatmentAdviceForGivenPatient() {
        TreatmentAdvice expectedTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        when(allTreatmentAdvices.findByPatientId(patientId)).thenReturn(expectedTreatmentAdvice);

        TreatmentAdvice treatmentAdvice = patientService.getTreatmentAdvice(patientId);

        assertEquals(expectedTreatmentAdvice, treatmentAdvice);
    }

    @Test
    public void shouldReturnRegimenForGivenId() {
        String regimenId = "regimenId";
        Regimen expectedRegimen = RegimenBuilder.startRecording().withDefaults().withId(regimenId).build();
        when(allRegimens.get(regimenId)).thenReturn(expectedRegimen);

        Regimen regimen = patientService.getRegimen(regimenId);

        assertEquals(expectedRegimen, regimen);
    }

    @Test
    public void shouldReturnPatientMedicalConditions() {
        LocalDate dateOfBirth = new LocalDate(2000, 10, 1);
        PowerMockito.mockStatic(DateUtil.class);
        PowerMockito.when(DateUtil.today()).thenReturn(new LocalDate(2011, 10, 19));
        PowerMockito.when(DateUtil.newDate(any(Date.class))).thenReturn(dateOfBirth);

        Patient patient = PatientBuilder.startRecording().withGender(Gender.newGender("Male")).withPatientId(patientId).withDateOfBirth(dateOfBirth).build();
        when(allPatients.get(patientId)).thenReturn(patient);

        String labTestId = "labTestId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).withName("CD4").build();
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withPatientId(patientId).withLabTest_id(labTestId).withResult("60").build();
        labResult.setLabTest(labTest);
        when(allLabResults.findByPatientId(patientId)).thenReturn(new LabResults(Arrays.asList(labResult)));

        String regimenId = "regimenId";
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withRegimenId(regimenId).build();
        when(allTreatmentAdvices.findByPatientId(patientId)).thenReturn(treatmentAdvice);

        String regimenName = "Regimen Name";
        Regimen regimen = RegimenBuilder.startRecording().withDefaults().withId(regimenId).withName(regimenName).build();
        when(allRegimens.get(regimenId)).thenReturn(regimen);

        PatientMedicalConditions patientMedicalConditions = patientService.getPatientMedicalConditions(patientId);

        assertEquals(regimenName, patientMedicalConditions.getRegimenName());
        assertEquals("Male", patientMedicalConditions.getGender());
        assertEquals(11, patientMedicalConditions.getAge());
        assertEquals(60, patientMedicalConditions.getCd4Count());
    }

    @Test
    public void shouldReturnTheLatestTestResultInCaseThereAreMultipleTestResultsForALabTest() {
        LocalDate dateOfBirth = new LocalDate(2000, 10, 1);
        Patient patient = PatientBuilder.startRecording().withGender(Gender.newGender("Male")).withPatientId(patientId).withDateOfBirth(dateOfBirth).build();
        when(allPatients.get(patientId)).thenReturn(patient);

        String labTestId = "labTestId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).withName("CD4").build();

        LabResult labResult1 = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTestId).withTestDate(new LocalDate(2011, 6, 20)).withResult("60").build();
        labResult1.setLabTest(labTest);
        LabResult labResult2 = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTestId).withTestDate(new LocalDate(2011, 10, 20)).withResult("50").build();
        labResult2.setLabTest(labTest);
        LabResult labResult3 = LabResultBuilder.startRecording().withDefaults().withLabTest_id(labTestId).withTestDate(new LocalDate(2011, 9, 20)).withResult("70").build();
        labResult3.setLabTest(labTest);
        when(allLabResults.findByPatientId(patientId)).thenReturn(new LabResults(Arrays.asList(labResult1, labResult2, labResult3)));

        String regimenId = "regimenId";
        when(allTreatmentAdvices.findByPatientId(patientId)).thenReturn(TreatmentAdviceBuilder.startRecording().withDefaults().withRegimenId(regimenId).build());
        when(allRegimens.get(regimenId)).thenReturn(RegimenBuilder.startRecording().withDefaults().build());

        PatientMedicalConditions patientMedicalConditions = patientService.getPatientMedicalConditions(patientId);

        assertEquals(50, patientMedicalConditions.getCd4Count());


    }
}