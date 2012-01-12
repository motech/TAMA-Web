package org.motechproject.tama.symptomreporting.command;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.patient.domain.PatientAlert;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tama.patient.service.PatientAlertService;

import java.util.Map;
import java.util.Properties;

import static org.mockito.Matchers.*;
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
    private ArgumentMatcher<Map<String, String>> naReportedTypeDataMatcher;
    private ArgumentMatcher<Map<String, String>> notReportedTypeDataMatcher;

    @Before
    public void setup() {
        initMocks(this);
        TAMAIVRContextForTest context = new TAMAIVRContextForTest().patientDocumentId("dummyPatientId");
        when(contextFactory.create(any(KooKooIVRContext.class))).thenReturn(context);
        command = new SymptomReportingAlertsCommand(alertService, properties, contextFactory);
        naReportedTypeDataMatcher = new ArgumentMatcher<Map<String, String>>() {
            @Override
            public boolean matches(Object argument) {
                Map<String, String> map = (Map<String, String>) argument;
                return map.size() == 1 && map.get(PatientAlert.CONNECTED_TO_DOCTOR).equals(TAMAConstants.ReportedType.NA.toString());
            }
        };
        notReportedTypeDataMatcher = new ArgumentMatcher<Map<String, String>>() {
            @Override
            public boolean matches(Object argument) {
                Map<String, String> map = (Map<String, String>) argument;
                return map.size() == 1 && map.get(PatientAlert.CONNECTED_TO_DOCTOR).equals(TAMAConstants.ReportedType.No.toString());
            }
        };
    }

    @Test
    public void shouldCallAlertServiceToCreateANewAlert() {
        final ITreeCommand iTreeCommand = command.symptomReportingAlertWithPriority(1, node, TAMAConstants.ReportedType.NA);
        iTreeCommand.execute(null);
        when(properties.get(any())).thenReturn(StringUtils.EMPTY);
        verify(alertService).createAlert(eq("dummyPatientId"), eq(1), eq(""), eq(""), eq(PatientAlertType.SymptomReporting), argThat(naReportedTypeDataMatcher));
    }

    @Test
    public void shouldCallAlertServiceToCreateANewAlert_ForASpecificReportedType() {
        final ITreeCommand iTreeCommand = command.symptomReportingAlertWithPriority(1, node, TAMAConstants.ReportedType.No);
        iTreeCommand.execute(null);
        when(properties.get(any())).thenReturn(StringUtils.EMPTY);
        verify(alertService).createAlert(eq("dummyPatientId"), eq(1), eq(""), eq(""), eq(PatientAlertType.SymptomReporting), argThat(notReportedTypeDataMatcher));
    }

    @Test
    public void shouldCreateANewAlertWithCorrectSymptomMappingWithPPCPrompt() {
        Node stubNode = new Node();
        stubNode.setPrompts(new MenuAudioPrompt().setName("ppc_prompt"), new MenuAudioPrompt().setName("cy_prompt"), new MenuAudioPrompt().setName("cn_prompt"));
        when(properties.get("ppc_prompt")).thenReturn("ppc_prompt");
        when(properties.get("cy_prompt")).thenReturn("cy_prompt");
        when(properties.get("cn_prompt")).thenReturn("cn_prompt");

        final ITreeCommand iTreeCommand = command.symptomReportingAlertWithPriority(1, stubNode, TAMAConstants.ReportedType.NA);
        iTreeCommand.execute(null);
        verify(alertService).createAlert(eq("dummyPatientId"), eq(1), eq(""), eq("ppc_prompt"), eq(PatientAlertType.SymptomReporting), argThat(naReportedTypeDataMatcher));
    }

    @Test
    public void shouldCreateANewAlertWithCorrectSymptomMappingWithCyPrompt() {
        Node stubNode = new Node();
        stubNode.setPrompts(new MenuAudioPrompt().setName("cn_prompt"), new MenuAudioPrompt().setName("cy_prompt"));
        when(properties.get("cy_prompt")).thenReturn("cy_prompt");
        when(properties.get("cn_prompt")).thenReturn("cn_prompt");

        final ITreeCommand iTreeCommand = command.symptomReportingAlertWithPriority(1, stubNode, TAMAConstants.ReportedType.NA);
        iTreeCommand.execute(null);
        verify(alertService).createAlert(eq("dummyPatientId"), eq(1), eq(""), eq("cy_prompt"), eq(PatientAlertType.SymptomReporting), argThat(naReportedTypeDataMatcher));
    }

    @Test
    public void shouldNotSetSymptomsForN02Node() {
        Node stubNode = new Node();
        stubNode.setPrompts(new MenuAudioPrompt().setName("adv_callclinic"), new MenuAudioPrompt().setName("cn_prompt"));
        when(properties.get(any())).thenReturn("test");

        final ITreeCommand iTreeCommand = command.symptomReportingAlertWithPriority(1, stubNode, TAMAConstants.ReportedType.NA);
        iTreeCommand.execute(null);
        verify(alertService).createAlert(eq("dummyPatientId"), eq(1), eq("test"), eq("-"), eq(PatientAlertType.SymptomReporting), argThat(naReportedTypeDataMatcher));
    }
}
