package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.ivr.IVREvent;

import java.util.Map;

public class Actions {
    private Map<String, IVRIncomingAction> map;

    public Actions(Map<String, IVRIncomingAction> map) {
        this.map = map;
    }

    public IVRIncomingAction findFor(IVREvent event) {
        String key = StringUtils.lowerCase(event.key());
        return map.get(key);
    }
}
