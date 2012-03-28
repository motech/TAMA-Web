package org.motechproject.tama.web.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.tama.web.view.CallEventView;
import org.motechproject.tama.web.view.CallFlowGroupView;
import org.motechproject.tama.web.view.CallLogView;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallFlowDetailMapTest {

    @Mock
    private CallLogView callLogView;

    CallFlowGroupView callFlowGroupView;

    @Before
    public void setUp() {
        initMocks(this);
        CallEventView callEventView = new CallEventView(new CallEvent(IVREvent.NewCall.toString()));
        callFlowGroupView = new CallFlowGroupView("Menu", callEventView);

        callFlowGroupView.setFlowStartTime(DateUtil.now());
        callFlowGroupView.setFlowEndTime(DateUtil.now().plusMinutes(2).plusSeconds(3));

        when(callLogView.getCallFlowGroupViews()).thenReturn(new ArrayList<CallFlowGroupView>() {{
            add(callFlowGroupView);
        }});
    }

    @Test
    public void shouldPopulateMapWithFlowDetails() {
        CallFlowDetailMap callFlowDetailMap = new CallFlowDetailMap();

        callFlowDetailMap.populateFlowDetails(Arrays.asList(callLogView));

        CallFlowDetails menuFlow = callFlowDetailMap.getCallFlowDetailsMap().get(CallTypeConstants.MENU);
        assertEquals(123, menuFlow.getTotalAccessDuration());
        assertEquals("123", menuFlow.getIndividualAccessDurations());
        assertEquals(1, menuFlow.getNumberOfTimesAccessed());

        CallFlowDetails healthTipsFlow = callFlowDetailMap.getCallFlowDetailsMap().get(CallTypeConstants.HEALTH_TIPS);
        assertEquals(0, healthTipsFlow.getTotalAccessDuration());
        assertEquals("NA", healthTipsFlow.getIndividualAccessDurations());
        assertEquals(0, healthTipsFlow.getNumberOfTimesAccessed());
    }
}
