package org.motechproject.tama.patient.strategy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TimeMeridiem;
import org.motechproject.tama.patient.domain.TimeOfDay;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
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
        changePatientPreferenceContext.executeStrategy(dbPatient, patient, null);

        assertNotNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(dailyCallPlan).disEnroll(dbPatient, null);
        verify(weeklyCallPlan).enroll(patient, null);
    }

    @Test
    public void shouldExecute_CallPlanChangedStrategy_WhenPatientChangesFromWeeklyToDaily() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();

        ChangePatientPreferenceContext changePatientPreferenceContext = new ChangePatientPreferenceContext(callPlans, outbox);
        changePatientPreferenceContext.executeStrategy(dbPatient, patient, null);

        assertNotNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(weeklyCallPlan).disEnroll(dbPatient, null);
        verify(dailyCallPlan).enroll(patient, null);
    }

    @Test
    public void shouldExecute_CallPlanChangedStrategy_WhenPatientChangesFromWeeklyToDaily_AndBestCallTimeChanges() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(11, 0, TimeMeridiem.AM)).build();

        ChangePatientPreferenceContext changePatientPreferenceContext = new ChangePatientPreferenceContext(callPlans, outbox);
        changePatientPreferenceContext.executeStrategy(dbPatient, patient, null);

        assertNotNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox).reEnroll(dbPatient, patient);
        verify(weeklyCallPlan).disEnroll(dbPatient, null);
        verify(dailyCallPlan).enroll(patient, null);
    }

    @Test
    public void shouldExecute_BestCallTimeChangedStrategy_WhenDailyReminderPatient_ChangesHisBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(11, 0, TimeMeridiem.AM)).build();

        ChangePatientPreferenceContext changePatientPreferenceContext = new ChangePatientPreferenceContext(callPlans, outbox);
        changePatientPreferenceContext.executeStrategy(dbPatient, patient, null);

        verify(outbox).reEnroll(dbPatient, patient);
    }

    @Test
    public void shouldExecute_BestCallTimeChangedStrategy_WhenWeeklyReminderPatient_ChangesHisBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(11, 0, TimeMeridiem.AM)).build();

        ChangePatientPreferenceContext changePatientPreferenceContext = new ChangePatientPreferenceContext(callPlans, outbox);
        changePatientPreferenceContext.executeStrategy(dbPatient, patient, null);

        verify(outbox).reEnroll(dbPatient, patient);
        verify(weeklyCallPlan).reEnroll(patient, null);
    }

    @Test
    public void shouldExecute_DayOfWeeklyCallChangedStrategy_WhenPatientChangesHisDayOfWeeklyCall() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withDayOfWeeklyCall(DayOfWeek.Saturday).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withDayOfWeeklyCall(DayOfWeek.Sunday).build();

        ChangePatientPreferenceContext changePatientPreferenceContext = new ChangePatientPreferenceContext(callPlans, outbox);
        changePatientPreferenceContext.executeStrategy(dbPatient, patient, null);

        verify(weeklyCallPlan).reEnroll(patient, null);
    }
}
