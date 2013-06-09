package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.reporting.WeeklyAdherenceMapper;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.reporting.service.WeeklyPatientReportingService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Mock
    private WeeklyPatientReportingService weeklyPatientReportingService;
    @Mock
    private WeeklyAdherenceMapper weeklyAdherenceMapper;

    @Mock
    private AllRegimens allRegimens;

    @Before
    public void setUp() {
        initMocks(this);
        setUpTime();
        setUpPatient();
        setUpTreatmentAdvice();

        fourdayRecallDateService = new FourDayRecallDateService();
        weeklyAdherenceLogsService = new WeeklyAdherenceLogService(allPatients, allTreatmentAdvices, allWeeklyAdherenceLogs, fourdayRecallDateService, weeklyPatientReportingService, weeklyAdherenceMapper, allRegimens);
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
        weeklyAdherenceLogsService.createLog(patient.getId(), 1);

        ArgumentCaptor<WeeklyAdherenceLog> weeklyAdherenceLogCaptor = ArgumentCaptor.forClass(WeeklyAdherenceLog.class);
        verify(allWeeklyAdherenceLogs).add(weeklyAdherenceLogCaptor.capture());

        WeeklyAdherenceLog weeklyAdherenceLog = weeklyAdherenceLogCaptor.getValue();
        assertWeeklyAdherenceLog(weeklyAdherenceLog);
    }

    @Test
    public void shouldUpdateLogForPatientIfNotRespondedLogExistsOnSameDate() {
        WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog();
        weeklyAdherenceLog.setNotResponded(true);

        when(allWeeklyAdherenceLogs.findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), new LocalDate(treatmentAdvice.getStartDate()))).thenReturn(weeklyAdherenceLog);

        weeklyAdherenceLogsService.createLog(patient.getId(), 1);

        ArgumentCaptor<WeeklyAdherenceLog> logCaptor = ArgumentCaptor.forClass(WeeklyAdherenceLog.class);
        verify(allWeeklyAdherenceLogs).update(logCaptor.capture());
        assertFalse(logCaptor.getValue().getNotResponded());
        assertEquals(1, logCaptor.getValue().getNumberOfDaysMissed());
    }

    @Test
    public void shouldNotModifyLogForPatientIfRespondedLogExistsOnSameDate() {
        when(allWeeklyAdherenceLogs.findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), new LocalDate(treatmentAdvice.getStartDate()))).thenReturn(new WeeklyAdherenceLog());

        weeklyAdherenceLogsService.createLog(patient.getId(), 1);
        verify(allWeeklyAdherenceLogs, never()).add(Matchers.<WeeklyAdherenceLog>any());
        verify(allWeeklyAdherenceLogs, never()).update(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldCreateLogOnGivenDate() {
        LocalDate weekStartDate = new LocalDate(2011, 1, 1);
        when(allWeeklyAdherenceLogs.findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), weekStartDate)).thenReturn(null);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);

        weeklyAdherenceLogsService.createLog(patient.getId(), weekStartDate, 0);
        verify(allWeeklyAdherenceLogs).add(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldNotModifyLogOnGivenWeekStartDateIfRespondedLogExistOnSameDate() {
        WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog(patient.getId(), treatmentAdvice.getId(), new LocalDate(2011, 1, 1), DateUtil.newDateTime(DateUtil.newDate(2011, 1, 1), 0, 0, 0), 0);
        weeklyAdherenceLog.setNotResponded(false);
        when(allWeeklyAdherenceLogs.findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), weeklyAdherenceLog.getWeekStartDate())).thenReturn(weeklyAdherenceLog);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);

        weeklyAdherenceLogsService.createLog(patient.getId(), weeklyAdherenceLog.getWeekStartDate(), 0);
        verify(allWeeklyAdherenceLogs, never()).add(Matchers.<WeeklyAdherenceLog>any());
        verify(allWeeklyAdherenceLogs, never()).update(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldUpdateLogOnGivenWeekStartDateIfNotRespondedLogExistsOnSameDate() {
        WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog(patient.getId(), treatmentAdvice.getId(), new LocalDate(2011, 1, 1), DateUtil.newDateTime(DateUtil.newDate(2011, 1, 1), 0, 0, 0), 0);
        weeklyAdherenceLog.setNotResponded(true);
        when(allWeeklyAdherenceLogs.findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), weeklyAdherenceLog.getWeekStartDate())).thenReturn(weeklyAdherenceLog);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);

        weeklyAdherenceLogsService.createLog(patient.getId(), weeklyAdherenceLog.getWeekStartDate(), 0);
        verify(allWeeklyAdherenceLogs).update(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldNotModifyLogWithLogDateIfLogExistOnSameWeekStartDate() {
        LocalDate weekStartDate = new LocalDate(2011, 1, 1);
        DateTime logDate = DateUtil.newDateTime(weekStartDate.plusDays(4), 0, 0, 0);

        WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog(patient.getId(), treatmentAdvice.getId(), weekStartDate, logDate, 0);
        weeklyAdherenceLog.setNotResponded(false);

        when(allWeeklyAdherenceLogs.findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), weekStartDate)).thenReturn(weeklyAdherenceLog);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);

        weeklyAdherenceLogsService.createLog(patient.getId(), weekStartDate, 0, logDate);
        verify(allWeeklyAdherenceLogs, never()).add(Matchers.<WeeklyAdherenceLog>any());
        verify(allWeeklyAdherenceLogs, never()).update(Matchers.<WeeklyAdherenceLog>any());
    }

    @Test
    public void shouldUpdateLogWithLogDateIfNotRespondedLogExistsOnSameWeekStartDate() {
        LocalDate weekStartDate = new LocalDate(2011, 1, 1);
        DateTime logDate = DateUtil.newDateTime(weekStartDate.plusDays(4), 0, 0, 0);
        FourDayRecallDateService fourDayRecallDateService = mock(FourDayRecallDateService.class);
        mockCurrentDate(logDate);

        WeeklyAdherenceLog weeklyAdherenceLog = new WeeklyAdherenceLog(patient.getId(), treatmentAdvice.getId(), weekStartDate, logDate, 0);
        weeklyAdherenceLog.setNotResponded(true);

        when(fourDayRecallDateService.treatmentWeekStartDate(logDate.toLocalDate(), patient, treatmentAdvice)).thenReturn(weekStartDate);

        when(allWeeklyAdherenceLogs.findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), weekStartDate)).thenReturn(weeklyAdherenceLog);
        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);

        new WeeklyAdherenceLogService(allPatients, allTreatmentAdvices, allWeeklyAdherenceLogs, fourDayRecallDateService, weeklyPatientReportingService, weeklyAdherenceMapper, allRegimens).createNotRespondedLog(patient.getId());
        verify(allWeeklyAdherenceLogs).update(Matchers.<WeeklyAdherenceLog>any());
    }

    private void assertWeeklyAdherenceLog(WeeklyAdherenceLog weeklyAdherenceLog) {
        assertEquals(today, weeklyAdherenceLog.getLogDate().toLocalDate());
        assertEquals(1, weeklyAdherenceLog.getNumberOfDaysMissed());
        assertEquals(patient.getId(), weeklyAdherenceLog.getPatientId());
        assertEquals(treatmentAdvice.getId(), weeklyAdherenceLog.getTreatmentAdviceId());
        assertEquals(new LocalDate(treatmentAdvice.getStartDate()), weeklyAdherenceLog.getWeekStartDate());
    }
}
