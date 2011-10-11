package org.motechproject.tama.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class FourDayRecallServiceTest {
    private FourDayRecallService fourDayRecallService;

    @Mock
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldReturnTrueIfAdherenceIsCapturedForCurrentWeek() {
        String patientId = "patientId";
        String treatmentAdviceId = "treatmentAdviceId";
        LocalDate today = new LocalDate(2011, 10, 19);
        LocalDate startDateOfTreatmentAdvice = new LocalDate(2011, 10, 5);
        LocalDate startDateForWeek = new LocalDate(2011, 10, 12);

        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.pastDateWith(DayOfWeek.getDayOfWeek(startDateOfTreatmentAdvice.dayOfWeek().get()), startDateOfTreatmentAdvice.plusDays(4))).thenReturn(startDateForWeek);
        when(allWeeklyAdherenceLogs.findLogCountByPatientIDAndTreatmentAdviceIdAndDateRange(patientId, treatmentAdviceId, startDateForWeek, today)).thenReturn(1);

        fourDayRecallService = new FourDayRecallService(allWeeklyAdherenceLogs);
        boolean capturedForCurrentWeek = fourDayRecallService.isAdherenceCapturedForCurrentWeek(patientId, treatmentAdviceId, startDateOfTreatmentAdvice);

        assertTrue(capturedForCurrentWeek);
    }

    @Test
    public void shouldReturnFalseIfAdherenceIsNotCapturedForCurrentWeek() {
        String patientId = "patientId";
        String treatmentAdviceId = "treatmentAdviceId";
        LocalDate today = new LocalDate(2011, 10, 19);
        LocalDate startDateOfTreatmentAdvice = new LocalDate(2011, 10, 5);
        LocalDate startDateForWeek = new LocalDate(2011, 10, 12);

        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.pastDateWith(DayOfWeek.getDayOfWeek(startDateOfTreatmentAdvice.dayOfWeek().get()), startDateOfTreatmentAdvice.plusDays(4))).thenReturn(startDateForWeek);
        when(allWeeklyAdherenceLogs.findLogCountByPatientIDAndTreatmentAdviceIdAndDateRange(patientId, treatmentAdviceId, startDateForWeek, today)).thenReturn(0);

        fourDayRecallService = new FourDayRecallService(allWeeklyAdherenceLogs);
        boolean capturedForCurrentWeek = fourDayRecallService.isAdherenceCapturedForCurrentWeek(patientId, treatmentAdviceId, startDateOfTreatmentAdvice);

        assertFalse(capturedForCurrentWeek);
    }
}
