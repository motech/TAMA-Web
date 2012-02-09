package org.motechproject.tama.patient.strategy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChangePatientPreferenceContextTest {
    @Mock
    private CallPlan dailyCallPlan;
    @Mock
    private CallPlan weeklyCallPlan;
    @Mock
    private Outbox outbox;

    private Map<CallPreference, CallPlan> callPlans;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        callPlans = new HashMap<CallPreference, CallPlan>();
        callPlans.put(CallPreference.DailyPillReminder, dailyCallPlan);
        callPlans.put(CallPreference.FourDayRecall, weeklyCallPlan);
    }

    @Test
    public void shouldExecute_CallPlanChangedStrategy_WhenPatientChangesFromDailyToWeekly() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();

        ChangePatientPreferenceContext changePatientPreferenceContext = new ChangePatientPreferenceContext(callPlans, outbox);
        ChangePatientPreferenceStrategy strategy = changePatientPreferenceContext.getStrategy(dbPatient, patient);

        assertEquals(CallPlanChangedStrategy.class, strategy.getClass());
    }

    @Test
    public void shouldExecute_CallPlanChangedStrategy_WhenPatientChangesFromWeeklyToDaily() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();

        ChangePatientPreferenceContext changePatientPreferenceContext = new ChangePatientPreferenceContext(callPlans, outbox);
        ChangePatientPreferenceStrategy strategy = changePatientPreferenceContext.getStrategy(dbPatient, patient);

        assertEquals(CallPlanChangedStrategy.class, strategy.getClass());
    }

    @Test
    public void shouldExecute_CallPlanChangedStrategy_WhenPatientChangesFromWeeklyToDaily_AndBestCallTimeChanges() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(11, 0, TimeMeridiem.AM)).build();

        ChangePatientPreferenceContext changePatientPreferenceContext = new ChangePatientPreferenceContext(callPlans, outbox);
        ChangePatientPreferenceStrategy strategy = changePatientPreferenceContext.getStrategy(dbPatient, patient);

        assertEquals(CallPlanChangedStrategy.class, strategy.getClass());
    }

    @Test
    public void shouldExecute_BestCallTimeChangedStrategy_WhenDailyReminderPatient_ChangesHisBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(11, 0, TimeMeridiem.AM)).build();

        ChangePatientPreferenceContext changePatientPreferenceContext = new ChangePatientPreferenceContext(callPlans, outbox);
        ChangePatientPreferenceStrategy strategy = changePatientPreferenceContext.getStrategy(dbPatient, patient);

        assertEquals(BestCallTimeChangedStrategy.class, strategy.getClass());
    }

    @Test
    public void shouldExecute_BestCallTimeChangedStrategy_WhenWeeklyReminderPatient_ChangesHisBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(11, 0, TimeMeridiem.AM)).build();

        ChangePatientPreferenceContext changePatientPreferenceContext = new ChangePatientPreferenceContext(callPlans, outbox);
        ChangePatientPreferenceStrategy strategy = changePatientPreferenceContext.getStrategy(dbPatient, patient);

        assertEquals(BestCallTimeChangedStrategy.class, strategy.getClass());
    }

    @Test
    public void shouldExecute_BestCallTimeChangedStrategy_WhenWeeklyReminderPatient_ChangesHisBestCallTime_AndDayOfWeeklyCall() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(11, 0, TimeMeridiem.AM)).build();

        ChangePatientPreferenceContext changePatientPreferenceContext = new ChangePatientPreferenceContext(callPlans, outbox);
        ChangePatientPreferenceStrategy strategy = changePatientPreferenceContext.getStrategy(dbPatient, patient);

        assertEquals(BestCallTimeChangedStrategy.class, strategy.getClass());
    }

    @Test
    public void shouldExecute_DayOfWeeklyCallChangedStrategy_WhenPatientChangesHisDayOfWeeklyCall() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withDayOfWeeklyCall(DayOfWeek.Saturday).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withDayOfWeeklyCall(DayOfWeek.Sunday).build();

        ChangePatientPreferenceContext changePatientPreferenceContext = new ChangePatientPreferenceContext(callPlans, outbox);
        ChangePatientPreferenceStrategy strategy = changePatientPreferenceContext.getStrategy(dbPatient, patient);

        assertEquals(DayOfWeeklyCallChangedStrategy.class, strategy.getClass());
    }

    @Test
    public void shouldReturn_NoStrategy_When_PatientPreferencesAreNotChanged() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withDayOfWeeklyCall(DayOfWeek.Saturday).build();

        ChangePatientPreferenceContext changePatientPreferenceContext = new ChangePatientPreferenceContext(callPlans, outbox);
        ChangePatientPreferenceStrategy strategy = changePatientPreferenceContext.getStrategy(dbPatient, dbPatient);

        assertNull(strategy);
    }
}
