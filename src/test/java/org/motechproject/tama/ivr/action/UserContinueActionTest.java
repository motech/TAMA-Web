package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.event.BaseActionTest;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.repository.Patients;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserContinueActionTest extends BaseActionTest {

    private UserContinueAction userContinueAction;
    @Mock
    private IVRMessage messages;
    @Mock
    private Patients patients;
    @Mock
    private Clinics clinics;

    @Before
    public void setUp() {
        initMocks(this);
        userContinueAction = new UserContinueAction(messages, patients, clinics);
    }

    @Test
    public void shouldReturnUserProceedResponse() {
        IVRRequest ivrRequest = new IVRRequest();
        Patient patient = mock(Patient.class);
        Clinic clinic = mock(Clinic.class);

        when(patient.getClinic_id()).thenReturn("clinic_id");
        when(clinic.getName()).thenReturn("Mayo");
        when(request.getSession(false)).thenReturn(session);
        when(messages.get(IVR.MessageKey.TAMA_IVR_WELCOME_MESSAGE)).thenReturn("Welcome. This is TAMA calling from {0} clinic");
        when(session.getAttribute(IVR.Attributes.PATIENT_DOCUMENT_ID)).thenReturn("id");
        when(patients.get("id")).thenReturn(patient);
        when(clinics.get("clinic_id")).thenReturn(clinic);

        String handle = userContinueAction.handle(ivrRequest, request, response);
        assertEquals("<response><playtext>Welcome. This is TAMA calling from Mayo clinic</playtext></response>", StringUtils.replace(handle, "\n", ""));
    }

}
