package org.motechproject.tama.symptomreporting.context;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.domain.CallState;

public class SymptomsReportingContext extends TAMAIVRContext {

    public static String NUMBER_OF_CLINICIANS_CALLED = "number_of_clinicians_called";

    public SymptomsReportingContext(KooKooIVRContext kooKooIVRContext) {
        super(kooKooIVRContext);
    }

    protected SymptomsReportingContext() {
    }

    public void startCall() {
        callState(CallState.DIAL);
    }

    public void endCall() {
        callState(CallState.ALL_TREES_COMPLETED);
    }

    public int numberOfCliniciansCalled() {
        String value = cookies().getValue(NUMBER_OF_CLINICIANS_CALLED);
        return StringUtils.isEmpty(value) ? 0 : Integer.valueOf(value);
    }

    public int anotherClinicianCalled() {
        int numberOfCliniciansCalled = numberOfCliniciansCalled() + 1;
        cookies().add(NUMBER_OF_CLINICIANS_CALLED, String.valueOf(numberOfCliniciansCalled));
        return numberOfCliniciansCalled;
    }
}
