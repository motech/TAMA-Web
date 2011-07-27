package org.motechproject.tama.ivr.action.pillreminder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.IVRAuditMatcher;
import org.motechproject.tama.ivr.action.event.BaseActionTest;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.repository.IVRCallAudits;
import org.motechproject.tama.repository.Patients;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DoseRemindActionTest extends BaseActionTest {
    public static final String PATIENT_ID = "patientId";
    public static final String REGIMEN_ID = "regimenId";
    public static final String DOSAGE_ID = "dosageId";

    private DoseRemindAction action;
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
        initMocks(this);
        action = new DoseRemindAction(patients, clinics, service, messages, audits);
    }

    @Test
    public void shouldPlayClinicWelcomeDosageDrugsAndMenu() {
        IVRRequest ivrRequest = mock(IVRRequest.class);
        Patient patient = mock(Patient.class);
        Clinic clinic = mock(Clinic.class);

        Map params = new HashMap();
        params.put(PillReminderCall.REGIMEN_ID, REGIMEN_ID);
        params.put(PillReminderCall.DOSAGE_ID, DOSAGE_ID);

        when(service.medicinesFor(REGIMEN_ID, DOSAGE_ID)).thenReturn(Arrays.asList("m1", "m2"));
        when(session.getAttribute(IVRCallAttribute.PATIENT_DOC_ID)).thenReturn(PATIENT_ID);
        when(request.getSession(false)).thenReturn(session);
        when(patients.get("patientId")).thenReturn(patient);
        when(patient.getClinic_id()).thenReturn("clinicId");
        when(clinics.get("clinicId")).thenReturn(clinic);
        when(clinic.getName()).thenReturn("mayo");
        setUpIVRRequest(ivrRequest, params);
        setUpMessages();

        String responseXML = action.handle(ivrRequest, request, response);

        IVRAuditMatcher matcher = new IVRAuditMatcher(ivrRequest.getSid(), ivrRequest.getCid(), "patientId", IVRCallAudit.State.USER_AUTHORISED);

        assertEquals("<response sid=\"sid\">" +
                "<playaudio>mayo.wav</playaudio>" +
                "<playaudio>supposed_to_take.wav</playaudio><playaudio>m1.wav</playaudio>" +
                "<playaudio>supposed_to_take.wav</playaudio><playaudio>m2.wav</playaudio>" +
                "<collectdtmf><playaudio>menu.wav</playaudio></collectdtmf>" +
                "</response>", sanitize(responseXML));
    }

    private void setUpIVRRequest(IVRRequest ivrRequest, Map params) {
        when(ivrRequest.getTamaParams()).thenReturn(params);
        when(ivrRequest.getCid()).thenReturn("cid");
        when(ivrRequest.getSid()).thenReturn("sid");
    }

    private void setUpMessages() {
        when(messages.getWav(IVRMessage.YOU_ARE_SUPPOSED_TO_TAKE)).thenReturn("supposed_to_take.wav");
        when(messages.getWav("mayo")).thenReturn("mayo.wav");
        when(messages.getWav("m1")).thenReturn("m1.wav");
        when(messages.getWav("m2")).thenReturn("m2.wav");
        when(messages.getWav(IVRMessage.PILL_REMINDER_RESPONSE_MENU)).thenReturn("menu.wav");
    }
}
