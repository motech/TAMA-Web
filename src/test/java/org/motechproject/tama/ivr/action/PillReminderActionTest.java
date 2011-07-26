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
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.repository.IVRCallAudits;
import org.motechproject.tama.repository.Patients;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PillReminderActionTest extends BaseActionTest {
    public static final String PATIENT_ID = "patientId";
    public static final String REGIMEN_ID = "regimenId";
    public static final String DOSAGE_ID = "dosageId";
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
        IVRRequest ivrRequest = mock(IVRRequest.class);
        Patient patient = mock(Patient.class);
        Clinic clinic = mock(Clinic.class);

        Map params = new HashMap();
        params.put(PillReminderCall.REGIMEN_ID, REGIMEN_ID);
        params.put(PillReminderCall.DOSAGE_ID, DOSAGE_ID);

        when(ivrRequest.getTamaParams()).thenReturn(params);
        when(ivrRequest.getCid()).thenReturn("cid");
        when(ivrRequest.getSid()).thenReturn("sid");
        when(clinic.getName()).thenReturn("mayo");
        when(patient.getClinic_id()).thenReturn("clinicId");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVR.Attributes.PATIENT_DOC_ID)).thenReturn(PATIENT_ID);
        when(patients.get("patientId")).thenReturn(patient);
        when(clinics.get("clinicId")).thenReturn(clinic);

        when(messages.getWav(IVRMessage.YOU_ARE_SUPPOSED_TO_TAKE)).thenReturn("supposed_to_take.wav");
        when(messages.getWav("mayo")).thenReturn("mayo.wav");
        when(messages.getWav("m1")).thenReturn("m1.wav");
        when(messages.getWav("m2")).thenReturn("m2.wav");
        when(messages.getWav(IVRMessage.PILL_REMINDER_RESPONSE_MENU)).thenReturn("menu.wav");
        when(service.medicinesFor(REGIMEN_ID, DOSAGE_ID)).thenReturn(Arrays.asList("m1", "m2"));

        String responseXML = action.handle(ivrRequest, request, response);

        IVRAuditMatcher matcher = new IVRAuditMatcher(ivrRequest.getSid(), ivrRequest.getCid(), "patientId", IVRCallAudit.State.USER_AUTHORISED);
        verify(audits).add(argThat(matcher));

        assertEquals("<response sid=\"sid\">" +
                "<playaudio>mayo.wav</playaudio>" +
                "<playaudio>supposed_to_take.wav</playaudio><playaudio>m1.wav</playaudio>" +
                "<playaudio>supposed_to_take.wav</playaudio><playaudio>m2.wav</playaudio>" +
                "<collectdtmf><playaudio>menu.wav</playaudio></collectdtmf>" +
                "</response>", StringUtils.replace(responseXML, System.getProperty("line.separator"), ""));
    }

}

