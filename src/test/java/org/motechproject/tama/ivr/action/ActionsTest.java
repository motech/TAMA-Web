package org.motechproject.tama.ivr.action;

import org.junit.Test;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.action.event.HangupEventAction;
import org.motechproject.tama.ivr.action.event.NewCallEventAction;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ActionsTest {

    @Test
    public void shouldReturnActionBasedOnKey() {
        Map<String, IVRIncomingAction> map = new HashMap<String, IVRIncomingAction>();
        map.put("newcall", new NewCallEventAction(null, null, null));
        map.put("hangup", new HangupEventAction());

        Actions actions = new Actions(map);

        assertTrue(actions.findFor(IVR.Event.NEW_CALL).getClass().equals(NewCallEventAction.class));
        assertTrue(actions.findFor(IVR.Event.HANGUP).getClass().equals(HangupEventAction.class));
    }
}
