package org.motechproject.tama.patient.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.strategy.CallPlan;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TreatmentAdviceServiceTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private CallPlan dailyCallPlan;
    @Mock
    private CallPlan weeklyCallPlan;
    @Mock
    private CallTimeSlotService callTimeSlotService;

    private TreatmentAdviceService treatmentAdviceService;

    @Before
    public void setUp() {
        initMocks(this);
        treatmentAdviceService = new TreatmentAdviceService(allPatients, allTreatmentAdvices, callTimeSlotService);
    }

    @Test
    public void dailyPillReminderPatient_createsANewRegimen() {
        treatmentAdviceService.registerCallPlan(CallPreference.DailyPillReminder, dailyCallPlan);
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(treatmentAdvice.getPatientId())).thenReturn(patient);

        treatmentAdviceService.createRegimen(treatmentAdvice);
        verify(allTreatmentAdvices).add(treatmentAdvice);
        verify(dailyCallPlan).enroll(patient, treatmentAdvice);
    }

    @Test
    public void fourDayRecallPatient_createsANewRegimen() {
        treatmentAdviceService.registerCallPlan(CallPreference.FourDayRecall, weeklyCallPlan);
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(treatmentAdvice.getPatientId())).thenReturn(patient);

        treatmentAdviceService.createRegimen(treatmentAdvice);
        verify(allTreatmentAdvices).add(treatmentAdvice);
        verify(weeklyCallPlan).enroll(patient, treatmentAdvice);
    }

    @Test
    public void dailyPillReminderPatient_changesCurrentRegimen() {
        treatmentAdviceService.registerCallPlan(CallPreference.DailyPillReminder, dailyCallPlan);
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        TreatmentAdvice existingTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allTreatmentAdvices.get(existingTreatmentAdvice.getId())).thenReturn(existingTreatmentAdvice);
        when(allPatients.get(treatmentAdvice.getPatientId())).thenReturn(patient);

        final String newTreatmentAdviceId = treatmentAdviceService.changeRegimen(existingTreatmentAdvice.getId(), "stop", treatmentAdvice);

        assertEquals(treatmentAdvice.getId(), newTreatmentAdviceId);
        verify(allTreatmentAdvices).add(treatmentAdvice);
        verify(allTreatmentAdvices).update(existingTreatmentAdvice);
        verify(dailyCallPlan).reEnroll(patient, treatmentAdvice);
    }

    @Test
    public void fourDayRecallPatient_changesCurrentRegimen() {
        treatmentAdviceService.registerCallPlan(CallPreference.FourDayRecall, weeklyCallPlan);
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        TreatmentAdvice existingTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allTreatmentAdvices.get(existingTreatmentAdvice.getId())).thenReturn(existingTreatmentAdvice);
        when(allPatients.get(treatmentAdvice.getPatientId())).thenReturn(patient);

        treatmentAdviceService.changeRegimen(existingTreatmentAdvice.getId(), "stop", treatmentAdvice);
        verify(allTreatmentAdvices).add(treatmentAdvice);
        verify(allTreatmentAdvices).update(existingTreatmentAdvice);
        verify(weeklyCallPlan).reEnroll(patient, treatmentAdvice);
    }

    @Test
    public void shouldCreateMapForTreatmentAdviceIdAndDosageTime(){
        TreatmentAdvice treatmentAdvice_1 = TreatmentAdviceBuilder.startRecording().withDrugDosages("11:45am", "05:50pm").withId("treatmentAdvice_1").build();
        TreatmentAdvice treatmentAdvice_2 = TreatmentAdviceBuilder.startRecording().withDrugDosages("09:00am", "09:00am").withId("treatmentAdvice_2").build();
        TreatmentAdvice treatmentAdvice_3 = TreatmentAdviceBuilder.startRecording().withDrugDosages("10:45am").withId("treatmentAdvice_3").build();

        when(allTreatmentAdvices.find_by_patient_id("patientDocId")).thenReturn(Arrays.asList(treatmentAdvice_1, treatmentAdvice_2, treatmentAdvice_3));

        Map<String, List<String>> result = treatmentAdviceService.getAllDrugTimeHistory("patientDocId");

        assertEquals(3, result.size());
        assertEquals(Arrays.asList("11:45am", "05:50pm"), result.get(treatmentAdvice_1.getId()));
        assertEquals(Arrays.asList("09:00am"), result.get(treatmentAdvice_2.getId()));
        assertEquals(Arrays.asList("10:45am"), result.get(treatmentAdvice_3.getId()));
    }
}
