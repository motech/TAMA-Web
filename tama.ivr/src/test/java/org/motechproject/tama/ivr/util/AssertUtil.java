package org.motechproject.tama.ivr.util;


import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.domain.CallState;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class AssertUtil {

    private KooKooIVRContext kookooIVRContext;
    private HttpSession httpSession;

    public AssertUtil(KooKooIVRContext kookooIVRContext, HttpSession httpSession) {
        this.kookooIVRContext = kookooIVRContext;
        this.httpSession = httpSession;
    }

    public void assertCallStateTransitionForKeyPress(String keyPressed, Map<String, Transition> transitions, CallState callState) {
        List<ITreeCommand> treeCommands = transitions.get(keyPressed).getDestinationNode().getTreeCommands();
        for (ITreeCommand treeCommand : treeCommands) {
            treeCommand.execute(kookooIVRContext);
        }

        verify(httpSession, atLeastOnce()).setAttribute(anyString(), eq(callState.toString()));
    }
}
