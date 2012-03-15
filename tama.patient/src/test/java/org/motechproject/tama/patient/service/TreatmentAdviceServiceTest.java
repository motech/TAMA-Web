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

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
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
        verify(callTimeSlotService).freeSlots(patient, existingTreatmentAdvice);
        verify(callTimeSlotService).allotSlots(patient, treatmentAdvice);
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
        verify(callTimeSlotService).freeSlots(patient, existingTreatmentAdvice);
        verify(callTimeSlotService, times(0)).allotSlots(patient, treatmentAdvice);
    }
}
