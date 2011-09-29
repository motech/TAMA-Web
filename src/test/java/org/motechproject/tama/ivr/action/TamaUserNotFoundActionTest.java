package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.IVREvent;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.ivr.audit.AuditService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TamaUserNotFoundActionTest extends BaseActionTest {
    private TamaUserNotFoundAction userNotFoundAction;
    @Mock
    private IVRRequest ivrRequest;
    @Mock
    private AuditService auditService;

    @Before
    public void setUp() {
        super.setUp();
        userNotFoundAction = new TamaUserNotFoundAction(auditService);
    }

    @Test
    public void shouldReturnUserNotFoundResponse() {
        when(ivrRequest.getSid()).thenReturn("sid");
        when(ivrRequest.getCid()).thenReturn("cid");
        when(ivrRequest.getEvent()).thenReturn(IVREvent.GOT_DTMF.name());
        when(ivrRequest.getData()).thenReturn("testdata");
        String responseXML = userNotFoundAction.createResponse(ivrRequest, request, response);

        verify(auditService).audit(ivrRequest, StringUtils.EMPTY, IVRCallAudit.State.USER_NOT_FOUND);
        assertEquals("<response sid=\"sid\"><hangup/></response>", sanitize(responseXML));
    }
}

