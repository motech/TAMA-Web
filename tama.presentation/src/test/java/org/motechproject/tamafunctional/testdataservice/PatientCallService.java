package org.motechproject.tamafunctional.testdataservice;

import org.motechproject.tamafunctional.framework.MyWebClient;
import org.motechproject.tamafunctional.ivr.Caller;

import java.io.IOException;

public class PatientCallService {
    private MyWebClient webClient;

    public PatientCallService(MyWebClient webClient) {
        this.webClient = webClient;
    }

    public void takenPill(String phoneNumber, String pinNumber) {
        Caller caller = new Caller(phoneNumber, webClient);
        caller.call();
        caller.enter(pinNumber);
        caller.enter("1");
        caller.hangup();
    }

    public void takenPill(String phoneNumber, String pinNumber, int numberOfTimesInLastWeek) throws IOException {
        Caller caller = new Caller(phoneNumber, webClient);
        caller.receiveCall();
        caller.enter(pinNumber);
        caller.enter(Integer.toString(numberOfTimesInLastWeek));
        caller.hangup();
    }
}
