package org.motechproject.tamacallflow.ivr.context;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.util.Cookies;

public class SymptomsReportingContext {

    public static String SWITCH_TO_DIAL_STATE = "switch_to_dial_state";
    public static String NUMBER_OF_CLINICIANS_CALLED = "number_of_clinicians_called";

    private Cookies cookies;

    public SymptomsReportingContext(KooKooIVRContext kooKooIVRContext) {
        this(kooKooIVRContext.cookies());
    }

    private SymptomsReportingContext(Cookies cookies) {
        this.cookies = cookies;
    }

    public boolean isDialState() {
        return Boolean.valueOf(cookies.getValue(SWITCH_TO_DIAL_STATE));
    }

    public void startCall() {
        cookies.add(SWITCH_TO_DIAL_STATE, String.valueOf(true));
    }

    public void endCall() {
        cookies.add(SWITCH_TO_DIAL_STATE, String.valueOf(false));
    }

    public int numberOfCliniciansCalled() {
        String value = cookies.getValue(NUMBER_OF_CLINICIANS_CALLED);
        return StringUtils.isEmpty(value) ? 0 : Integer.valueOf(value);
    }

    public int anotherClinicianCalled() {
        int numberOfCliniciansCalled = numberOfCliniciansCalled() + 1;
        cookies.add(NUMBER_OF_CLINICIANS_CALLED, String.valueOf(numberOfCliniciansCalled));
        return numberOfCliniciansCalled;
    }
}
