package org.motechproject.tama.messages.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.common.domain.TAMAMessageTypes;
import org.motechproject.tama.messages.service.Messages;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullMessagesControllerTest {

    @Mock
    private Messages messages;
    @Mock
    private KooKooIVRContext kooKooIVRContext;
    @Mock
    private Cookies cookies;
    @Mock
    private HttpSession httpSession;
    @Mock
    private KookooRequest kookooRequest;

    private String patientId = "patientId";

    private PullMessagesController pullMessagesController;

    @Before
    public void setUp() {
        initMocks(this);
        setupSession();
        setupCookies();
        pullMessagesController = new PullMessagesController(messages);
    }

    private void setupSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(httpSession);
        when(kooKooIVRContext.kooKooRequest()).thenReturn(kookooRequest);
        when(kooKooIVRContext.httpRequest()).thenReturn(request);
        when(httpSession.getAttribute(TAMAIVRContext.PATIENT_ID)).thenReturn(patientId);
    }

    private void setupCookies() {
        when(kooKooIVRContext.cookies()).thenReturn(cookies);
    }

    @Test
    public void shouldMarkMessageAsReadAndContinueIfMessageIsAlreadyPlayed() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn("healthTip");
        boolean shouldContinue = pullMessagesController.markAsReadAndContinue(kooKooIVRContext);
        assertTrue(shouldContinue);
    }

    @Test
    public void shouldContinueIfNoMessageWasPlayed() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn(null);
        boolean shouldContinue = pullMessagesController.markAsReadAndContinue(kooKooIVRContext);
        assertTrue(shouldContinue);
    }

    @Test
    public void shouldReturnAnEmptyResponseIfDTMFInputIs9() {
        when(kookooRequest.getInput()).thenReturn("9");
        KookooIVRResponseBuilder kookooIVRResponse = pullMessagesController.gotDTMF(kooKooIVRContext);
        assertTrue(kookooIVRResponse.isEmpty());
    }

    @Test
    public void shouldPlayNextMessageWhenMessageCategoryIsAllMessages() {
        KookooIVRResponseBuilder response = new KookooIVRResponseBuilder().withPlayAudios("audio");

        when(kookooRequest.getInput()).thenReturn("1");
        when(cookies.getValue(TAMAIVRContext.MESSAGE_CATEGORY_NAME)).thenReturn(TAMAMessageTypes.ALL_MESSAGES.name());
        when(messages.nextMessage(kooKooIVRContext)).thenReturn(response);

        KookooIVRResponseBuilder kookooIVRResponse = pullMessagesController.gotDTMF(kooKooIVRContext);
        assertEquals(response, kookooIVRResponse);
        assertTrue(kookooIVRResponse.isCollectDTMF());
    }

    @Test
    public void shouldNotCollectDTMFWhenNoMoreMessagesAreToBePlayed() {
        KookooIVRResponseBuilder emptyResponse = new KookooIVRResponseBuilder();

        when(kookooRequest.getInput()).thenReturn("1");
        when(cookies.getValue(TAMAIVRContext.MESSAGE_CATEGORY_NAME)).thenReturn(TAMAMessageTypes.ALL_MESSAGES.name());
        when(messages.nextMessage(kooKooIVRContext)).thenReturn(emptyResponse);

        KookooIVRResponseBuilder kookooIVRResponse = pullMessagesController.gotDTMF(kooKooIVRContext);
        assertFalse(kookooIVRResponse.isCollectDTMF());
    }

    @Test
    public void shouldPlayPress9ToGoBackToMainMenuIfThereIsAnyMessage() {
        KookooIVRResponseBuilder emptyResponse = new KookooIVRResponseBuilder().withPlayAudios("audio");

        when(kookooRequest.getInput()).thenReturn("1");
        when(cookies.getValue(TAMAIVRContext.MESSAGE_CATEGORY_NAME)).thenReturn(TAMAMessageTypes.ALL_MESSAGES.name());
        when(messages.nextMessage(kooKooIVRContext)).thenReturn(emptyResponse);

        KookooIVRResponseBuilder kookooIVRResponse = pullMessagesController.gotDTMF(kooKooIVRContext);
        assertTrue(kookooIVRResponse.getPlayAudios().contains(TamaIVRMessage.PRESS_9_FOR_MAIN_MENU));
    }

    @Test
    public void shouldPlayNextHealthTipWhenMessageCategoryIsNotAllMessages() {
        KookooIVRResponseBuilder response = new KookooIVRResponseBuilder().withPlayAudios("audio");

        when(kookooRequest.getInput()).thenReturn("1");
        when(cookies.getValue(TAMAIVRContext.MESSAGE_CATEGORY_NAME)).thenReturn(TAMAMessageTypes.FAMILY_AND_CHILDREN.name());
        when(messages.nextHealthTip(kooKooIVRContext, TAMAMessageTypes.FAMILY_AND_CHILDREN)).thenReturn(response);

        KookooIVRResponseBuilder kookooIVRResponse = pullMessagesController.gotDTMF(kooKooIVRContext);
        assertEquals(response, kookooIVRResponse);
        assertTrue(kookooIVRResponse.isCollectDTMF());
    }
}
