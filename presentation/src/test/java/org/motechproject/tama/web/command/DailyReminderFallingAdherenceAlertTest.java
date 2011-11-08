package org.motechproject.tama.web.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.service.DailyReminderAdherenceTrendService;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class DailyReminderFallingAdherenceAlertTest {

    @Mock
    private DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;

    @Mock
    private AlertService alertService;

    private DailyReminderFallingAdherenceAlert dailyReminderFallingAdherenceAlert;

    @Before
    public void setUp(){
        initMocks(this);
        dailyReminderFallingAdherenceAlert = new DailyReminderFallingAdherenceAlert(dailyReminderAdherenceTrendService, alertService);
    }

    @Test
    public void shouldRaiseAlertWhenAdherenceIsFalling(){
        final String patientId = "patientId";
        TAMAIVRContextForTest ivrContext = new TAMAIVRContextForTest().patientId(patientId);

        when(dailyReminderAdherenceTrendService.isAdherenceFalling(patientId)).thenReturn(true);
        dailyReminderFallingAdherenceAlert.execute(ivrContext);
        verify(alertService).createAlert(Matchers.<Alert>any());
    }

    @Test
    public void shouldNotRaiseAlertWhenAdherenceIsNotFalling(){
        final String patientId = "patientId";
        TAMAIVRContextForTest ivrContext = new TAMAIVRContextForTest().patientId(patientId);

        when(dailyReminderAdherenceTrendService.isAdherenceFalling(patientId)).thenReturn(false);
        dailyReminderFallingAdherenceAlert.execute(ivrContext);
        verify(alertService, never()).createAlert(Matchers.<Alert>any());
    }
}
