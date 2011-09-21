package org.motechproject.tama.web.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomReportingAlertsCommandTest {
    private IVRContext context;
    @Mock
    private IVRRequest ivrRequest;
    @Mock
    private IVRSession ivrSession;
    @Mock
    private AlertService alertService;

    @Before
    public void setup() {
        initMocks(this);
        context = new IVRContext(ivrRequest, ivrSession);
        when(ivrSession.getExternalId()).thenReturn("dummyPatientId");
    }

    @Test
    public void shouldCallAlertServiceToCreateANewAlert() {
        SymptomReportingAlertsCommand command = new SymptomReportingAlertsCommand(alertService);
        final ITreeCommand iTreeCommand = command.symptomReportingAlertWithPriority(1);
        iTreeCommand.execute(context);

        final ArgumentMatcher<Alert> alertArgumentMatcher = new ArgumentMatcher<Alert>(){
            @Override
            public boolean matches(Object testAlert) {
                Alert alert = (Alert)testAlert;
                return alert.getAlertType().equals(AlertType.MEDIUM)
               && alert.getExternalId().equals("dummyPatientId")
               && (alert.getPriority() == 1)
               && alert.getStatus().equals(AlertStatus.NEW);
            }
        };
        verify(alertService).createAlert(argThat(alertArgumentMatcher));
    }


}
