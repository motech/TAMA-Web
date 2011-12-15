package org.motechproject.tamacallflow.service;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tamacallflow.platform.service.FourDayRecallService;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class AdherenceServiceTest {
    
    @Mock
    Patient patient;

    @Mock
    TreatmentAdvice treatmentAdvice;
    
    @Mock
    DailyReminderAdherenceService dailyReminderAdherenceService;
    
    @Mock
    FourDayRecallService fourDayRecallService;
    

    @Before
    public void setup() {
        initMocks(this);

    }
    
    @Test
    public void shouldCheckForAnyMissedDosageInThePreviousWeekForDailyReminderPatient() {
        final DateTime asOfDate = DateUtil.now();
        final String patientId = "patientId";
        when(patient.isOnDailyPillReminder()).thenReturn(true);
        when(patient.getId()).thenReturn(patientId);
        when(treatmentAdvice.getStartDate()).thenReturn(DateUtil.now().minusDays(3).toDate());
        when(dailyReminderAdherenceService.getAdherenceForLastWeekInPercentage(patientId, asOfDate)).thenReturn(98.0);
        AdherenceService adherenceService = new AdherenceService(dailyReminderAdherenceService, fourDayRecallService);
        assertTrue(adherenceService.isDosageMissedLastWeek(patient));
    }

    @Test
    public void shouldCheckForAnyMissedDosageInThePreviousWeekForFourDayRecallPatient() {
        final DateTime asOfDate = DateUtil.now();
        final String patientId = "patientId";
        when(patient.isOnDailyPillReminder()).thenReturn(false);
        when(patient.getId()).thenReturn(patientId);
        when(treatmentAdvice.getStartDate()).thenReturn(DateUtil.now().minusDays(20).toDate());
        when(fourDayRecallService.getAdherencePercentageForPreviousWeek(patientId)).thenReturn(100);
        when(fourDayRecallService.getFirstWeeksFourDayRecallRetryEndDate(patient)).thenReturn(DateUtil.now().minusDays(6));
        AdherenceService adherenceService = new AdherenceService(dailyReminderAdherenceService, fourDayRecallService);
        assertFalse(adherenceService.isDosageMissedLastWeek(patient));
    }

    @Test
    public void shouldNotCheckForAnyMissedDosageInThePreviousWeekForFourDayRecallPatientWhenARTIsInFirstWeek() {
        final DateTime asOfDate = DateUtil.now();
        final String patientId = "patientId";
        when(patient.isOnDailyPillReminder()).thenReturn(false);
        when(patient.getId()).thenReturn(patientId);
        when(treatmentAdvice.getStartDate()).thenReturn(DateUtil.now().minusDays(3).toDate());
        when(fourDayRecallService.getFirstWeeksFourDayRecallRetryEndDate(patient)).thenReturn(DateUtil.now().plusDays(6));
        when(fourDayRecallService.getAdherencePercentageForPreviousWeek(patientId)).thenReturn(0);
        AdherenceService adherenceService = new AdherenceService(dailyReminderAdherenceService, fourDayRecallService);
        assertFalse(adherenceService.isDosageMissedLastWeek(patient));
    }

    @Test
    public void shouldNotCheckForAnyMissedDosageInThePreviousWeekForFourDayRecallPatientWhenARTIsInFirstWeekButAdherenceIsCaptured() {
        final DateTime asOfDate = DateUtil.now();
        final String patientId = "patientId";
        when(patient.isOnDailyPillReminder()).thenReturn(false);
        when(treatmentAdvice.getStartDate()).thenReturn(DateUtil.now().minusDays(3).toDate());
        when(patient.getId()).thenReturn(patientId);
        when(fourDayRecallService.getAdherencePercentageForPreviousWeek(patientId)).thenReturn(23);
        when(fourDayRecallService.getFirstWeeksFourDayRecallRetryEndDate(patient)).thenReturn(DateUtil.now().plusDays(6));
        AdherenceService adherenceService = new AdherenceService(dailyReminderAdherenceService, fourDayRecallService);
        assertTrue(adherenceService.isDosageMissedLastWeek(patient));
    }
    
}
