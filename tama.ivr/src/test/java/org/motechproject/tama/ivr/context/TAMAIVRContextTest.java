package org.motechproject.tama.ivr.context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class TAMAIVRContextTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private KookooRequest kookooRequest;
    @Mock
    private Cookies cookies;
    @Mock
    private KooKooIVRContext kooKooIVRContext;
    @Mock
    private HttpSession httpSession;

    public TAMAIVRContext tamaivrContext;

    @Before
    public void setUp() {
        initMocks(this);
        when(kooKooIVRContext.httpRequest()).thenReturn(request);
        when(kooKooIVRContext.cookies()).thenReturn(cookies);
        when(request.getSession()).thenReturn(httpSession);
    }

    @Test
    public void numberOfAttemptsShouldBeInitialized() {
        String callerId = "123";

        when(kookooRequest.getCid()).thenReturn(callerId);

        tamaivrContext = new TAMAIVRContext(kookooRequest, request, cookies);
        tamaivrContext.initialize();

        verify(httpSession).setAttribute(TAMAIVRContext.NUMBER_OF_ATTEMPTS, "0");
        verify(httpSession).setAttribute(TAMAIVRContext.CALLER_ID, callerId);
    }

    @Test
    public void shouldResetContextForMenuRepeat() {
        tamaivrContext = new TAMAIVRContext(kooKooIVRContext);
        tamaivrContext.resetForMenuRepeat();

        verify(cookies).add("LastCompletedTree", null);
        verify(kooKooIVRContext).currentDecisionTreePath("");
        verify(httpSession).setAttribute("PillRegimen", null);
        verify(httpSession).setAttribute("call_state", CallState.AUTHENTICATED.toString());
    }

    @Test
    public void shouldNotResetPushedMessageAsPartOfMenuRepeat() {
        tamaivrContext = new TAMAIVRContext(kooKooIVRContext);
        tamaivrContext.resetForMenuRepeat();

        verify(cookies, never()).add(eq("messages_pushed"), anyString());
    }

    @Test
    public void shouldAddLastCompletedTreeToListOfCompletedTrees() {
        String lastTreeName = "lastTreeName";

        tamaivrContext = new TAMAIVRContext(kooKooIVRContext);
        tamaivrContext.lastCompletedTree(lastTreeName);

        verify(kooKooIVRContext).addToListOfCompletedTrees(lastTreeName);
    }

    @Test
    public void verifyIfNotTraversedThroughAnyTree() {
        tamaivrContext = new TAMAIVRContext(kooKooIVRContext);
        when(kooKooIVRContext.getListOfCompletedTrees()).thenReturn(new ArrayList<String>());

        assertFalse(tamaivrContext.hasTraversedAnyTree());
    }

    @Test
    public void verifyIfTraversedThroughATree() {
        String treeName = "completedTree";

        tamaivrContext = new TAMAIVRContext(kooKooIVRContext);
        when(kooKooIVRContext.getListOfCompletedTrees()).thenReturn(Arrays.asList(treeName));

        assertTrue(tamaivrContext.hasTraversedAnyTree());
    }

    @Test
    public void shouldAddCallStateAsDataToBeLogged() {
        tamaivrContext = new TAMAIVRContext(kooKooIVRContext);
        tamaivrContext.callState(CallState.PULL_MESSAGES);

        ArgumentCaptor<Map> dataToLogMapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(kooKooIVRContext).dataToLog((HashMap<String, String>) dataToLogMapCaptor.capture());
        HashMap<String, String> dataToLogMap = (HashMap<String, String>) dataToLogMapCaptor.getValue();
        assertEquals(1, dataToLogMap.size());
        assertEquals(CallState.PULL_MESSAGES.name(), dataToLogMap.get(CallEventConstants.CALL_STATE).toString());
    }

    @Test
    public void shouldAddMessageCategoryAsDataToBeLogged() {
        tamaivrContext = new TAMAIVRContext(kooKooIVRContext);
        tamaivrContext.setMessagesCategory("category");

        ArgumentCaptor<Map> dataToLogMapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(kooKooIVRContext).dataToLog((HashMap<String, String>) dataToLogMapCaptor.capture());
        HashMap<String, String> dataToLogMap = (HashMap<String, String>) dataToLogMapCaptor.getValue();
        assertEquals(1, dataToLogMap.size());
        assertEquals("category", dataToLogMap.get(TAMAIVRContext.MESSAGE_CATEGORY_NAME).toString());
    }
}
