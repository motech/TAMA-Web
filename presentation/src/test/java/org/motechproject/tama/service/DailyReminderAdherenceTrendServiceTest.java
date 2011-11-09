package org.motechproject.tama.service;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.PatientAlertType;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.util.DosageUtil;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DosageUtil.class, DateUtil.class})
public class DailyReminderAdherenceTrendServiceTest {

    @Mock
    private PillReminderService pillReminderService;

    @Mock
    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    @Mock
    private PatientAlertService patientAlertService;
    @Mock
    PillRegimenResponse pillRegimenResponse;

    private DateTime dateTime = DateTime.now();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(DosageUtil.class);
        PowerMockito.mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(dateTime);
    }


    @Test
    public void shouldReportWhenAdherenceTrendIsFalling(){
        final String testPatientId = "testPatientId";
        DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService = new DailyReminderAdherenceTrendService(null, null, null) {
            @Override
            protected double getAdherencePercentageForCurrentWeek(String patientId) {
                if(patientId.equals(testPatientId))return 23.0;
                return 0.0;
            }

            @Override
            protected double getAdherencePercentageForLastWeek(String patientId) {
                if(patientId.equals(testPatientId)) return 43.0;
                return 0.0;
            }
        };
        assertTrue(dailyReminderAdherenceTrendService.isAdherenceFalling(testPatientId));
    }

    @Test
    public void shouldReturnAdhrenceTrendPercentage() {
        String externalId = "someExternalId";
        String pillRegimenId = "pillRegimenId";
        int successCountThisWeek = 25;
        int scheduledDosageCount = 100;
        Mockito.when(pillReminderService.getPillRegimen(Mockito.anyString())).thenReturn(pillRegimenResponse);
        Mockito.when(pillRegimenResponse.getPillRegimenId()).thenReturn(pillRegimenId);
        Mockito.when(allDosageAdherenceLogs.findScheduledDosagesSuccessCount(pillRegimenId, dateTime.minusWeeks(4).toLocalDate(), dateTime.toLocalDate())).thenReturn(successCountThisWeek);
        PowerMockito.when(DosageUtil.getScheduledDosagesTotalCount(Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(PillRegimenResponse.class))).thenReturn(scheduledDosageCount);

        DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService = new DailyReminderAdherenceTrendService(allDosageAdherenceLogs, pillReminderService, patientAlertService);
        assertEquals(0.25, dailyReminderAdherenceTrendService.getAdherencePercentage(externalId));
    }

    @Test
    public void shouldRaiseAlertWhenAdherenceIsFalling(){
        DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService = new DailyReminderAdherenceTrendService(allDosageAdherenceLogs, pillReminderService, patientAlertService) {
            @Override
            public boolean isAdherenceFalling(String patientId) {
                return true;
            }
        };
        final String patientId = "patientId";


        dailyReminderAdherenceTrendService.raiseAdherenceFallingAlert(patientId);
        verify(patientAlertService).createAlert(patientId, 3, "Falling Adherence", "Falling Adherence", PatientAlertType.FallingAdherence);
    }

    @Test
    public void shouldNotRaiseAlertWhenAdherenceIsNotFalling(){
        DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService = new DailyReminderAdherenceTrendService(allDosageAdherenceLogs, pillReminderService, patientAlertService) {
            @Override
            public boolean isAdherenceFalling(String patientId) {
                return false;
            }
        };
        final String patientId = "patientId";


        dailyReminderAdherenceTrendService.raiseAdherenceFallingAlert(patientId);
        verify(patientAlertService, never()).createAlert(patientId, 3, "Falling Adherence", "Falling Adherence", PatientAlertType.FallingAdherence);
    }

}
