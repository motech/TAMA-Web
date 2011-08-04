package org.motechproject.tama.ivr.action;

import org.junit.Test;
import org.motechproject.tama.ivr.IVREvent;
import org.motechproject.tama.ivr.action.event.HangupEventAction;
import org.motechproject.tama.ivr.action.event.NewCallEventAction;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ActionsTest {

    @Test
    public void shouldReturnActionBasedOnKey() {
        Map<String, BaseIncomingAction> map = new HashMap<String, BaseIncomingAction>();
        map.put("newcall", new NewCallEventAction(null, null, null, null));
        map.put("hangup", new HangupEventAction());

        Actions actions = new Actions(map);

        assertTrue(actions.findFor(IVREvent.NEW_CALL).getClass().equals(NewCallEventAction.class));
        assertTrue(actions.findFor(IVREvent.HANGUP).getClass().equals(HangupEventAction.class));
    }
}
