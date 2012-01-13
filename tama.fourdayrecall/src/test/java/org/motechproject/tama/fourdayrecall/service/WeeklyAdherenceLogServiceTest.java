package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TimeMeridiem;
import org.motechproject.tama.patient.domain.TimeOfDay;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class WeeklyAdherenceLogServiceTest extends BaseUnitTest {

    private FourDayRecallDateService fourdayRecallDateService;
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;

    WeeklyAdherenceLogService weeklyAdherenceLogsService;
    private LocalDate today;

    @Before
    public void setUp() {
        initMocks(this);
        today = new LocalDate(2011, 10, 7);
        mockCurrentDate(DateUtil.newDateTime(today, 9, 0, 0));

        fourdayRecallDateService = new FourDayRecallDateService();
        weeklyAdherenceLogsService = new WeeklyAdherenceLogService(allPatients, allTreatmentAdvices, allWeeklyAdherenceLogs, fourdayRecallDateService);
    }


    @Test
    public void shouldCreateWeeklyAdherenceLog() {
        String patientId = "patient_id";
        Patient patient = PatientBuilder.startRecording().withId(patientId).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        LocalDate treatmentStartDate = new LocalDate(2011, 10, 2);
        String treatmentAdviceId = "treatmentAdviceId";
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId(treatmentAdviceId).withStartDate(treatmentStartDate).build();

        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        when(allPatients.get(patientId)).thenReturn(patient);

        weeklyAdherenceLogsService.createLogFor(patientId, 1);

        ArgumentCaptor<WeeklyAdherenceLog> weeklyAdherenceLogCaptor = ArgumentCaptor.forClass(WeeklyAdherenceLog.class);
        verify(allWeeklyAdherenceLogs).add(weeklyAdherenceLogCaptor.capture());

        WeeklyAdherenceLog weeklyAdherenceLog = weeklyAdherenceLogCaptor.getValue();

        assertEquals(today, weeklyAdherenceLog.getLogDate());
        assertEquals(1, weeklyAdherenceLog.getNumberOfDaysMissed());
        assertEquals(patientId, weeklyAdherenceLog.getPatientId());
        assertEquals(treatmentAdviceId, weeklyAdherenceLog.getTreatmentAdviceId());
        assertEquals(treatmentStartDate, weeklyAdherenceLog.getWeekStartDate());
    }
}
