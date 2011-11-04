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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
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
    @Mock
    private AllVitalStatistics allVitalStatistics;

    private final String patientId = "patientId";
    PatientService patientService;

    @Before
    public void setUp() {
        initMocks(this);
        patientService = new PatientService(tamaSchedulerService, pillReminderService, allPatients, allTreatmentAdvices, allLabResults, allRegimens, allUniquePatientFields, allVitalStatistics);
    }

    @Test
    public void shouldReturnPatientMedicalConditions() {

        Patient patient = PatientBuilder.startRecording().withDefaults().withGender(Gender.newGender("Male")).withPatientId(patientId).withDateOfBirth(new LocalDate(2000, 10, 1)).build();
        when(allPatients.get(patientId)).thenReturn(patient);

        String labTestId = "labTestId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).build();
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withPatientId(patientId).withLabTest_id(labTestId).withResult("60").build();
        labResult.setLabTest(labTest);
        when(allLabResults.findByPatientId(patientId)).thenReturn(new LabResults(Arrays.asList(labResult)));

        when(allVitalStatistics.findByPatientId(patientId)).thenReturn(new VitalStatistics(74.00, 174.00, 10, 10, 10.00, 10, patientId));

        String regimenId = "regimenId";
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(DateUtil.today()).withRegimenId(regimenId).build();
        when(allTreatmentAdvices.earliestTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);

        String regimenName = "Regimen Name";
        Regimen regimen = RegimenBuilder.startRecording().withDefaults().withId(regimenId).withName(regimenName).build();
        when(allRegimens.get(regimenId)).thenReturn(regimen);

        MedicalCondition medicalCondition = patientService.getPatientMedicalConditions(patientId);

        assertEquals(regimenName, medicalCondition.regimenName());
        assertEquals("Male", medicalCondition.gender());
        assertEquals(24.44, medicalCondition.bmi());
        assertEquals(11, medicalCondition.age());
        assertEquals(60, medicalCondition.cd4Count());
    }
}