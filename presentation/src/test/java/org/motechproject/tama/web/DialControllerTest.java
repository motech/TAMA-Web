package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.SymptomsReportingContextWrapper;
import org.motechproject.tama.ivr.SymptomsReportingContextWrapperFactory;
import org.motechproject.tama.ivr.TAMAIVRContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class DialControllerTest {
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private SymptomsReportingContextWrapperFactory symptomsReportingContextFactory;
    @Mock
    private SymptomsReportingContextWrapper symptomsReportingContext;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldAddDialTagToResponse() {
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
        when(symptomsReportingContextFactory.create(any(KooKooIVRContext.class))).thenReturn(symptomsReportingContext);
        DialController dialController = new DialController(null, null, symptomsReportingContextFactory);
        String dialResponse = dialController.dial(null, httpRequest, null);

        assertTrue(dialResponse.contains("<dial>ph1</dial>"));
        verify(symptomsReportingContext).isDialState(false);
    }
}
