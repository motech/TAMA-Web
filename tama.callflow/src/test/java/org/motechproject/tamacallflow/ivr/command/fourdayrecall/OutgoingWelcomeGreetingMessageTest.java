package org.motechproject.tamacallflow.ivr.command.fourdayrecall;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;

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