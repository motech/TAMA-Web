package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.context.EventContext;
import org.motechproject.event.EventRelay;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ExtendedModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@ContextConfiguration(locations = "classpath:META-INF/spring/application*Context.xml", inheritLocations = false)
public class PatientControllerIT extends SpringIntegrationTest {

    @Autowired
    PatientController patientController;
    @Autowired
    AllPatients allPatients;

    @Autowired(required = false)
    private EventRelay eventRelay = EventContext.getInstance().getEventRelay();
    private static final String USER_NAME = "userName";
    @Mock
    private HttpSession session;
    @Mock
    private HttpServletRequest request;
    @Mock
    private AuthenticatedUser user;

    @Before
    public void setUp() {
        initMocks(this);
        when(request.getCharacterEncoding()).thenReturn(null);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
        when(user.getUsername()).thenReturn(USER_NAME);
    }

    @Test
    public void shouldActivate() throws Exception {
        final Patient patient = PatientBuilder.startRecording().withDefaults().
                withActivationDate(null).
                build();
        allPatients.add(patient, USER_NAME);
        markForDeletion(patient);


        patientController.activate(patient.getId(), new ExtendedModelMap(), request);

        Patient patientFromDB = allPatients.get(patient.getId());
        assertEquals(Status.Active.toString(), patientFromDB.getStatus().toString());
        assertEquals(DateUtil.today(), patientFromDB.getActivationDate().toLocalDate());
    }
}
