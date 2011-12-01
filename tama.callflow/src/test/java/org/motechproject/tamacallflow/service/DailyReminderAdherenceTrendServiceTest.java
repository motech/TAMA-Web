package org.motechproject.tamacallflow.service;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.util.DosageUtil;
import org.motechproject.tamacallflow.util.EmptyMapMatcher;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.domain.PatientAlert;
import org.motechproject.tamadomain.domain.PatientAlertType;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
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
    EmptyMapMatcher emptyMapMatcher;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(DosageUtil.class);
        PowerMockito.mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(dateTime);
        emptyMapMatcher = new EmptyMapMatcher();
    }


    @Test
    public void shouldReportWhenAdherenceTrendIsFalling(){
        final String testPatientId = "testPatientId";
        DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService = new DailyReminderAdherenceTrendService(allDosageAdherenceLogs, pillReminderService, patientAlertService) {
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
        PowerMockito.when(DosageUtil.getScheduledDosagesTotalCountForLastFourWeeks(Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(PillRegimenResponse.class))).thenReturn(scheduledDosageCount);

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

            @Override
            public double getAdherencePercentage(String patientId) {
                return 20.0;
            }

            @Override
            protected double getAdherencePercentage(String patientId, DateTime asOfDate) {
                return 30.0;
            }
        };
        final String patientId = "patientId";


        dailyReminderAdherenceTrendService.raiseAdherenceFallingAlert(patientId);
        verify(patientAlertService).createAlert(eq(patientId), eq(0), eq("Falling Adherence"), eq("Adherence fell by 0.00%, from 30.00% to 30.00%"), eq(PatientAlertType.FallingAdherence), argThat(emptyMapMatcher));
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
        verify(patientAlertService, never()).createAlert(eq(patientId), eq(0), eq("Falling Adherence"), Matchers.<String>any(), eq(PatientAlertType.FallingAdherence), argThat(emptyMapMatcher));
    }

    @Test
    public void shouldRaiseRedAlertForThePatient() {
        DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService = new DailyReminderAdherenceTrendService(allDosageAdherenceLogs, pillReminderService, patientAlertService);
        String patientId = "patientId";
        double adherencePercentage = 69.9;

        dailyReminderAdherenceTrendService.raiseRedAlert(patientId, adherencePercentage);

        Map<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ADHERENCE, adherencePercentage+"");
        verify(patientAlertService).createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, "Adherence in Red", "Adherence percentage is 69.90%", PatientAlertType.AdherenceInRed, data);
    }

}
