package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.ivr.IVREvent;
import org.motechproject.tama.ivr.action.event.BaseEventAction;

import java.util.Map;

public class Actions {
    private Map<String, BaseEventAction> map;

    public Actions(Map<String, BaseEventAction> map) {
        this.map = map;
    }

    public BaseEventAction findFor(IVREvent event) {
        String key = StringUtils.lowerCase(event.key());
        return map.get(key);
    }
}
