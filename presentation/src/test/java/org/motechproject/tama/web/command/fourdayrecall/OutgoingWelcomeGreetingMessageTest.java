package org.motechproject.tama.web.command.fourdayrecall;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.repository.AllClinics;
import org.motechproject.tama.repository.AllPatients;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutgoingWelcomeGreetingMessageTest {
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllClinics allClinics;
    @Mock
    private PillReminderService pillReminderService;

    private OutgoingWelcomeGreetingMessage welcomeGreetingMessage;
    private TAMAIVRContextForTest context;

    @Before
    public void setup() {
        initMocks(this);
        Patient patient = new Patient();
        patient.setClinic_id("clinicId");
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName("clinicName").build();
        context = new TAMAIVRContextForTest().patientId("patientId");
        welcomeGreetingMessage = new OutgoingWelcomeGreetingMessage(allPatients, allClinics, pillReminderService);
        when(allPatients.get("patientId")).thenReturn(patient);
        when(allClinics.get("clinicId")).thenReturn(clinic);
    }

    @Test
    public void shouldReturnMessageBasedOnClinicAndPlayGreetingMessage() {
        String[] messages = welcomeGreetingMessage.executeCommand(context);
        assertEquals(2, messages.length);
        assertEquals("clinicName", messages[0]);
        assertEquals(TamaIVRMessage.FDR_GREETING, messages[1]);
    }
}