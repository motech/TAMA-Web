package org.motechproject.tama.ivr.factory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageTransitionFactoryTest {

    @Mock
    private KooKooIVRContext kooKooIVRContext;
    @Mock
    private HttpSession httpSession;
    @Mock
    private Cookies cookies;

    @Before
    public void setup() {
        initMocks(this);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getSession()).thenReturn(httpSession);
        when(kooKooIVRContext.httpRequest()).thenReturn(request);
        when(kooKooIVRContext.cookies()).thenReturn(cookies);
    }

    @Test
    public void shouldSetCallSateOnMessageTransition() {
        Transition transition = MessageTransitionFactory.createTransition(CallState.PULL_MESSAGES, TAMAMessageType.ALL_MESSAGES.name());
        executeCommands(transition);
        verify(httpSession).setAttribute(CallEventConstants.CALL_STATE, CallState.PULL_MESSAGES.name());
    }

    @Test
    public void shouldSetMessageCategoryOnMessageTransition() {
        Transition transition = MessageTransitionFactory.createTransition(CallState.PULL_MESSAGES, TAMAMessageType.ALL_MESSAGES.name());
        executeCommands(transition);
        verify(cookies).add(TAMAIVRContext.MESSAGE_CATEGORY_NAME, TAMAMessageType.ALL_MESSAGES.name());
    }

    private void executeCommands(Transition transition) {
        List<ITreeCommand> treeCommands = transition.getDestinationNode().getTreeCommands();
        for (ITreeCommand treeCommand : treeCommands) {
            treeCommand.execute(kooKooIVRContext);
        }
    }
}
