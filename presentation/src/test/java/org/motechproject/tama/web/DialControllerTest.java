package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.server.service.ivr.IVRStatus;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.SymptomsReportingContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;

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
    private HttpServletResponse response;
    private Clinic clinic;
    private Patient patient;
    private KookooRequest kookooRequest;
    private DialController dialController;
    private KooKooIVRContext kooKooIVRContext;

    @Before
    public void setup() {
        initMocks(this);
        clinic = new Clinic("id");
        clinic.setClinicianContacts(new ArrayList<Clinic.ClinicianContact>() {{
            this.add(new Clinic.ClinicianContact("name1", "ph1"));
            this.add(new Clinic.ClinicianContact("name2", "ph2"));
            this.add(new Clinic.ClinicianContact("name3", "ph3"));
        }});
        patient = new Patient() {{
            this.setClinic(clinic);
        }};
        when(httpRequest.getAttribute(TAMAIVRContext.PATIENT)).thenReturn(patient);

        kookooRequest = new KookooRequest("", "", "Dial", "", IVRStatus.NotAnswered.toString());
        kooKooIVRContext = new KooKooIVRContext(kookooRequest, httpRequest, response);
        dialController = new DialController(null, callDetailRecordService, null, null);
    }

    @Test
    public void shouldAddNumberOfCliniciansCalled_ToRepsonse() {
        when(httpRequest.getAttribute(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED)).thenReturn("");
        String dialResponse = dialController.gotDTMF(kooKooIVRContext).create(null);

        assertTrue(dialResponse.contains("<dial>0ph1</dial>"));
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(1)).addCookie(cookieCaptor.capture());
        assertEquals(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED, cookieCaptor.getValue().getName());
        assertEquals("1", cookieCaptor.getValue().getValue());
    }

    @Test
    public void shouldSwitchedToDialledState_OnAnswered_CallStatus() {
        kookooRequest.setStatus(IVRStatus.Answered.toString());
        when(httpRequest.getAttribute(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED)).thenReturn("1");
        String dialResponse = dialController.gotDTMF(kooKooIVRContext).create(null);

        assertFalse(dialResponse.contains("<dial>0ph2</dial>"));
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(1)).addCookie(cookieCaptor.capture());
        assertEquals(SymptomsReportingContext.SWITCH_TO_DIAL_STATE, cookieCaptor.getValue().getName());
        assertEquals("false", cookieCaptor.getValue().getValue());
    }

    @Test
    public void shouldSwitchedToDialledState_OnSettingTheLast_ClinicianPhoneNumber() {
        when(httpRequest.getAttribute(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED)).thenReturn("3");
        when(ivrMessage.getWav(TamaIVRMessage.CANNOT_CONNECT_TO_DOCTOR, "en")).thenReturn("cannot-connect");
        String dialResponse = dialController.gotDTMF(kooKooIVRContext).create(ivrMessage);

        assertTrue(dialResponse.contains("<playaudio>cannot-connect</playaudio>"));
        assertFalse(dialResponse.contains("dial"));
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(2)).addCookie(cookieCaptor.capture());
        assertEquals(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED, cookieCaptor.getAllValues().get(0).getName());
        assertEquals(SymptomsReportingContext.SWITCH_TO_DIAL_STATE, cookieCaptor.getAllValues().get(1).getName());
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
        String dialResponse = dialController.gotDTMF(kooKooIVRContext).create(null);

        assertTrue(dialResponse.contains("<dial>0ph3</dial>"));
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(1)).addCookie(cookieCaptor.capture());
        assertEquals(SymptomsReportingContext.NUMBER_OF_CLINICIANS_CALLED, cookieCaptor.getValue().getName());
        assertEquals("3", cookieCaptor.getValue().getValue());
    }
}
