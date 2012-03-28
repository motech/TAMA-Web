package org.motechproject.tama.patient.strategy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.service.CallPlan;
import org.motechproject.tama.patient.service.Outbox;
import org.motechproject.tama.patient.service.registry.CallPlanRegistry;
import org.motechproject.tama.patient.service.registry.OutboxRegistry;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class BestCallTimeChangedStrategyTest {
    @Mock
    private CallPlan dailyCallPlan;
    @Mock
    private CallPlan weeklyCallPlan;
    @Mock
    private Outbox outbox;
    private OutboxRegistry outboxRegistry;
    private CallPlanRegistry callPlanRegistry;

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
    public void dailyReminderPatient_ChangesHisBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(11, 0, TimeMeridiem.AM)).build();
        final TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        new BestCallTimeChangedStrategy(callPlanRegistry, outboxRegistry).execute(dbPatient, patient, treatmentAdvice);

        verify(outbox).reEnroll(dbPatient, patient);
        verify(dailyCallPlan, never()).reEnroll(patient, treatmentAdvice);
    }

    @Test
    public void weeklyReminderPatient_ChangesHisBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(11, 0, TimeMeridiem.AM)).build();
        final TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        new BestCallTimeChangedStrategy(callPlanRegistry, outboxRegistry).execute(dbPatient, patient, treatmentAdvice);

        verify(outbox).reEnroll(dbPatient, patient);
        verify(weeklyCallPlan).reEnroll(patient, treatmentAdvice);
    }

}
