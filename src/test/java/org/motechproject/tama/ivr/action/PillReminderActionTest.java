package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.event.BaseActionTest;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.repository.IVRCallAudits;
import org.motechproject.tama.repository.Patients;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PillReminderActionTest extends BaseActionTest {
    private PillReminderAction action;
    @Mock
    private IVRMessage messages;
    @Mock
    private Patients patients;
    @Mock
    private Clinics clinics;
    @Mock
    private IVRCallAudits audits;
    @Mock
    private PillReminderService service;

    @Before
    public void setUp() {
        super.setUp();
        action = new PillReminderAction(messages, patients, clinics, audits, service);
    }

    @Test
    public void shouldReturnUserProceedResponse() {
        IVRRequest ivrRequest = new IVRRequest("sid", "cid", "event", "data");
        Patient patient = mock(Patient.class);
        Clinic clinic = mock(Clinic.class);

        when(patient.getClinic_id()).thenReturn("clinic_id");
        when(clinic.getName()).thenReturn("Mayo");
        when(request.getSession(false)).thenReturn(session);
        when(messages.get(IVRMessage.TAMA_IVR_WELCOME_MESSAGE)).thenReturn("Welcome. This is TAMA calling from {0} clinic");
        when(session.getAttribute(IVR.Attributes.PATIENT_DOCUMENT_ID)).thenReturn("id");
        when(patients.get("id")).thenReturn(patient);
        when(clinics.get("clinic_id")).thenReturn(clinic);

        String responseXML = action.handle(ivrRequest, request, response);

        IVRAuditMatcher matcher = new IVRAuditMatcher(ivrRequest.getSid(), ivrRequest.getCid(), "id", IVRCallAudit.State.USER_AUTHORISED);
        verify(audits).add(argThat(matcher));
        assertEquals("<response sid=\"sid\"><playtext>Welcome. This is TAMA calling from Mayo clinic</playtext></response>", StringUtils.replace(responseXML, System.getProperty("line.separator"), ""));
    }

}

