package org.motechproject.tama.symptomreporting.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.ivr.model.IVRStatus;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.tama.symptomreporting.context.SymptomsReportingContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class DialControllerTest {
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private KookooCallDetailRecordsService callDetailRecordService;
    @Mock
    private IVRMessage ivrMessage;
    @Mock
    private AllPatients allPatients;
    @Mock
    private TAMAIVRContextFactory contextFactory;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PatientAlertService patientAlertService;
    private Clinic clinic;
    private KookooRequest kookooRequest;
    private DialController dialController;
    private KooKooIVRContext kooKooIVRContext;
    private TAMAIVRContextForTest tamaivrcontext;

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

        kookooRequest = new KookooRequest("", "", "Dial", "", IVRStatus.NotAnswered.toString());
        kooKooIVRContext = new KooKooIVRContext(kookooRequest, httpRequest, response);
        tamaivrcontext = new TAMAIVRContextForTest().patient(patient).patientId(patient.getId());
        when(allPatients.get("patientId")).thenReturn(patient);
        dialController = new DialController(null, callDetailRecordService, null, contextFactory, allPatients, patientAlertService);

        when(contextFactory.create(kooKooIVRContext)).thenReturn(tamaivrcontext);
    }

    @Test
    public void shouldAddNumberOfCliniciansCalled_ToRepsonse() {
        when(httpRequest.getAttribute(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED)).thenReturn("");
        when(ivrMessage.getWav(TamaIVRMessage.CONNECTING_TO_DOCTOR, "en")).thenReturn("connecting-to-dr");
        String dialResponse = dialController.dial(kooKooIVRContext).create(ivrMessage);

        assertTrue(dialResponse.contains("<dial>0ph1</dial>"));
        assertTrue(dialResponse.contains("<playaudio>connecting-to-dr</playaudio>"));

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(1)).addCookie(cookieCaptor.capture());
        assertEquals(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED, cookieCaptor.getValue().getName());
        assertEquals("1", cookieCaptor.getValue().getValue());
    }

    @Test
    public void shouldSwitchedToDialledState_OnAnswered_CallStatus() {
        kookooRequest.setStatus(IVRStatus.Answered.toString());
        when(httpRequest.getAttribute(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED)).thenReturn("1");
        String dialResponse = dialController.dial(kooKooIVRContext).create(ivrMessage);

        assertFalse(dialResponse.contains("<dial>0ph2</dial>"));

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(1)).addCookie(cookieCaptor.capture());
        assertEquals(TAMAIVRContext.SWITCH_TO_DIAL_STATE, cookieCaptor.getValue().getName());
        assertEquals("false", cookieCaptor.getValue().getValue());
        verify(patientAlertService).updateDoctorConnectedToDuringSymptomCall("patientId", "name1");
    }

    @Test
    public void shouldSwitchedToDialledState_OnSettingTheLast_ClinicianPhoneNumber() {
        when(httpRequest.getAttribute(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED)).thenReturn("3");
        when(ivrMessage.getWav(TamaIVRMessage.CANNOT_CONNECT_TO_DOCTOR, "en")).thenReturn("cannot-connect");
        String dialResponse = dialController.dial(kooKooIVRContext).create(ivrMessage);

        assertTrue(dialResponse.contains("<playaudio>cannot-connect</playaudio>"));
        assertFalse(dialResponse.contains("dial"));

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(2)).addCookie(cookieCaptor.capture());
        assertEquals(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED, cookieCaptor.getAllValues().get(0).getName());
        assertEquals(TAMAIVRContext.SWITCH_TO_DIAL_STATE, cookieCaptor.getAllValues().get(1).getName());
        assertEquals("4", cookieCaptor.getAllValues().get(0).getValue());
        assertEquals("false", cookieCaptor.getAllValues().get(1).getValue());
    }

    @Test
    public void shouldSwitchToNextClinicianContactIfCurrentContactPhoneNumberDoesNotExist() {
        clinic.getClinicianContacts().get(0).setPhoneNumber("");
        clinic.getClinicianContacts().get(1).setPhoneNumber(null);

        when(httpRequest.getAttribute(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED)).thenReturn("");
        when(httpRequest.getAttribute(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED)).thenReturn("1");
        when(httpRequest.getAttribute(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED)).thenReturn("2");
        when(ivrMessage.getWav(TamaIVRMessage.CONNECTING_TO_DOCTOR, "en")).thenReturn("connecting-to-dr");
        String dialResponse = dialController.dial(kooKooIVRContext).create(ivrMessage);

        assertTrue(dialResponse.contains("<dial>0ph3</dial>"));
        assertTrue(dialResponse.contains("<playaudio>connecting-to-dr</playaudio>"));

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(1)).addCookie(cookieCaptor.capture());
        assertEquals(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED, cookieCaptor.getValue().getName());
        assertEquals("3", cookieCaptor.getValue().getValue());
    }

    @Test
    public void shouldPickUpPreferredLanguage_WhileBuilding_KookooResponse() {
        tamaivrcontext.preferredLanguage("mr");
        when(httpRequest.getAttribute(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED)).thenReturn("");

        dialController.dial(kooKooIVRContext).create(ivrMessage);
        verify(ivrMessage).getWav(TamaIVRMessage.CONNECTING_TO_DOCTOR, "mr");
    }
}