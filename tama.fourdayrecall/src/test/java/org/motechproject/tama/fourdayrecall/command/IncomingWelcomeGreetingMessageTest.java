package org.motechproject.tama.fourdayrecall.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.command.ClinicNameMessageBuilder;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.domain.IVRLanguage;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class IncomingWelcomeGreetingMessageTest {

    @Mock
    private AllPatients allPatients;

    @Mock
    private AllClinics allClinics;

    @Mock
    private ClinicNameMessageBuilder clinicNameMessageBuilder;

    private IncomingWelcomeGreetingMessage incomingWelcomeGreetingMessage;

    private Patient patient;

    private Clinic clinic;

    private TAMAIVRContextForTest ivrContext;

    @Before
    public void setUp() {
        initMocks(this);
        String patientId = "testPatientId";

        incomingWelcomeGreetingMessage = new IncomingWelcomeGreetingMessage(allPatients, allClinics, clinicNameMessageBuilder);
        clinic = ClinicBuilder.startRecording().withId("testClinicId").withName("testClinicName").build();
        patient = PatientBuilder.startRecording().withId(patientId).withClinic(clinic).withIVRLanguage(IVRLanguage.newIVRLanguage("English", "en")).build();
        ivrContext = new TAMAIVRContextForTest().patientDocumentId(patientId);

        when(allPatients.get(patientId)).thenReturn(patient);
        when(allClinics.get("testClinicId")).thenReturn(clinic);
    }

    @Test
    public void shouldPlayWelcomeGreetingMessageDependingOnClinic() {
        when(clinicNameMessageBuilder.getInboundMessage(clinic, patient.getPatientPreferences().getIvrLanguage())).thenReturn("welcome_to_" + clinic.getName());

        final String[] messagesToBePlayed = incomingWelcomeGreetingMessage.executeCommand(ivrContext);
        assertNotNull(messagesToBePlayed);
        assertEquals(1, messagesToBePlayed.length);
        assertEquals(String.format("welcome_to_%s", clinic.getName()), messagesToBePlayed[0]);
    }

    @Test
    public void shouldNotPlayWelcomeGreetingMessageForRepeatMenu() {
        ivrContext.addLastCompletedTreeToListOfCompletedTrees(TAMATreeRegistry.FOUR_DAY_RECALL_INCOMING_CALL);
        final String[] messagesToBePlayed = incomingWelcomeGreetingMessage.executeCommand(ivrContext);
        assertEquals(0, messagesToBePlayed.length);
    }

    @Test
    public void shouldPlayDefaultMessage_WhenClinicSpecificMessageNotAvailable(){
        when(clinicNameMessageBuilder.getInboundMessage(clinic, patient.getPatientPreferences().getIvrLanguage())).thenReturn("genericClinicMessage");

        String[] messages = incomingWelcomeGreetingMessage.executeCommand(ivrContext);
        assertEquals(1, messages.length);
        assertEquals("genericClinicMessage", messages[0]);
    }
}
