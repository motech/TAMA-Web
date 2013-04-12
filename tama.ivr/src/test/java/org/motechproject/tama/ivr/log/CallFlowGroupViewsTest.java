package org.motechproject.tama.ivr.log;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.util.DateUtil;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.fail;

public class CallFlowGroupViewsTest {

    @Test
    public void shouldHavePullMessagesFlowWhenPullMessagesWereAccessedInCall() {
        CallLog callLog = new CallLog();
        callLog.setCallEvents(asList(newCallEvent(CallState.PULL_MESSAGES_TREE.name(), TAMATreeRegistry.PULL_MESSAGES_TREE, TAMAMessageType.ALL_MESSAGES.name())));

        CallFlowGroupViews callFlowGroupViews = new CallFlowGroupViews(callLog);
        assertHasFlow(CallTypeConstants.MESSAGES, callFlowGroupViews.getCallFlowGroupViews());
    }

    @Test
    public void shouldPullMessagesWhenAnyMessageOfCategoryWasAccessed() {
        CallLog callLog = new CallLog();
        callLog.setCallEvents(asList(newCallEvent(CallState.PULL_MESSAGES.name(), null, TAMAMessageType.ALL_MESSAGES.name())));

        CallFlowGroupViews callFlowGroupViews = new CallFlowGroupViews(callLog);
        assertHasFlow(CallTypeConstants.MESSAGES, callFlowGroupViews.getCallFlowGroupViews());
    }

    @Test
    public void shouldHaveMessagesFlowWhenMessagesArePushedInTheCall() {
        CallLog callLog = new CallLog();
        callLog.setCallEvents(asList(newCallEvent(CallState.PUSH_MESSAGES.name(), null, null)));

        CallFlowGroupViews callFlowGroupViews = new CallFlowGroupViews(callLog);
        assertHasFlow(CallTypeConstants.PUSHED_MESSAGES, callFlowGroupViews.getCallFlowGroupViews());
    }

    private CallEvent newCallEvent(String callState, String treeName, String categoryName) {
        CallEvent callEvent = new CallEvent(IVREvent.GotDTMF.name());
        callEvent.setTimeStamp(DateUtil.now().minusSeconds(1));
        callEvent.appendData(CallEventConstants.CALL_STATE, callState);
        callEvent.appendData(CallEventConstants.TREE_NAME, treeName);
        callEvent.appendData(TAMAIVRContext.MESSAGE_CATEGORY_NAME, categoryName);
        return callEvent;
    }

    private void assertHasFlow(String flowName, List<CallFlowGroupView> callFlowGroupViews) {
        for (CallFlowGroupView callFlowGroupView : callFlowGroupViews) {
            if (StringUtils.equals(flowName, callFlowGroupView.getFlow()))
                return;
        }
        fail("Expected flow not found. Expected: " + flowName + " Actual : " + callFlowGroupViews);
    }
}
