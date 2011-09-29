package org.motechproject.tama.ivr.action;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.action.IvrAction;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.server.service.ivr.IVRCallState;
import org.motechproject.server.service.ivr.IVREvent;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.server.service.ivr.IVRSession.IVRCallAttribute;
import org.motechproject.tama.domain.IVRLanguage;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class TamaAuthenticateActionTest extends BaseActionTest {
    private TamaAuthenticateAction authenticateAction;
    @Mock
    private AllPatients allPatients;
    @Mock
    private TamaRetryAction retryAction;
    @Mock
    private TamaUserNotFoundAction userNotFoundAction;
    @Mock
    private Patient patient;
    @Mock
    private IvrAction tamaIvrAction;
    @Mock
    private PillReminderService pillReminderService;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;

    private static final String PATIENT_ID = "12345";
    public static final String MOBILE_NO = "9876543210";
    public static final String PASSCODE = "1234";

    @Before
    public void setUp() {
        super.setUp();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(patient.getId()).thenReturn(PATIENT_ID);
        when(patient.getIvrLanguage()).thenReturn(IVRLanguage.newIVRLanguage("English", "en"));
        when(allTreatmentAdvices.findByPatientId(PATIENT_ID)).thenReturn(new TreatmentAdvice());
        when(session.getAttribute(TamaSessionAttribute.PATIENT_DOC_ID)).thenReturn(PATIENT_ID);
        when(request.getParameter("symptoms_reporting")).thenReturn("true");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRCallAttribute.CALLER_ID)).thenReturn(MOBILE_NO);
        authenticateAction = new TamaAuthenticateAction(pillReminderService, allPatients, allTreatmentAdvices, retryAction, userNotFoundAction, null, null, null);
    }

    @Test
    public void shouldGoToRetryActionIfPatientPassCodeIsNotValid() {
        IVRRequest ivrRequest = new KookooRequest("sid", MOBILE_NO, IVREvent.GOT_DTMF.key(), PASSCODE);

        when(retryAction.createResponse(ivrRequest, request, response)).thenReturn("OK");
        when(patient.authenticatedWith(PASSCODE)).thenReturn(false);
        when(patient.isActive()).thenReturn(true);
        when(allPatients.findByMobileNumberAndPasscode(MOBILE_NO, PASSCODE)).thenReturn(patient);
        when(allPatients.findByMobileNumber(MOBILE_NO)).thenReturn(patient);

        String handle = authenticateAction.handle(ivrRequest, request, response, tamaIvrAction);

        verify(retryAction).createResponse(ivrRequest, request, response);
        assertEquals("OK", handle);
    }

    @Test
    public void shouldGoToTamaIvrActionIfPatientPassCodeIsValid() {
        IVRRequest ivrRequest = new KookooRequest("sid", MOBILE_NO, IVREvent.GOT_DTMF.key(), PASSCODE);
        DateTime now = new DateTime(2010, 10, 10, 16, 00, 00);

        when(request.getSession()).thenReturn(session);
        when(patient.authenticatedWith(PASSCODE)).thenReturn(true);
        when(patient.isActive()).thenReturn(true);
        when(patient.getPatientId()).thenReturn(PATIENT_ID);
        when(allPatients.findByMobileNumberAndPasscode(MOBILE_NO, PASSCODE)).thenReturn(patient);
        when(allPatients.findByMobileNumber(MOBILE_NO)).thenReturn(patient);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse(null, PATIENT_ID, 2, 5, null);
        when(pillReminderService.getPillRegimen(PATIENT_ID)).thenReturn(pillRegimenResponse);

        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(now);

        authenticateAction.handle(ivrRequest, request, response, tamaIvrAction);

        verify(session).invalidate();
        verify(session).setAttribute(IVRCallAttribute.CALL_STATE, IVRCallState.AUTH_SUCCESS);
        verify(session).setAttribute(IVRCallAttribute.CALL_TIME, now);
        verify(session).setAttribute(TamaSessionAttribute.PATIENT_DOC_ID, PATIENT_ID);
        verify(session).setAttribute(TamaSessionAttribute.REGIMEN_FOR_PATIENT, pillRegimenResponse);
        verify(session).setAttribute(IVRCallAttribute.PREFERRED_LANGUAGE_CODE, "en");
        verify(session).setAttribute(TamaSessionAttribute.SYMPTOMS_REPORTING_PARAM, "true");
        verify(tamaIvrAction).handle(any(IVRRequest.class), any(IVRSession.class));
    }

    @Test
    public void shouldHangupIfPatientIsNotActive() {
        IVRRequest ivrRequest = new KookooRequest("unique-call-id", MOBILE_NO, IVREvent.NEW_CALL.key(), "Data");
        when(allPatients.findByMobileNumber(MOBILE_NO)).thenReturn(patient);
        when(allPatients.findByMobileNumberAndPasscode(MOBILE_NO, "Data")).thenReturn(patient);
        when(patient.isActive()).thenReturn(false);
        when(patient.authenticatedWith("Data")).thenReturn(true);
        when(userNotFoundAction.createResponse(ivrRequest, request, response)).thenReturn("hangup response");
        String responseXML = authenticateAction.createResponse(ivrRequest, request, response);
        assertEquals("hangup response", responseXML);
    }
}
