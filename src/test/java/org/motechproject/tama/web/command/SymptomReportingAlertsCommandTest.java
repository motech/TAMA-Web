package org.motechproject.tama.web.command;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;

import java.util.Properties;

import static org.mockito.Matchers.any;
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
    @Mock
    private Properties properties;

    @Mock
    private Node node;

    @Before
    public void setup() {
        initMocks(this);
        context = new IVRContext(ivrRequest, ivrSession);
        when(ivrSession.getExternalId()).thenReturn("dummyPatientId");

    }

    @Test
    public void shouldCallAlertServiceToCreateANewAlert() {
        SymptomReportingAlertsCommand command = new SymptomReportingAlertsCommand(alertService, properties);
        final ITreeCommand iTreeCommand = command.symptomReportingAlertWithPriority(1, node);
        iTreeCommand.execute(context);
        when(properties.get(any())).thenReturn(StringUtils.EMPTY);
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

    @Test
    public void shouldCreateANewAlertWithCorrectSymptomMappingWithPPCPrompt() {
        SymptomReportingAlertsCommand command = new SymptomReportingAlertsCommand(alertService, properties);
        Node stubNode = new Node();
        stubNode.setPrompts(new MenuAudioPrompt().setName("ppc_prompt"), new MenuAudioPrompt().setName("cy_prompt"), new MenuAudioPrompt().setName("cn_prompt"));
        when(properties.get("ppc_prompt")).thenReturn("ppc_prompt");
        when(properties.get("cy_prompt")).thenReturn("cy_prompt");
        when(properties.get("cn_prompt")).thenReturn("cn_prompt");

        final ITreeCommand iTreeCommand = command.symptomReportingAlertWithPriority(1, stubNode);
        iTreeCommand.execute(context);
        final ArgumentMatcher<Alert> alertArgumentMatcher = new ArgumentMatcher<Alert>(){
            @Override
            public boolean matches(Object testAlert) {
                Alert alert = (Alert)testAlert;
                return alert.getDescription().equals("ppc_prompt");
            }
        };
        verify(alertService).createAlert(argThat(alertArgumentMatcher));
    }

    @Test
    public void shouldCreateANewAlertWithCorrectSymptomMappingWithCyPrompt() {
        SymptomReportingAlertsCommand command = new SymptomReportingAlertsCommand(alertService, properties);
        Node stubNode = new Node();
        stubNode.setPrompts( new MenuAudioPrompt().setName("cn_prompt"), new MenuAudioPrompt().setName("cy_prompt"));
        when(properties.get("cy_prompt")).thenReturn("cy_prompt");
        when(properties.get("cn_prompt")).thenReturn("cn_prompt");

        final ITreeCommand iTreeCommand = command.symptomReportingAlertWithPriority(1, stubNode);
        iTreeCommand.execute(context);
        final ArgumentMatcher<Alert> alertArgumentMatcher = new ArgumentMatcher<Alert>(){
            @Override
            public boolean matches(Object testAlert) {
                Alert alert = (Alert)testAlert;
                return alert.getDescription().equals("cy_prompt");
            }
        };
        verify(alertService).createAlert(argThat(alertArgumentMatcher));
    }

    @Test
    public void shouldNotSetSymptomsForN02Node() {
        SymptomReportingAlertsCommand command = new SymptomReportingAlertsCommand(alertService, properties);
        Node stubNode = new Node();
        stubNode.setPrompts(new MenuAudioPrompt().setName("adv_callclinic"), new MenuAudioPrompt().setName("cn_prompt"));
        when(properties.get(any())).thenReturn("test");

        final ITreeCommand iTreeCommand = command.symptomReportingAlertWithPriority(1, stubNode);
        iTreeCommand.execute(context);
        final ArgumentMatcher<Alert> alertArgumentMatcher = new ArgumentMatcher<Alert>(){
            @Override
            public boolean matches(Object testAlert) {
                Alert alert = (Alert)testAlert;
                return alert.getDescription().equals("-");
            }
        };
        verify(alertService).createAlert(argThat(alertArgumentMatcher));
    }


}
