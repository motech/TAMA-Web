package org.motechproject.tama.patient.strategy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.CallPlan;
import org.motechproject.tama.patient.service.registry.CallPlanRegistry;
import org.motechproject.tama.patient.service.registry.OutboxRegistry;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DayOfWeeklyCallChangedStrategyTest {
    @Mock
    private CallPlan dailyCallPlan;
    @Mock
    private CallPlan weeklyCallPlan;
    @Mock
    private OutboxRegistry outboxStrategy;

    private CallPlanRegistry callPlanRegistry;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        callPlanRegistry = new CallPlanRegistry();
        callPlanRegistry.registerCallPlan(CallPreference.DailyPillReminder, dailyCallPlan);
        callPlanRegistry.registerCallPlan(CallPreference.FourDayRecall, weeklyCallPlan);
    }

    @Test
    public void patientChangesHisDayOfWeeklyCall() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withDayOfWeeklyCall(DayOfWeek.Saturday).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withDayOfWeeklyCall(DayOfWeek.Sunday).build();
        final TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        new DayOfWeeklyCallChangedStrategy(callPlanRegistry, outboxStrategy).execute(dbPatient, patient, treatmentAdvice);

        verify(weeklyCallPlan).reEnroll(patient, treatmentAdvice);
    }
}
