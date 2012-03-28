package org.motechproject.tama.fourdayrecall.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.command.ClinicNameMessageBuilder;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.domain.IVRLanguage;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutgoingWelcomeGreetingMessageTest {

    @Mock
    private AllPatients allPatients;

    @Mock
    private AllClinics allClinics;

    @Mock
    private TamaIVRMessage tamaIVRMessage;

    @Mock
    private ClinicNameMessageBuilder clinicNameMessageBuilder;

    private OutgoingWelcomeGreetingMessage welcomeGreetingMessage;

    private TAMAIVRContextForTest context;

    private Patient patient;
    private Clinic clinic;


    @Before
    public void setup() {
        initMocks(this);
        clinic = ClinicBuilder.startRecording().withDefaults().withId("clinicId").withName("clinic name").withGreetingName("clinicName").build();
        patient = PatientBuilder.startRecording().withDefaults().withClinic(clinic).withIVRLanguage(IVRLanguage.newIVRLanguage("english", "en")).build();
        context = new TAMAIVRContextForTest().patientDocumentId("patientId");

        welcomeGreetingMessage = new OutgoingWelcomeGreetingMessage(allPatients, allClinics, clinicNameMessageBuilder);
        when(allPatients.get("patientId")).thenReturn(patient);
        when(allClinics.get("clinicId")).thenReturn(clinic);
    }

    @Test
    public void shouldReturnMessageBasedOnClinicAndPlayGreetingMessage() {
        when(clinicNameMessageBuilder.getOutboundMessage(clinic, patient.getPatientPreferences().getIvrLanguage())).thenReturn(clinic.getGreetingName());
        String[] messages = welcomeGreetingMessage.executeCommand(context);
        assertEquals(2, messages.length);
        assertEquals("clinicName", messages[0]);
        assertEquals(TamaIVRMessage.FDR_GREETING, messages[1]);
    }

    @Test
    public void shouldReturnDefaultMessageForClinic_WhenClinicSpecificMessageNotFound() {
        when(clinicNameMessageBuilder.getOutboundMessage(clinic, patient.getPatientPreferences().getIvrLanguage())).thenReturn("genericClinicMessage");
        String[] messages = welcomeGreetingMessage.executeCommand(context);
        assertEquals(2, messages.length);
        assertEquals("genericClinicMessage", messages[0]);
    }
}