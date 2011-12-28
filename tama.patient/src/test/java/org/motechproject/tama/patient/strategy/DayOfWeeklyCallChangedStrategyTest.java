package org.motechproject.tama.patient.strategy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DayOfWeeklyCallChangedStrategyTest {
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
    public void patientChangesHisDayOfWeeklyCall() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withDayOfWeeklyCall(DayOfWeek.Saturday).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).withDayOfWeeklyCall(DayOfWeek.Sunday).build();

        new DayOfWeeklyCallChangedStrategy(callPlans, outbox).execute(dbPatient, patient, null);

        verify(weeklyCallPlan).reEnroll(patient, null);
    }
}
