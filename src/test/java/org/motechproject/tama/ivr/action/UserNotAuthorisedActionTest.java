package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.event.BaseActionTest;
import org.motechproject.tama.repository.IVRCallAudits;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserNotAuthorisedActionTest extends BaseActionTest {
    private UserNotAuthorisedAction userNotAuthorisedAction;

    @Before
    public void setUp() {
        super.setUp();
        userNotAuthorisedAction = new UserNotAuthorisedAction(messages, audits);
    }

    @Test
    public void shouldReturnUserNptAuthorisedResponse() {
        IVRRequest ivrRequest = new IVRRequest("sid", "cid", "event", "data");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRCallAttribute.PATIENT_DOC_ID)).thenReturn("patientId");

        String responseXML = userNotAuthorisedAction.handle(ivrRequest, request, response);

        IVRAuditMatcher matcher = new IVRAuditMatcher(ivrRequest.getSid(), ivrRequest.getCid(), "patientId", IVRCallAudit.State.PASSCODE_ENTRY_FAILED);
        verify(audits).add(argThat(matcher));
        verify(session).invalidate();
        assertEquals("<response sid=\"sid\"><hangup/></response>", sanitize(responseXML));
    }

}
