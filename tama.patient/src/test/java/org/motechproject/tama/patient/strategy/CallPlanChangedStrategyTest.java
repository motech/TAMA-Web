package org.motechproject.tama.patient.strategy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.CallPlan;
import org.motechproject.tama.patient.service.Outbox;
import org.motechproject.tama.patient.service.registry.CallPlanRegistry;
import org.motechproject.tama.patient.service.registry.OutboxRegistry;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallPlanChangedStrategyTest {
    @Mock
    private CallPlan dailyCallPlan;
    @Mock
    private CallPlan weeklyCallPlan;
    @Mock
    private Outbox outbox;

    private CallPlanRegistry callPlanRegistry;
    private OutboxRegistry outboxRegistry;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        callPlanRegistry = new CallPlanRegistry();
        callPlanRegistry.registerCallPlan(CallPreference.DailyPillReminder, dailyCallPlan);
        callPlanRegistry.registerCallPlan(CallPreference.FourDayRecall, weeklyCallPlan);
        outboxRegistry = new OutboxRegistry();
        outboxRegistry.registerOutbox(outbox);
    }

    @Test
    public void patientChangesFromDailyToWeekly() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        final TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        new CallPlanChangedStrategy(callPlanRegistry, outboxRegistry).execute(dbPatient, patient, treatmentAdvice);

        assertNotNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox).reEnroll(dbPatient, patient);
        verify(dailyCallPlan).disEnroll(dbPatient, treatmentAdvice);
        verify(weeklyCallPlan).enroll(patient, treatmentAdvice);
    }

    @Test
    public void patientChangesFromWeeklyToDaily() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        final TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        new CallPlanChangedStrategy(callPlanRegistry, outboxRegistry).execute(dbPatient, patient, treatmentAdvice);

        assertNotNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox).reEnroll(dbPatient, patient);
        verify(weeklyCallPlan).disEnroll(dbPatient, treatmentAdvice);
        verify(dailyCallPlan).enroll(patient, treatmentAdvice);
    }
}
