package org.motechproject.tama.web.command;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.TAMAIVRContextFactory;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.service.PatientAlertService;

import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomReportingAlertsCommandTest {
    @Mock
    private PatientAlertService alertService;
    @Mock
    private Properties properties;
    @Mock
    private TAMAIVRContextFactory contextFactory;

    @Mock
    private Node node;
    private SymptomReportingAlertsCommand command;

    @Before
    public void setup() {
        initMocks(this);
        TAMAIVRContextForTest context = new TAMAIVRContextForTest().patientId("dummyPatientId");
        when(contextFactory.create(any(KooKooIVRContext.class))).thenReturn(context);
        command = new SymptomReportingAlertsCommand(alertService, properties, contextFactory);
    }

    @Test
    public void shouldCallAlertServiceToCreateANewAlert() {
        final ITreeCommand iTreeCommand = command.symptomReportingAlertWithPriority(1, node);
        iTreeCommand.execute(null);
        when(properties.get(any())).thenReturn(StringUtils.EMPTY);
        verify(alertService).createAlert("dummyPatientId", 1, "", "");
    }

    @Test
    public void shouldCreateANewAlertWithCorrectSymptomMappingWithPPCPrompt() {
        Node stubNode = new Node();
        stubNode.setPrompts(new MenuAudioPrompt().setName("ppc_prompt"), new MenuAudioPrompt().setName("cy_prompt"), new MenuAudioPrompt().setName("cn_prompt"));
        when(properties.get("ppc_prompt")).thenReturn("ppc_prompt");
        when(properties.get("cy_prompt")).thenReturn("cy_prompt");
        when(properties.get("cn_prompt")).thenReturn("cn_prompt");

        final ITreeCommand iTreeCommand = command.symptomReportingAlertWithPriority(1, stubNode);
        iTreeCommand.execute(null);
        verify(alertService).createAlert("dummyPatientId", 1, "ppc_prompt", "");

    }

    @Test
    public void shouldCreateANewAlertWithCorrectSymptomMappingWithCyPrompt() {
        Node stubNode = new Node();
        stubNode.setPrompts(new MenuAudioPrompt().setName("cn_prompt"), new MenuAudioPrompt().setName("cy_prompt"));
        when(properties.get("cy_prompt")).thenReturn("cy_prompt");
        when(properties.get("cn_prompt")).thenReturn("cn_prompt");

        final ITreeCommand iTreeCommand = command.symptomReportingAlertWithPriority(1, stubNode);
        iTreeCommand.execute(null);
        verify(alertService).createAlert("dummyPatientId", 1, "cy_prompt", "");
    }


    @Test
    public void shouldNotSetSymptomsForN02Node() {
        Node stubNode = new Node();
        stubNode.setPrompts(new MenuAudioPrompt().setName("adv_callclinic"), new MenuAudioPrompt().setName("cn_prompt"));
        when(properties.get(any())).thenReturn("test");

        final ITreeCommand iTreeCommand = command.symptomReportingAlertWithPriority(1, stubNode);
        iTreeCommand.execute(null);
        verify(alertService).createAlert("dummyPatientId", 1, "-", "test");
    }
}
