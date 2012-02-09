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
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    public void shouldAddLastCompletedTreeToListOfCompletedTrees() {
        String lastTreeName = "lastTreeName";

        tamaivrContext = new TAMAIVRContext(kooKooIVRContext);
        tamaivrContext.lastCompletedTree(lastTreeName);

        verify(kooKooIVRContext).addToListOfCompletedTrees(lastTreeName);
    }
    
    @Test
    public void shouldAddCallState_ToDataBucket(){
        tamaivrContext = new TAMAIVRContext(kooKooIVRContext);
        tamaivrContext.callState(CallState.HEALTH_TIPS);

        ArgumentCaptor<Map> dataToLogMapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(kooKooIVRContext).dataToLog((HashMap<String, String>) dataToLogMapCaptor.capture());
        HashMap<String, String> dataToLogMap = (HashMap<String, String>) dataToLogMapCaptor.getValue();
        assertEquals(1, dataToLogMap.size());
        assertEquals(CallState.HEALTH_TIPS.name(), dataToLogMap.get(CallEventConstants.CALL_STATE).toString());
    }
}
