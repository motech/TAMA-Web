package org.motechproject.tama.web.command.fourdayrecall;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.TAMAIVRContext;
import org.motechproject.tama.repository.AllClinics;
import org.motechproject.tama.repository.AllPatients;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class IncomingWelcomeGreetingMessageTest {

    @Mock
    private AllPatients allPatients;

    @Mock
    private AllClinics allClinics;

    private IncomingWelcomeGreetingMessage incomingWelcomeGreetingMessage;

    private Patient patient;

    private Clinic clinic;

    private TAMAIVRContext ivrContext;

    @Before
    public void setUp() {
        initMocks(this);
        final String patientId = "testPatientId";
        final String clinicId = "testClinicId";
        String clinicName = "testClinicName";

        incomingWelcomeGreetingMessage = new IncomingWelcomeGreetingMessage(allPatients, allClinics, null);
        clinic = ClinicBuilder.startRecording().withId(clinicId).withName(clinicName).build();
        patient = PatientBuilder.startRecording().withId(patientId).withClinic(clinic).build();
        ivrContext = new TAMAIVRContext() {
            @Override
            public String patientId() {
                return patientId;
            }
        };
        when(allPatients.get(patientId)).thenReturn(patient);
        when(allClinics.get(clinicId)).thenReturn(clinic);
    }

    @Test
    public void shouldPlayWelcomeGreetingMessageDependingOnClinic() {
        final String[] messagesToBePlayed = incomingWelcomeGreetingMessage.executeCommand(ivrContext);

        assertNotNull(messagesToBePlayed);
        assertEquals(1, messagesToBePlayed.length);
        assertEquals(String.format("welcome_to_%s", clinic.getName()), messagesToBePlayed[0]);
    }
}
