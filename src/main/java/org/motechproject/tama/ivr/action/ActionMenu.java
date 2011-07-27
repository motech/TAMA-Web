package org.motechproject.tama.ivr.action;

import java.util.HashMap;
import java.util.Map;

public class ActionMenu {
    private Map<String, IVRIncomingAction> actions = new HashMap<String, IVRIncomingAction>();

    public void add(IVRIncomingAction... actions) {
        for (IVRIncomingAction action : actions) {
            this.actions.put(action.getKey(), action);
        }
    }

    public IVRIncomingAction get(String key) {
        return actions.get(key);
    }
}
