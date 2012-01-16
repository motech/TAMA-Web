package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
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
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class WeeklyAdherenceLogServiceTest extends BaseUnitTest {

    private FourDayRecallDateService fourdayRecallDateService;
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;

    private LocalDate today;
    private Patient patient;
    private TreatmentAdvice treatmentAdvice;

    private WeeklyAdherenceLogService weeklyAdherenceLogsService;

    @Before
    public void setUp() {
        initMocks(this);
        setUpTime();
        setUpPatient();
        setUpTreatmentAdvice();

        fourdayRecallDateService = new FourDayRecallDateService();
        weeklyAdherenceLogsService = new WeeklyAdherenceLogService(allPatients, allTreatmentAdvices, allWeeklyAdherenceLogs, fourdayRecallDateService);
    }

    private void setUpTime() {
        today = new LocalDate(2011, 10, 7);
        mockCurrentDate(DateUtil.newDateTime(today, 9, 0, 0));
    }

    private void setUpPatient() {
        patient = PatientBuilder.startRecording().withId("patient_id").withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 10, TimeMeridiem.AM)).build();
        when(allPatients.get(patient.getId())).thenReturn(patient);
    }

    private void setUpTreatmentAdvice() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 10, 2)).build();
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);
    }

    @Test
    public void shouldCreateLogForPatient() {
        weeklyAdherenceLogsService.createLogFor(patient.getId(), 1);

        ArgumentCaptor<WeeklyAdherenceLog> weeklyAdherenceLogCaptor = ArgumentCaptor.forClass(WeeklyAdherenceLog.class);
        verify(allWeeklyAdherenceLogs).add(weeklyAdherenceLogCaptor.capture());

        WeeklyAdherenceLog weeklyAdherenceLog = weeklyAdherenceLogCaptor.getValue();
        assertWeeklyAdherenceLog(weeklyAdherenceLog);
    }

    @Test
    public void shouldNotModifyForPatientIfLogExistsOnSameDate() {
        when(allWeeklyAdherenceLogs.findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), new LocalDate(treatmentAdvice.getStartDate()))).thenReturn(new WeeklyAdherenceLog());

        weeklyAdherenceLogsService.createLogFor(patient.getId(), 1);
        verify(allWeeklyAdherenceLogs, never()).add(Matchers.<WeeklyAdherenceLog>any());
        verify(allWeeklyAdherenceLogs, never()).update(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldUpdateLogForPatientIfNotRespondedLogExistsOnSameDate() {
        WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog();
        weeklyAdherenceLog.setNotResponded(true);

        when(allWeeklyAdherenceLogs.findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), new LocalDate(treatmentAdvice.getStartDate()))).thenReturn(weeklyAdherenceLog);

        weeklyAdherenceLogsService.createLogFor(patient.getId(), 1);

        ArgumentCaptor<WeeklyAdherenceLog> logCaptor = ArgumentCaptor.forClass(WeeklyAdherenceLog.class);
        verify(allWeeklyAdherenceLogs).update(logCaptor.capture());
        assertFalse(logCaptor.getValue().getNotResponded());
        assertEquals(1, logCaptor.getValue().getNumberOfDaysMissed());
    }

    @Test
    public void shouldCreateLogOnGivenDate() {
        LocalDate logDate = new LocalDate(2011, 1, 1);
        when(allWeeklyAdherenceLogs.findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), logDate)).thenReturn(null);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);

        weeklyAdherenceLogsService.createLogOn(patient.getId(), logDate, 0);
        verify(allWeeklyAdherenceLogs).add(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldNotModifyLogOnGivenDateIfLogExistOnSameDate() {
        WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog(patient.getId(), treatmentAdvice.getId(), new LocalDate(2011, 1, 1), new LocalDate(2011, 1, 1), 0);
        weeklyAdherenceLog.setNotResponded(false);
        when(allWeeklyAdherenceLogs.findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), weeklyAdherenceLog.getLogDate())).thenReturn(weeklyAdherenceLog);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);

        weeklyAdherenceLogsService.createLogOn(patient.getId(), weeklyAdherenceLog.getLogDate(), 0);
        verify(allWeeklyAdherenceLogs, never()).add(Matchers.<WeeklyAdherenceLog>any());
        verify(allWeeklyAdherenceLogs, never()).update(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldUpdateLogOnGivenDateIfNotRespondedLogExistsOnSameDate(){
        WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog(patient.getId(), treatmentAdvice.getId(), new LocalDate(2011, 1, 1), new LocalDate(2011, 1, 1), 0);
        weeklyAdherenceLog.setNotResponded(true);
        when(allWeeklyAdherenceLogs.findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), weeklyAdherenceLog.getLogDate())).thenReturn(weeklyAdherenceLog);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);

        weeklyAdherenceLogsService.createLogOn(patient.getId(), weeklyAdherenceLog.getLogDate(), 0);
        verify(allWeeklyAdherenceLogs).update(Matchers.<WeeklyAdherenceLog>any());
    }

    private void assertWeeklyAdherenceLog(WeeklyAdherenceLog weeklyAdherenceLog) {
        assertEquals(today, weeklyAdherenceLog.getLogDate());
        assertEquals(1, weeklyAdherenceLog.getNumberOfDaysMissed());
        assertEquals(patient.getId(), weeklyAdherenceLog.getPatientId());
        assertEquals(treatmentAdvice.getId(), weeklyAdherenceLog.getTreatmentAdviceId());
        assertEquals(new LocalDate(treatmentAdvice.getStartDate()), weeklyAdherenceLog.getWeekStartDate());
    }
}
