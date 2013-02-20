package org.motechproject.tama.symptomreporting.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.dto.SendSMSRequest;
import org.motechproject.tama.ivr.reporting.SMSType;
import org.motechproject.tama.ivr.service.SendSMSService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;

import java.util.Arrays;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;


public class SendSMSCommandTest {
    @Mock
    private SendSMSService sendSMSService;
    @Mock
    private AllPatients allPatients;

    private Patient patient;

    private TAMAIVRContextForTest tamaivrContextForTest;

    private Properties messageDescription;

    private SendSMSCommand sendSMSCommand;

    @Before
    public void setUp() {
        initMocks(this);
        patient = PatientBuilder.startRecording().withDefaults().withMobileNumber("1234567890").withOTCPreference(true).build();
        messageDescription = new Properties();
        messageDescription.setProperty("adv_crocin01", "Take one tablet of crocin");
        messageDescription.setProperty("adv_halfhourcro01", "Take a paracetamol tablet thrice a day for 5 days after eating something.");
        tamaivrContextForTest = new TAMAIVRContextForTest();
    }

    @Test
    public void shouldSMSTheAdviceToPatient() {
        Prompt advicePrompt = new AudioPrompt().setName("adv_crocin01");
        Prompt notAnAdvicePrompt = new AudioPrompt().setName("ppc_crocin01");
        sendSMSCommand = new SendSMSCommand(Arrays.asList(advicePrompt, notAnAdvicePrompt), sendSMSService, allPatients, messageDescription);

        when(allPatients.get(tamaivrContextForTest.patientDocumentId())).thenReturn(patient);
        sendSMSCommand.executeCommand(tamaivrContextForTest);
        verify(sendSMSService, times(1)).send(eq(new SendSMSRequest(patient.getMobilePhoneNumber(), patient.getId())), anyString(), eq(SMSType.OTC));
    }

    @Test
    public void shouldSendSMSWithDescriptionOfAdvice() {
        String descriptionOfAdvice = "Take one tablet of crocin";
        messageDescription.setProperty("adv_crocin01", descriptionOfAdvice);
        sendSMSCommand = new SendSMSCommand(Arrays.asList(new AudioPrompt().setName("adv_crocin01")), sendSMSService, allPatients, messageDescription);

        when(allPatients.get(tamaivrContextForTest.patientDocumentId())).thenReturn(patient);
        sendSMSCommand.executeCommand(tamaivrContextForTest);
        verify(sendSMSService, times(1)).send(eq(new SendSMSRequest(patient.getMobilePhoneNumber(), patient.getId())), eq(descriptionOfAdvice), eq(SMSType.OTC));
    }

    @Test
    public void shouldNotSendSMSWhenDescriptionIsEmpty() {
        String emptyDescription = "";
        messageDescription.setProperty("adv_crocin01", emptyDescription);
        sendSMSCommand = new SendSMSCommand(Arrays.asList(new AudioPrompt().setName("adv_crocin01")), sendSMSService, allPatients, messageDescription);

        when(allPatients.get(tamaivrContextForTest.patientDocumentId())).thenReturn(patient);
        sendSMSCommand.executeCommand(tamaivrContextForTest);
        verifyZeroInteractions(sendSMSService);
    }

    @Test
    public void shouldSendSMSWhenPatientHasAgreedToReceiveSMS() {
        patient.getPatientPreferences().setReceiveOTCAdvice(true);
        sendSMSCommand = new SendSMSCommand(Arrays.asList(new AudioPrompt().setName("adv_crocin01")), sendSMSService, allPatients, messageDescription);

        when(allPatients.get(tamaivrContextForTest.patientDocumentId())).thenReturn(patient);
        sendSMSCommand.executeCommand(tamaivrContextForTest);
        verify(sendSMSService).send(eq(new SendSMSRequest(patient.getMobilePhoneNumber(), patient.getId())), anyString(), eq(SMSType.OTC));
    }

    @Test
    public void shouldNotSendSMSWhenPatientHasNotAgreedToReceiveSMS() {
        patient.getPatientPreferences().setReceiveOTCAdvice(false);
        sendSMSCommand = new SendSMSCommand(Arrays.asList(new AudioPrompt().setName("adv_crocin01")), sendSMSService, allPatients, messageDescription);

        when(allPatients.get(tamaivrContextForTest.patientDocumentId())).thenReturn(patient);
        sendSMSCommand.executeCommand(tamaivrContextForTest);
        verifyZeroInteractions(sendSMSService);
    }

    @Test
    public void shouldAddWillSendSMSMessageOnlyOnce_IrrespectiveOfHowManySMSAreSent() {
        String descriptionOfAdvice1 = "Take one tablet of crocin";
        String descriptionOfAdvice2 = "Take a paracetamol tablet thrice a day for 5 days after eating something.";
        sendSMSCommand = new SendSMSCommand(Arrays.asList(new AudioPrompt().setName("adv_crocin01"), new AudioPrompt().setName("adv_halfhourcro01")),
                sendSMSService, allPatients, messageDescription);
        when(allPatients.get(tamaivrContextForTest.patientDocumentId())).thenReturn(patient);
        String[] willSendSMSMessage = sendSMSCommand.executeCommand(tamaivrContextForTest);
        verify(sendSMSService, times(1)).send(eq(new SendSMSRequest(patient.getMobilePhoneNumber(), patient.getId())), eq(descriptionOfAdvice1), eq(SMSType.OTC));
        verify(sendSMSService, times(1)).send(eq(new SendSMSRequest(patient.getMobilePhoneNumber(), patient.getId())), eq(descriptionOfAdvice2), eq(SMSType.OTC));
        assertEquals(1, willSendSMSMessage.length);
    }

}
