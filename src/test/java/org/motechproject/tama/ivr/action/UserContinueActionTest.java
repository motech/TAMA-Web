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

public class UserContinueActionTest extends BaseActionTest {

    private UserContinueAction userContinueAction;
    @Mock
    private IVRMessage messages;

    @Before
    public void setUp() {
        initMocks(this);
        userContinueAction = new UserContinueAction(messages);
    }

    @Test
    public void shouldReturnUserProceedResponse() {
        IVRRequest ivrRequest = new IVRRequest();
        when(messages.get(IVR.MessageKey.TAMA_IVR_RESPONSE_AFTER_AUTH)).thenReturn("Please continue");

        String handle = userContinueAction.handle(ivrRequest, request, response);
        assertEquals("<response><playtext>Please continue</playtext></response>", StringUtils.replace(handle, "\n", ""));
    }

}
