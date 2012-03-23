package org.motechproject.tama.symptomreporting.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.patient.service.PatientAlertService;

import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UpdateAdviceCommandTest {

    public static final String PATIENT_DOC_ID = "dummyPatientId";

    @Mock
    private PatientAlertService patientAlertService;
    @Mock
    private Properties properties;
    @Mock
    private TAMAIVRContextFactory contextFactory;

    private UpdateAdviceCommand command;

    @Before
    public void setup() {
        initMocks(this);
        TAMAIVRContextForTest context = new TAMAIVRContextForTest().patientDocumentId(PATIENT_DOC_ID);
        when(contextFactory.create(any(KooKooIVRContext.class))).thenReturn(context);
        command = new UpdateAdviceCommand(patientAlertService, properties, contextFactory);
    }

    @Test
    public void shouldSetAdviceAndPriorityOnAlert() {
        Node node = node("adv_blah");
        when(properties.get("adv_blah")).thenReturn("blahblah");

        command.get(1, node).execute(null);
        verify(patientAlertService).updateAdviceOnSymptomsReportingAlert(PATIENT_DOC_ID, "blahblah", 1);
    }

    @Test
    public void shouldSetEmptyAdviceWhenNodeIsNotAnAdviceNode() {
        Node node = node("ha_blah");
        command.get(1, node).execute(null);
        verify(patientAlertService).updateAdviceOnSymptomsReportingAlert(PATIENT_DOC_ID, "", 1);
    }

    private Node node(String name) {
        Node node = new Node();
        AudioPrompt audioPrompt = new AudioPrompt();
        audioPrompt.setName(name);
        node.setPrompts(audioPrompt);
        return node;
    }
}
