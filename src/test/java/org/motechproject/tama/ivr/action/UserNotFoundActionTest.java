package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.event.BaseActionTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserNotFoundActionTest extends BaseActionTest {
    private UserNotFoundAction userNotFoundAction;
    @Mock
    private IVRMessage messages;

    @Before
    public void setUp() {
        initMocks(this);
        userNotFoundAction = new UserNotFoundAction(messages);
    }

    @Test
    public void shouldReturnUserNotFoundResponse() {
        IVRRequest ivrRequest = new IVRRequest();
        when(messages.get(IVR.MessageKey.TAMA_IVR_REPORT_USER_NOT_FOUND)).thenReturn("Not found");

        String handle = userNotFoundAction.handle(ivrRequest, request, response);
        assertEquals("<response><playtext>Not found</playtext><hangup/></response>", StringUtils.replace(handle, "\n", ""));
    }

}
