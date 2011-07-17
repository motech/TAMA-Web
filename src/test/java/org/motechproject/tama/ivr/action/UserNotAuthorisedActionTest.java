package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.domain.IVRCallAudit;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.event.BaseActionTest;
import org.motechproject.tama.repository.IVRCallAudits;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserNotAuthorisedActionTest extends BaseActionTest {
    private UserNotAuthorisedAction userNotAuthorisedAction;
    @Mock
    private IVRMessage messages;
    @Mock
    private IVRCallAudits audits;

    @Before
    public void setUp() {
        initMocks(this);
        userNotAuthorisedAction = new UserNotAuthorisedAction(messages, audits);
    }

    @Test
    public void shouldReturnUserNptAuthorisedResponse() {
        IVRRequest ivrRequest = new IVRRequest("sid", "cid", "event", "data");

        when(request.getSession(false)).thenReturn(session);
        when(messages.get(IVR.MessageKey.TAMA_IVR_REPORT_USER_NOT_AUTHORISED)).thenReturn("Not authorised");
        when(session.getAttribute(IVR.Attributes.PATIENT_DOCUMENT_ID)).thenReturn("patientId");

        String handle = userNotAuthorisedAction.handle(ivrRequest, request, response);

        IVRAuditMatcher matcher = new IVRAuditMatcher(ivrRequest.getSid(), ivrRequest.getCid(), "patientId", IVRCallAudit.State.PASSCODE_ENTRY_FAILED);
        verify(audits).add(argThat(matcher));
        assertEquals("<response sid=\"sid\"><playtext>Not authorised</playtext><hangup/></response>", StringUtils.replace(handle, "\n", ""));
    }

}
