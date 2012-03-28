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

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientPreferenceChangedStrategyFactoryTest {

    @Mock
    CallPlanChangedStrategy callPlanChangedStrategy;
    @Mock
    BestCallTimeChangedStrategy bestCallTimeChangedStrategy;
    @Mock
    DayOfWeeklyCallChangedStrategy dayOfWeeklyCallChangedStrategy;

    PatientPreferenceChangedStrategyFactory preferenceChangedStrategyFactory;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        preferenceChangedStrategyFactory = new PatientPreferenceChangedStrategyFactory(callPlanChangedStrategy, bestCallTimeChangedStrategy, dayOfWeeklyCallChangedStrategy);
    }

    @Test
    public void shouldReturnCallPlanChangedStrategy_WhenPatientChangesHisCallPlan() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        PatientPreferenceChangedStrategy strategy = preferenceChangedStrategyFactory.getStrategy(changedPatientPreferenceContext);

        assertEquals(callPlanChangedStrategy, strategy);
    }

    @Test
    public void shouldReturnBestCallTimeChangedStrategy_WhenPatientChangesHisBestCallTime() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withBestCallTime(new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withBestCallTime(new TimeOfDay(11, 10, TimeMeridiem.AM)).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        PatientPreferenceChangedStrategy strategy = preferenceChangedStrategyFactory.getStrategy(changedPatientPreferenceContext);

        assertEquals(bestCallTimeChangedStrategy, strategy);
    }

    @Test
    public void shouldReturnDayOfWeeklyCallChangedStrategyWhenPatientChangesHisDayOfWeeklyCall() {
        Patient dbPatient = PatientBuilder.startRecording().withDefaults().withDayOfWeeklyCall(DayOfWeek.Saturday).build();
        Patient patient = PatientBuilder.startRecording().withDefaults().withDayOfWeeklyCall(DayOfWeek.Sunday).build();

        ChangedPatientPreferenceContext changedPatientPreferenceContext = new ChangedPatientPreferenceContext(dbPatient, patient);
        PatientPreferenceChangedStrategy strategy = preferenceChangedStrategyFactory.getStrategy(changedPatientPreferenceContext);

        assertEquals(dayOfWeeklyCallChangedStrategy, strategy);
    }

}
