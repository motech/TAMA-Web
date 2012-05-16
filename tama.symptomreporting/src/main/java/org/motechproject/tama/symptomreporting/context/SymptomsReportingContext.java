package org.motechproject.tama.symptomreporting.context;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;

public class SymptomsReportingContext extends TAMAIVRContext {

    public static String NUMBER_OF_CLINICIANS_CALLED = "number_of_clinicians_called";

    public SymptomsReportingContext(KooKooIVRContext kooKooIVRContext) {
        super(kooKooIVRContext);
    }

    protected SymptomsReportingContext() {
    }

    public void startCall() {
        kooKooIVRContext.addToCallSession(TAMAIVRContext.SWITCH_TO_DIAL_STATE, String.valueOf(true));
    }

    public void endCall() {
        kooKooIVRContext.addToCallSession(TAMAIVRContext.SWITCH_TO_DIAL_STATE, String.valueOf(false));
    }

    public int numberOfCliniciansCalled() {
        String value = kooKooIVRContext.getFromCallSession(NUMBER_OF_CLINICIANS_CALLED);
        return StringUtils.isEmpty(value) ? 0 : Integer.valueOf(value);
    }

    public int anotherClinicianCalled() {
        int numberOfCliniciansCalled = numberOfCliniciansCalled() + 1;
        kooKooIVRContext.addToCallSession(NUMBER_OF_CLINICIANS_CALLED, String.valueOf(numberOfCliniciansCalled));
        return numberOfCliniciansCalled;
    }
}
