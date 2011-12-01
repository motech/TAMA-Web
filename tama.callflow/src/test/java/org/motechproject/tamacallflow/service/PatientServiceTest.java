package org.motechproject.tamacallflow.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.decisiontree.domain.MedicalCondition;
import org.motechproject.tamacallflow.platform.service.TamaSchedulerService;
import org.motechproject.tamadomain.builder.*;
import org.motechproject.tamadomain.domain.*;
import org.motechproject.tamadomain.repository.*;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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
    @Mock
    private AllDosageAdherenceLogs allDosageAdherenceLogs;
    @Mock
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;
    @Mock
    private DailyReminderAdherenceService dailyReminderAdherenceService;

    private final String patientId = "patientId";
    PatientService patientService;

    @Before
    public void setUp() {
        initMocks(this);
        patientService = new PatientService(tamaSchedulerService, pillReminderService, allPatients, allTreatmentAdvices, allLabResults, allRegimens, allUniquePatientFields, allVitalStatistics, fourDayRecallAdherenceService, dailyReminderAdherenceService);
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

    @Test
    public void shouldActivateWhenPatientIsReActivated(){
        when(allPatients.get("patientId")).thenReturn(PatientBuilder.startRecording().withDefaults().withPatientId("patientId").withLastSuspendedDate(new DateTime(2011, 11, 11, 0, 0, 0)).build());
        patientService.reActivate("patientId", new SuspendedAdherenceData());
        verify(allPatients).activate("patientId");
    }

}