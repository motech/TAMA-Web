package org.motechproject.tama.symptomreporting.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.symptomsreporting.decisiontree.domain.MedicalCondition;
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
import org.motechproject.util.DateUtil;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
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

    private final String patientId = "patientId";
    private SymptomReportingService symptomReportingService;

    @Before
    public void setUp() {
        initMocks(this);
        symptomReportingService = new SymptomReportingService(allPatients, allTreatmentAdvices, allLabResults, allRegimens, allUniquePatientFields, allVitalStatistics);
    }

    @Test
    public void shouldReturnPatientMedicalConditions() {

        Patient patient = PatientBuilder.startRecording().withDefaults().withGender(Gender.newGender("Male")).withPatientId(patientId).withDateOfBirth(new LocalDate(2000, 10, 1)).build();
        when(allPatients.get(patientId)).thenReturn(patient);

        String labTestId = "labTestId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId(labTestId).build();
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().withPatientId(patientId).withLabTest_id(labTestId).withResult("60").build();
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
}