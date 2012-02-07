package org.motechproject.tamafunctionalframework.testdataservice;

import org.motechproject.deliverytools.kookoo.QueryParams;
import org.motechproject.tamafunctionalframework.framework.MyWebClient;
import org.motechproject.tamafunctionalframework.framework.TamaUrl;
import org.motechproject.tamafunctionalframework.ivr.Caller;

import java.io.IOException;

public class PatientCallService {
    private MyWebClient webClient;
    private int waitForIncomingCallInSeconds;

    public PatientCallService(MyWebClient webClient, int waitForIncomingCallInSeconds) {
        this.webClient = webClient;
        this.waitForIncomingCallInSeconds = waitForIncomingCallInSeconds;
    }

    //Combining these two takenPill methods would be misleading as enter("1") doesn't mean I have taken the pill "once" but just that it is the confirmation to dtmf menu.
    // In other words this is for daily pill reminder
    public void takenPill(String pinNumber) throws IOException {
        Caller caller = Caller.receiveCall(webClient, waitForIncomingCallInSeconds);
        caller.enter(pinNumber);
        caller.enter("1");
        caller.hangup();
    }

    public void takenPill(String pinNumber, int numberOfTimesInLastWeek) throws IOException {
        Caller caller = Caller.receiveCall(webClient, numberOfTimesInLastWeek);
        caller.enter(pinNumber);
        caller.enter(Integer.toString(numberOfTimesInLastWeek));
        caller.hangup();
    }

    public boolean gotCall() {
        return Caller.gotCall(webClient, waitForIncomingCallInSeconds);
    }

    public void listenToOutbox(String pinNumber) throws IOException {
        Caller caller = Caller.receiveCall(webClient, 0);
        caller.enter(pinNumber);
        caller.hangup();
    }

    public void clearLastCall() {
        webClient.getWebResponse(TamaUrl.baseFor("motech-delivery-tools/outbound/clear"), new QueryParams());
    }
}
