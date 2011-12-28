package org.motechproject.tama.patient.strategy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;

import java.util.HashMap;
import java.util.Map;

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

    private Map<CallPreference, CallPlan> callPlans;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        callPlans = new HashMap<CallPreference, CallPlan>();
        callPlans.put(CallPreference.DailyPillReminder, dailyCallPlan);
        callPlans.put(CallPreference.FourDayRecall, weeklyCallPlan);
    }

    @Test
    public void patientChangesFromDailyToWeekly() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();

        new CallPlanChangedStrategy(callPlans, outbox).execute(dbPatient, patient, null);

        assertNotNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox).reEnroll(dbPatient, patient);
        verify(dailyCallPlan).disEnroll(dbPatient, null);
        verify(weeklyCallPlan).enroll(patient, null);
    }

    @Test
    public void patientChangesFromWeeklyToDaily() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();

        new CallPlanChangedStrategy(callPlans, outbox).execute(dbPatient, patient, null);

        assertNotNull(patient.getPatientPreferences().getCallPreferenceTransitionDate());
        verify(outbox).reEnroll(dbPatient, patient);
        verify(weeklyCallPlan).disEnroll(dbPatient, null);
        verify(dailyCallPlan).enroll(patient, null);
    }
}
