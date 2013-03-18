package org.motechproject.tama.messages;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.healthtips.service.HealthTipService;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PushedHealthTipMessageTest {

    @Mock
    private HealthTipService healthTipService;
    @Mock
    private KooKooIVRContext kookooIVRContext;

    private PushedHealthTipMessage pushedHealthTipMessage;

    @Before
    public void setup() {
        initMocks(this);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession httpSession = mock(HttpSession.class);
        Cookies cookies = mock(Cookies.class);

        when(request.getSession()).thenReturn(httpSession);
        when(kookooIVRContext.httpRequest()).thenReturn(request);
        when(kookooIVRContext.cookies()).thenReturn(cookies);
        pushedHealthTipMessage = new PushedHealthTipMessage(healthTipService);
    }

    @Test
    public void shouldPlayHealthTipsWhenThereAreNoAdherenceMessages() {
        String nextHealthTip = "nextHealthTip";
        when(healthTipService.nextHealthTip(anyString())).thenReturn(nextHealthTip);

        KookooIVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder();
        pushedHealthTipMessage.addToResponse(ivrResponseBuilder, kookooIVRContext);
        assertEquals(asList(nextHealthTip), ivrResponseBuilder.getPlayAudios());
    }
}
