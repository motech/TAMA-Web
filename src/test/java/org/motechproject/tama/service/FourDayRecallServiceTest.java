package org.motechproject.tama.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.domain.WeeklyAdherenceLog;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class FourDayRecallServiceTest {

    public static final String PATIENT_ID = "patientId";

    @Mock
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private TreatmentAdvice treatmentAdvice;

    private FourDayRecallService fourDayRecallService;

    @Before
    public void setUp() {
        initMocks(this);
        fourDayRecallService = new FourDayRecallService(allWeeklyAdherenceLogs, allTreatmentAdvices);
    }

    @Test
    public void shouldReturnTrueIfAdherenceIsCapturedForCurrentWeek() {
        String treatmentAdviceId = "treatmentAdviceId";
        LocalDate today = new LocalDate(2011, 10, 19);
        LocalDate startDateOfTreatmentAdvice = new LocalDate(2011, 10, 5);
        LocalDate startDateForWeek = new LocalDate(2011, 10, 12);

        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.pastDateWith(DayOfWeek.getDayOfWeek(startDateOfTreatmentAdvice.dayOfWeek().get()), startDateOfTreatmentAdvice.plusDays(4))).thenReturn(startDateForWeek);
        when(allWeeklyAdherenceLogs.findLogCountByPatientIDAndTreatmentAdviceIdAndDateRange(PATIENT_ID, treatmentAdviceId, startDateForWeek, today)).thenReturn(1);

        boolean capturedForCurrentWeek = fourDayRecallService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, treatmentAdviceId, startDateOfTreatmentAdvice);

        assertTrue(capturedForCurrentWeek);
    }

    @Test
    public void shouldReturnFalseIfAdherenceIsNotCapturedForCurrentWeek() {
        String treatmentAdviceId = "treatmentAdviceId";
        LocalDate today = new LocalDate(2011, 10, 19);
        LocalDate startDateOfTreatmentAdvice = new LocalDate(2011, 10, 5);
        LocalDate startDateForWeek = new LocalDate(2011, 10, 12);

        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.pastDateWith(DayOfWeek.getDayOfWeek(startDateOfTreatmentAdvice.dayOfWeek().get()), startDateOfTreatmentAdvice.plusDays(4))).thenReturn(startDateForWeek);
        when(allWeeklyAdherenceLogs.findLogCountByPatientIDAndTreatmentAdviceIdAndDateRange(PATIENT_ID, treatmentAdviceId, startDateForWeek, today)).thenReturn(0);

        boolean capturedForCurrentWeek = fourDayRecallService.isAdherenceCapturedForCurrentWeek(PATIENT_ID, treatmentAdviceId, startDateOfTreatmentAdvice);

        assertFalse(capturedForCurrentWeek);
    }

    @Test
    public void shouldGetAdherenceLogForPreviousWeek() {
        String treatmentAdviceID = "treatmentAdviceID";
        LocalDate today = new LocalDate(2011, 10, 19);
        LocalDate startDateOfTreatmentAdvice = new LocalDate(2011, 10, 5);
        LocalDate startDateForWeek = new LocalDate(2011, 10, 12);
        LocalDate logDate = DateUtil.newDate(2011, 10, 10);
        List<WeeklyAdherenceLog> logs = new ArrayList<WeeklyAdherenceLog>();
        logs.add(new WeeklyAdherenceLog(PATIENT_ID, logDate, 2, treatmentAdviceID));

        mockStatic(DateUtil.class);

        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.pastDateWith(DayOfWeek.getDayOfWeek(startDateOfTreatmentAdvice.dayOfWeek().get()), startDateOfTreatmentAdvice.plusDays(4))).thenReturn(startDateForWeek);
        when(DateUtil.newDate(startDateOfTreatmentAdvice.toDate())).thenReturn(new LocalDate(startDateOfTreatmentAdvice));
        when(allWeeklyAdherenceLogs.findByDateRange(PATIENT_ID, treatmentAdviceID, new LocalDate(2011, 10, 9), today.minusDays(1))).thenReturn(logs);
        when(allTreatmentAdvices.findByPatientId(PATIENT_ID)).thenReturn(treatmentAdvice);
        when(treatmentAdvice.getStartDate()).thenReturn(startDateOfTreatmentAdvice.toDate());
        when(treatmentAdvice.getId()).thenReturn(treatmentAdviceID);

        WeeklyAdherenceLog logForPreviousWeek = fourDayRecallService.getAdherenceLogForPreviousWeek(PATIENT_ID);

        assertNotNull(logForPreviousWeek);
        assertEquals(logDate, logForPreviousWeek.getLogDate());
        assertEquals(PATIENT_ID, logForPreviousWeek.getPatientId());
        assertEquals(treatmentAdviceID, logForPreviousWeek.getTreatmentAdviceId());
    }
    
    
    @Test
    public void shouldReturnTrueIfLogsAreBeingCapturedForFirstWeek(){
        LocalDate startDateOfTreatmentAdvice = new LocalDate(2011, 10, 5);
        LocalDate today = new LocalDate(2011, 10, 10);
        mockStatic(DateUtil.class);

        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.pastDateWith(DayOfWeek.getDayOfWeek(startDateOfTreatmentAdvice.dayOfWeek().get()), startDateOfTreatmentAdvice.plusDays(4))).thenReturn(startDateOfTreatmentAdvice);
        when(DateUtil.newDate(startDateOfTreatmentAdvice.toDate())).thenReturn(new LocalDate(startDateOfTreatmentAdvice));

        when(allTreatmentAdvices.findByPatientId(PATIENT_ID)).thenReturn(treatmentAdvice);
        when(treatmentAdvice.getStartDate()).thenReturn(startDateOfTreatmentAdvice.toDate());
        boolean adherenceBeingCapturedForFirstWeek = fourDayRecallService.isAdherenceBeingCapturedForFirstWeek(PATIENT_ID);
        assertTrue(adherenceBeingCapturedForFirstWeek);
    }


    @Test
    public void shouldReturnFalseIfLogsAreBeingCapturedForFirstWeek(){
        LocalDate startDateOfTreatmentAdvice = new LocalDate(2011, 10, 5);
        LocalDate startDateOfCurrentWeek = new LocalDate(2011, 10, 12);
        LocalDate today = new LocalDate(2011, 10, 15);
        mockStatic(DateUtil.class);

        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.pastDateWith(DayOfWeek.getDayOfWeek(startDateOfTreatmentAdvice.dayOfWeek().get()), startDateOfTreatmentAdvice.plusDays(4))).thenReturn(startDateOfCurrentWeek);
        when(DateUtil.newDate(startDateOfTreatmentAdvice.toDate())).thenReturn(new LocalDate(startDateOfTreatmentAdvice));

        when(allTreatmentAdvices.findByPatientId(PATIENT_ID)).thenReturn(treatmentAdvice);
        when(treatmentAdvice.getStartDate()).thenReturn(startDateOfTreatmentAdvice.toDate());
        boolean adherenceBeingCapturedForFirstWeek = fourDayRecallService.isAdherenceBeingCapturedForFirstWeek(PATIENT_ID);
        assertFalse(adherenceBeingCapturedForFirstWeek);
    }
}