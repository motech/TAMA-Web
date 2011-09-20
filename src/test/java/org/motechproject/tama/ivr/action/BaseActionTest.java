package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.IVRMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BaseActionTest {
    public static final String SIGNATURE_MUSIC = "http://music";
    @Mock
    protected HttpServletRequest request;
    @Mock
    protected HttpServletResponse response;
    @Mock
    protected HttpSession session;
    @Mock
    protected IVRMessage messages;

    @Before
    public void setUp() {
        initMocks(this);
        mockIVRMessage();
    }

    protected void mockIVRMessage() {
        when(messages.getSignatureMusic()).thenReturn(SIGNATURE_MUSIC);
    }

    protected String sanitize(String responseXML) {
        return StringUtils.replace(responseXML, System.getProperty("line.separator"), "");
    }
}
