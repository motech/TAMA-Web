package org.motechproject.tama.symptomreporting.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.tama.symptomreporting.SymptomReportingContextForTest;
import org.motechproject.tama.symptomreporting.factory.SymptomReportingContextFactory;
import org.motechproject.tama.symptomreporting.service.SymptomRecordingService;
import org.motechproject.tama.symptomreporting.service.SymptomReportingService;

import java.util.ArrayList;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class DialControllerTest {
    @Mock
    private KookooCallDetailRecordsService callDetailRecordService;
    @Mock
    private IVRMessage ivrMessage;
    @Mock
    private AllPatients allPatients;
    @Mock
    private SymptomReportingContextFactory contextFactory;
    @Mock
    private PatientAlertService patientAlertService;
    @Mock
    private SymptomRecordingService symptomRecordingService;
    @Mock
    private SymptomReportingService symptomReportingService;
    @Mock
    private KooKooIVRContext kooKooIVRContext;

    private Clinic clinic;
    private DialController dialController;
    private SymptomReportingContextForTest symptomReportingContext;


    @Before
    public void setup() {
        initMocks(this);
        clinic = new Clinic("id");
        clinic.setClinicianContacts(new ArrayList<Clinic.ClinicianContact>() {{
            this.add(new Clinic.ClinicianContact("name1", "ph1"));
            this.add(new Clinic.ClinicianContact("name2", "ph2"));
            this.add(new Clinic.ClinicianContact("name3", "ph3"));
        }});
        Patient patient = new Patient() {{
            this.setClinic(clinic);
            setId("patientId");
        }};
        when(allPatients.get("patientId")).thenReturn(patient);

        symptomReportingContext = new SymptomReportingContextForTest().patientDocumentId(patient.getId());
        when(contextFactory.create(kooKooIVRContext)).thenReturn(symptomReportingContext);

        when(ivrMessage.getWav(TamaIVRMessage.CONNECTING_TO_DOCTOR, "en")).thenReturn("connecting-to-dr");
        when(ivrMessage.getWav(TamaIVRMessage.CANNOT_CONNECT_TO_DOCTOR, "en")).thenReturn("cannot-connect");

        dialController = new DialController(null, callDetailRecordService, null, contextFactory, allPatients, patientAlertService, symptomRecordingService);
    }

    @Test
    public void shouldDialTheFirstClinician() {
        final String callId = "callId";
        symptomReportingContext.numberOfCliniciansCalled(0).callId(callId);

        String dialResponse = dialController.dial(kooKooIVRContext).create(ivrMessage);

        assertTrue(dialResponse.contains("<dial>0ph1</dial>"));
        assertTrue(dialResponse.contains("<playaudio>connecting-to-dr</playaudio>"));
        assertEquals(1, symptomReportingContext.numberOfCliniciansCalled());
        verify(symptomRecordingService, times(1)).setAsNotConnectedToDoctor(callId);
    }

    @Test
    public void shouldEndCallWhenConnectedToTheDoctor() {
        final String callId = "callId";
        symptomReportingContext.numberOfCliniciansCalled(1).isAnswered(true).callId(callId);

        String dialResponse = dialController.dial(kooKooIVRContext).create(ivrMessage);

        assertFalse(dialResponse.contains("<dial>0ph2</dial>"));
        assertTrue(symptomReportingContext.getEndCall());
        verify(patientAlertService).updateDoctorConnectedToDuringSymptomCall("patientId", "name1");
        verify(symptomRecordingService).setAsConnectedToDoctor(callId);
    }

    @Test
    public void shouldEndCallWhenAllCliniciansHaveBeenDialled() {
        final String callId = "callId";
        symptomReportingContext.numberOfCliniciansCalled(3).callId(callId);

        String dialResponse = dialController.dial(kooKooIVRContext).create(ivrMessage);

        assertTrue(dialResponse.contains("<playaudio>cannot-connect</playaudio>"));
        assertFalse(dialResponse.contains("dial"));
        assertTrue(symptomReportingContext.getEndCall());
        assertEquals(4, symptomReportingContext.numberOfCliniciansCalled());
        verify(symptomRecordingService).setAsNotConnectedToDoctor(callId);
    }

    @Test
    public void shouldCallClinicianWhosePhoneNumberIsNotNullOrEmpty() {
        clinic.getClinicianContacts().get(0).setPhoneNumber("");
        clinic.getClinicianContacts().get(1).setPhoneNumber(null);

        String dialResponse = dialController.dial(kooKooIVRContext).create(ivrMessage);

        assertTrue(dialResponse.contains("<dial>0ph3</dial>"));
        assertTrue(dialResponse.contains("<playaudio>connecting-to-dr</playaudio>"));
        assertEquals(3, symptomReportingContext.numberOfCliniciansCalled());
    }

    @Test
    public void shouldBuildResponseUsingPatientsPreferredLanguage() {
        symptomReportingContext.preferredLanguage("mr");
        dialController.dial(kooKooIVRContext).create(ivrMessage);
        verify(ivrMessage).getWav(TamaIVRMessage.CONNECTING_TO_DOCTOR, "mr");
    }
}