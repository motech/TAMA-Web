package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.SymptomsReportingContextWrapper;
import org.motechproject.tama.ivr.TAMAIVRContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class DialControllerTest {
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private KookooCallDetailRecordsService callDetailRecordService;
    @Mock
    private HttpServletResponse response;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldAddDialTagToResponse() {
        KooKooIVRContext kooKooIVRContext = new KooKooIVRContext(null, httpRequest, response);
        final Clinic clinic = new Clinic("id");
        clinic.setClinicianContacts(new ArrayList<Clinic.ClinicianContact>() {{
            this.add(new Clinic.ClinicianContact("name1", "ph1"));
            this.add(new Clinic.ClinicianContact("name2", "ph2"));
            this.add(new Clinic.ClinicianContact("name3", "ph3"));
        }});
        Patient patient = new Patient() {{
            this.setClinic(clinic);
        }};
        when(httpRequest.getAttribute(TAMAIVRContext.PATIENT)).thenReturn(patient);
        DialController dialController = new DialController(null, callDetailRecordService, null, null);
        String dialResponse = dialController.gotDTMF(kooKooIVRContext).create(null);

        assertTrue(dialResponse.contains("<dial>ph1</dial>"));
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        assertEquals(SymptomsReportingContextWrapper.SWITCH_TO_DIAL_STATE, cookieCaptor.getValue().getName());
        assertEquals("false", cookieCaptor.getValue().getValue());
    }
}
