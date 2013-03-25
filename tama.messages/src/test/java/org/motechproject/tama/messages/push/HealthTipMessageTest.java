package org.motechproject.tama.messages.push;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.healthtips.criteria.ContinueToHealthTipsCriteria;
import org.motechproject.tama.healthtips.service.HealthTipService;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class HealthTipMessageTest {

    @Mock
    private HealthTipService healthTipService;
    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private ContinueToHealthTipsCriteria continueToHealthTipsCriteria;

    private HealthTipMessage healthTipMessage;

    @Before
    public void setup() {
        initMocks(this);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession httpSession = mock(HttpSession.class);
        Cookies cookies = mock(Cookies.class);

        when(request.getSession()).thenReturn(httpSession);
        when(kookooIVRContext.httpRequest()).thenReturn(request);
        when(kookooIVRContext.cookies()).thenReturn(cookies);
        healthTipMessage = new HealthTipMessage(healthTipService, continueToHealthTipsCriteria);
    }

    @Test
    public void shouldAddHealthTipMessageToResponse() {
        String nextHealthTip = "nextHealthTip";
        when(continueToHealthTipsCriteria.shouldContinue(anyString())).thenReturn(true);
        when(healthTipService.nextHealthTip(anyString())).thenReturn(nextHealthTip);

        KookooIVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder();
        healthTipMessage.addToResponse(ivrResponseBuilder, kookooIVRContext);
        assertEquals(asList(nextHealthTip), ivrResponseBuilder.getPlayAudios());
    }

    @Test
    public void shouldNotAddHealthTipMessageToResponseWhenUnableToDetermineHealthTips() {
        String nextHealthTip = "nextHealthTip";
        when(continueToHealthTipsCriteria.shouldContinue(anyString())).thenReturn(false);
        when(healthTipService.nextHealthTip(anyString())).thenReturn(nextHealthTip);

        KookooIVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder();
        healthTipMessage.addToResponse(ivrResponseBuilder, kookooIVRContext);
        assertTrue(ivrResponseBuilder.getPlayAudios().isEmpty());
    }

    @Test
    public void doesNotHaveHealthTipsWhenPatientDoesNotHaveRequiredData() {
        String nextHealthTip = "nextHealthTip";
        when(continueToHealthTipsCriteria.shouldContinue(anyString())).thenReturn(false);
        when(healthTipService.nextHealthTip(anyString())).thenReturn(nextHealthTip);

        assertFalse(healthTipMessage.hasAnyMessage(kookooIVRContext, null));
    }

    @Test
    public void doesNotHaveHealthTipsWhenThereAreNoMoreHealthTipsToBeRead() {
        String nextHealthTip = null;
        when(continueToHealthTipsCriteria.shouldContinue(anyString())).thenReturn(true);
        when(healthTipService.nextHealthTip(anyString())).thenReturn(nextHealthTip);

        assertFalse(healthTipMessage.hasAnyMessage(kookooIVRContext, null));
    }

    @Test
    public void shouldHaveHealthTipsWhenHealthTipsAreAvailable() {
        String nextHealthTip = "healthTip";
        when(continueToHealthTipsCriteria.shouldContinue(anyString())).thenReturn(true);
        when(healthTipService.nextHealthTip(anyString())).thenReturn(nextHealthTip);

        assertTrue(healthTipMessage.hasAnyMessage(kookooIVRContext, null));
    }

    @Test
    public void shouldMarkHealthTipAsRead() {
        String audioFileName = "audioFileName";
        String patientDocId = "patientDocumentId";

        healthTipMessage.markAsRead(patientDocId, audioFileName);
        verify(healthTipService).markAsPlayed(patientDocId, audioFileName);
    }

    @Test
    public void shouldNotMarkHealthTipAsReadWhenEmpty() {
        String audioFileName = "";
        String patientDocId = "patientDocumentId";

        healthTipMessage.markAsRead(patientDocId, audioFileName);
        verify(healthTipService, never()).markAsPlayed(patientDocId, audioFileName);
    }
}
