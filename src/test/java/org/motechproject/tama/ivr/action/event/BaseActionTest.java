package org.motechproject.tama.ivr.action.event;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.mockito.Mock;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.repository.IVRCallAudits;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public abstract class BaseActionTest {
    @Mock
    protected HttpServletRequest request;
    @Mock
    protected HttpServletResponse response;
    @Mock
    protected HttpSession session;
    @Mock
    protected IVRMessage messages;
    @Mock
    protected IVRCallAudits audits;

    @Before
    public void setUp() {
        initMocks(this);
        mockIVRMessage();
    }

    protected void mockIVRMessage() {
        when(messages.getWav(IVRMessage.SIGNATURE_MUSIC_URL, "en")).thenReturn("http://music");
    }

    protected String sanitize(String responseXML) {
        return StringUtils.replace(responseXML, System.getProperty("line.separator"), "");
    }
}
