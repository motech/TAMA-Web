package org.motechproject.tama.ivr.log;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tama.ivr.log.CallFlowConstants.TREE_TO_FLOW_MAP;

public class CallFlowDetailMapTest {

    @Mock
    private CallFlowGroupViews flowGroupViews;

    private CallFlowGroupView callFlowGroupView;

    @Before
    public void setUp() {
        initMocks(this);
        CallEventView callEventView = new CallEventView(new CallEvent(IVREvent.NewCall.toString()));
        callFlowGroupView = new CallFlowGroupView("Menu", callEventView);

        callFlowGroupView.setFlowStartTime(DateUtil.now());
        callFlowGroupView.setFlowEndTime(DateUtil.now().plusMinutes(2).plusSeconds(3));
        when(flowGroupViews.getCallFlowGroupViews()).thenReturn(new ArrayList<CallFlowGroupView>() {{
            add(callFlowGroupView);
        }});
    }

    @Test
    public void shouldPopulateMapWithFlowDetails() {
        CallFlowDetailMap callFlowDetailMap = new CallFlowDetailMap();

        callFlowDetailMap.populateFlowDetails(flowGroupViews);

        CallFlowDetails menuFlow = callFlowDetailMap.getCallFlowDetailsMap().get(CallTypeConstants.MENU);
        assertEquals(123, menuFlow.getTotalAccessDuration());
        assertEquals("123", menuFlow.getIndividualAccessDurations());
        assertEquals(1, menuFlow.getNumberOfTimesAccessed());

        CallFlowDetails healthTipsFlow = callFlowDetailMap.getCallFlowDetailsMap().get(CallTypeConstants.HEALTH_TIPS);
        assertEquals(0, healthTipsFlow.getTotalAccessDuration());
        assertEquals("NA", healthTipsFlow.getIndividualAccessDurations());
        assertEquals(0, healthTipsFlow.getNumberOfTimesAccessed());
    }

    @Test
    public void shouldNotPopulateMapWithFlowDetails_GivenAMissedCallLog() {
        when(flowGroupViews.hasMissedEvent()).thenReturn(true);
        CallFlowDetailMap callFlowDetailMap = new CallFlowDetailMap();
        callFlowDetailMap.populateFlowDetails(flowGroupViews);

        for (String key : CallFlowConstants.TREE_TO_FLOW_MAP.keySet()) {
            String flowName = TREE_TO_FLOW_MAP.get(key);
            CallFlowDetails flow = callFlowDetailMap.getCallFlowDetailsMap().get(flowName);
            assertEquals(0, flow.getTotalAccessDuration());
            assertEquals("NA", flow.getIndividualAccessDurations());
            assertEquals(0, flow.getNumberOfTimesAccessed());
        }

        CallFlowDetails healthTipsFlow = callFlowDetailMap.getCallFlowDetailsMap().get(CallTypeConstants.HEALTH_TIPS);
        assertEquals(0, healthTipsFlow.getTotalAccessDuration());
        assertEquals("NA", healthTipsFlow.getIndividualAccessDurations());
        assertEquals(0, healthTipsFlow.getNumberOfTimesAccessed());
    }
}
