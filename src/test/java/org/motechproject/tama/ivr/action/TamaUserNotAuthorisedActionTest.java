package org.motechproject.tama.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.IVREvent;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.ivr.audit.AuditService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TamaUserNotAuthorisedActionTest extends BaseActionTest {
    public static final String PATIENT_ID = "patientId";
    @Mock
    protected AuditService auditService;

    private TamaUserNotAuthorisedAction userNotAuthorisedAction;
    @Mock
    IVRRequest ivrRequest;

    @Before
    public void setUp() {
        super.setUp();
        userNotAuthorisedAction = new TamaUserNotAuthorisedAction(messages, auditService);
    }

    @Test
    public void shouldReturnUserNptAuthorisedResponse() {
    	when(ivrRequest.getSid()).thenReturn("sid");
        when(ivrRequest.getCid()).thenReturn("cid");
        when(ivrRequest.getEvent()).thenReturn(IVREvent.GOT_DTMF.name());
        when(ivrRequest.getData()).thenReturn("testdata");
        
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRSession.IVRCallAttribute.EXTERNAL_ID)).thenReturn(PATIENT_ID);

        String responseXML = userNotAuthorisedAction.createResponse(ivrRequest, request, response);

        verify(auditService).audit(ivrRequest, PATIENT_ID, IVRCallAudit.State.PASSCODE_ENTRY_FAILED);
        verify(session).invalidate();
        assertEquals("<response sid=\"sid\"><hangup/></response>", sanitize(responseXML));
    }

}
