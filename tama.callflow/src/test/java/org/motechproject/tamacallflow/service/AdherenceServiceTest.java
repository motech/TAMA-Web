package org.motechproject.tamacallflow.service;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.tamacallflow.platform.service.FourDayRecallService;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;

public class AdherenceServiceTest {
    
    @Mock
    Patient patient;
    
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
        when(dailyReminderAdherenceService.getAdherenceForLastWeek(patientId, asOfDate)).thenReturn(98.0);
        AdherenceService adherenceService = new AdherenceService(dailyReminderAdherenceService, fourDayRecallService);
        assertTrue(adherenceService.isDosageMissedLastWeek(patient));
    }

    @Test
    public void shouldCheckForAnyMissedDosageInThePreviousWeekForFourDayRecallPatient() {
        final DateTime asOfDate = DateUtil.now();
        final String patientId = "patientId";
        when(patient.isOnDailyPillReminder()).thenReturn(false);
        when(patient.getId()).thenReturn(patientId);
        when(fourDayRecallService.getAdherencePercentageForPreviousWeek(patientId)).thenReturn(100);
        AdherenceService adherenceService = new AdherenceService(dailyReminderAdherenceService, fourDayRecallService);
        assertFalse(adherenceService.isDosageMissedLastWeek(patient));
    }
    
}
