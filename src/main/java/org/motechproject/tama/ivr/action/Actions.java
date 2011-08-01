package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.ivr.IVREvent;

import java.util.Map;

public class Actions {
    private Map<String, BaseIncomingAction> map;

    public Actions(Map<String, BaseIncomingAction> map) {
        this.map = map;
    }

    public BaseIncomingAction findFor(IVREvent event) {
        String key = StringUtils.lowerCase(event.key());
        return map.get(key);
    }
}
