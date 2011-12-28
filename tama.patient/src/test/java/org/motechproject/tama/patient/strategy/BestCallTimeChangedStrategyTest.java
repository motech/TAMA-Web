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

    private Map<CallPreference, CallPlan> callPlans;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        callPlans = new HashMap<CallPreference, CallPlan>();
        callPlans.put(CallPreference.DailyPillReminder, dailyCallPlan);
        callPlans.put(CallPreference.FourDayRecall, weeklyCallPlan);
    }

    @Test
    public void dailyReminderPatient_ChangesHisBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).withBestCallTime(new TimeOfDay(11, 0, TimeMeridiem.AM)).build();

        new BestCallTimeChangedStrategy(callPlans, outbox).execute(dbPatient, patient, null);

        verify(outbox).reEnroll(dbPatient, patient);
        verify(dailyCallPlan, never()).reEnroll(patient, null);
    }

    @Test
    public void weeklyReminderPatient_ChangesHisBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(11, 0, TimeMeridiem.AM)).build();

        new BestCallTimeChangedStrategy(callPlans, outbox).execute(dbPatient, patient, null);

        verify(outbox).reEnroll(dbPatient, patient);
        verify(weeklyCallPlan).reEnroll(patient, null);
    }
}
