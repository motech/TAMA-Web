package org.motechproject.tamacallflow.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tamacallflow.util.EmptyMapMatcher;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.domain.PatientAlert;
import org.motechproject.tamadomain.domain.PatientAlertType;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DateUtil.class})
public class DailyReminderAdherenceTrendServiceTest {

    @Mock
    private PatientAlertService patientAlertService;

    @Mock
    private PillRegimenResponse pillRegimenResponse;

    @Mock
    private DailyReminderAdherenceService dailyReminderAdherenceService;

    private DateTime dateTime = DateTime.now();

    private DailyReminderAdherenceTrendService service;

    private EmptyMapMatcher emptyMapMatcher;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        emptyMapMatcher = new EmptyMapMatcher();
        service = new DailyReminderAdherenceTrendService(patientAlertService, dailyReminderAdherenceService);
    }

    @Test
    public void shouldRaiseAlertWhenAdherenceIsFalling(){
        DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService = new DailyReminderAdherenceTrendService(patientAlertService, dailyReminderAdherenceService);
        final String patientId = "patientId";
        DateTime now = DateUtil.now();
        when(dailyReminderAdherenceService.getAdherenceInPercentage("patientId", now.minusWeeks(1))).thenReturn(30.0);
        when(dailyReminderAdherenceService.getAdherenceInPercentage("patientId", now)).thenReturn(20.0);
        dailyReminderAdherenceTrendService.raiseAlertIfAdherenceTrendIsFalling(patientId, now);
        verify(patientAlertService).createAlert(eq(patientId), eq(0), eq("Falling Adherence"), eq("Adherence fell by 33.33%, from 30.00% to 20.00%"), eq(PatientAlertType.FallingAdherence), argThat(emptyMapMatcher));
    }

    @Test
    public void shouldNotRaiseAlertWhenAdherenceIsNotFalling(){
        DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService = new DailyReminderAdherenceTrendService(patientAlertService, dailyReminderAdherenceService) {
            @Override
            public boolean isAdherenceFallingAsOf(String patientId, DateTime asOf) {
                return false;
            }
        };
        final String patientId = "patientId";

        dailyReminderAdherenceTrendService.raiseAlertIfAdherenceTrendIsFalling(patientId, DateUtil.now());
        verify(patientAlertService, never()).createAlert(eq(patientId), eq(0), eq("Falling Adherence"), Matchers.<String>any(), eq(PatientAlertType.FallingAdherence), argThat(emptyMapMatcher));
    }

    @Test
    public void shouldRaiseRedAlertForThePatient() {
        DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService = new DailyReminderAdherenceTrendService(patientAlertService, dailyReminderAdherenceService);
        String patientId = "patientId";
        double adherencePercentage = 69.9;

        dailyReminderAdherenceTrendService.raiseAdherenceInRedAlert(patientId, adherencePercentage);

        Map<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ADHERENCE, adherencePercentage+"");
        verify(patientAlertService).createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, "Adherence in Red", "Adherence percentage is 69.90%", PatientAlertType.AdherenceInRed, data);
    }
}
