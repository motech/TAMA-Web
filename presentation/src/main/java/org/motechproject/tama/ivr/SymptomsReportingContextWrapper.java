package org.motechproject.tama.ivr;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.util.Cookies;

public class SymptomsReportingContextWrapper {

    public static String SWITCH_TO_DIAL_STATE = "switch_to_dial_state";
    public static String NUMBER_OF_CLINICIANS_CALLED = "number_of_clinicians_called";

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

    public int anotherClinicianCalled() {
        String value = cookies.getValue(NUMBER_OF_CLINICIANS_CALLED);
        int numberOfCliniciansCalled = StringUtils.isEmpty(value) ? 1 : Integer.valueOf(value) + 1;
        cookies.add(NUMBER_OF_CLINICIANS_CALLED, String.valueOf(numberOfCliniciansCalled));
        return numberOfCliniciansCalled;
    }
}
