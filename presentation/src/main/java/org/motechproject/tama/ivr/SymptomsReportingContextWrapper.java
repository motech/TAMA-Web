package org.motechproject.tama.ivr;

import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.util.Cookies;

public class SymptomsReportingContextWrapper {

    public static String SWITCH_TO_DIAL_STATE = "switch_to_dial_state";

    private Cookies cookies;

    protected SymptomsReportingContextWrapper() {
    }

    public SymptomsReportingContextWrapper(KooKooIVRContext kooKooIVRContext) {
        this.cookies = kooKooIVRContext.cookies();
    }

    public boolean isDialState() {
        return "true".equals(cookies.getValue(SWITCH_TO_DIAL_STATE));
    }

    public void isDialState(boolean dialState) {
        cookies.add(SWITCH_TO_DIAL_STATE, String.valueOf(dialState));
    }
}
