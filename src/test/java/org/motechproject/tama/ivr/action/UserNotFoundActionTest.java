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

public class UserNotFoundActionTest extends BaseActionTest {
    private UserNotFoundAction userNotFoundAction;
    @Mock
    private IVRMessage messages;
    @Mock
    private IVRCallAudits audits;

    @Before
    public void setUp() {
        initMocks(this);
        userNotFoundAction = new UserNotFoundAction(messages, audits);
    }

    @Test
    public void shouldReturnUserNotFoundResponse() {
        IVRRequest ivrRequest = new IVRRequest("sid", "cid", "event", "data");
        when(messages.get(IVR.MessageKey.TAMA_IVR_REPORT_USER_NOT_FOUND)).thenReturn("Not found");

        String handle = userNotFoundAction.handle(ivrRequest, request, response);

        IVRAuditMatcher matcher = new IVRAuditMatcher(ivrRequest.getSid(), ivrRequest.getCid(), StringUtils.EMPTY, IVRCallAudit.State.USER_NOT_FOUND);
        verify(audits).add(argThat(matcher));
        assertEquals("<response sid=\"sid\"><playtext>Not found</playtext><hangup/></response>", StringUtils.replace(handle, "\n", ""));
    }
}

