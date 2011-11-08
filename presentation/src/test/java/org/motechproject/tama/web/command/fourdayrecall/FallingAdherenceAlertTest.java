package org.motechproject.tama.web.command.fourdayrecall;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.platform.service.FourDayRecallService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class FallingAdherenceAlertTest {

    @Mock
    private AlertService alertService;

    @Mock
    private FourDayRecallService fourDayRecallService;

    private FallingAdherenceAlert alertCommand;

    @Before
    public void setUp() {
        initMocks(this);
        alertCommand = new FallingAdherenceAlert(fourDayRecallService, alertService);
    }

    @Test
    public void shouldAddAlertIfAdherenceIsFalling() {
       final String patientId =  "patientId";
       TAMAIVRContext context = new TAMAIVRContextForTest().patientId(patientId).dtmfInput("2");
       when(fourDayRecallService.isAdherenceFalling(2, patientId)).thenReturn(true);
       alertCommand.execute(context);
       verify(alertService).createAlert(Matchers.<Alert>any());
    }

}
