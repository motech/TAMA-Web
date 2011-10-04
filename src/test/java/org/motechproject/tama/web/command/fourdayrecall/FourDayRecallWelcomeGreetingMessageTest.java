package org.motechproject.tama.web.command.fourdayrecall;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllClinics;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.util.TamaSessionUtil;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FourDayRecallWelcomeGreetingMessageTest {
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllClinics allClinics;
    @Mock
    private IVRContext ivrContext;
    @Mock
    private IVRSession ivrSession;

    private WelcomeGreetingMessage welcomeGreetingMessage;

    @Before
    public void setup() {
        initMocks(this);
        Patient patient = new Patient();
        patient.setClinic_id("clinicId");
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName("clinicName").build();

        welcomeGreetingMessage = new WelcomeGreetingMessage(allPatients, allClinics);
        when(ivrContext.ivrSession()).thenReturn(ivrSession);
        when(ivrSession.get(TamaSessionUtil.TamaSessionAttribute.PATIENT_DOC_ID)).thenReturn("patientId");
        when(allPatients.get("patientId")).thenReturn(patient);
        when(allClinics.get("clinicId")).thenReturn(clinic);
    }

    @Test
    public void shouldReturnMessageBasedOnClinic() {
        String[] messages = welcomeGreetingMessage.execute(ivrContext);

        assertEquals(1, messages.length);
        assertEquals("clinicName", messages[0]);
    }
}