package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.ivr.IVR;

import java.util.Map;

public class Actions {
    private Map<String, IVRAction> map;

    public Actions(Map<String, IVRAction> map) {
        this.map = map;
    }

    public IVRAction findFor(IVR.Event event) {
        String key = StringUtils.lowerCase(event.key());
        return map.get(key);
    }
}
