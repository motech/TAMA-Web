package org.motechproject.tamafunctional.testdataservice;

import org.motechproject.tamafunctional.framework.MyWebClient;
import org.motechproject.tamafunctional.ivr.Caller;

public class PatientCallService {
    private MyWebClient webClient;

    public PatientCallService(MyWebClient webClient) {
        this.webClient = webClient;
    }

    public void takenPill(String phoneNumber, String pinNumber) {
        Caller caller = new Caller("123", phoneNumber, webClient);
        caller.call();
        caller.enter(pinNumber);
        caller.enter("1");
        caller.hangup();
    }
}
