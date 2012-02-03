package org.motechproject.tama.ivr.service;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class EndOfCallObserver {

    private CallLogService callLogService;

    List<Subscriber> subscribers;

    @Autowired
    public EndOfCallObserver(CallLogService callLogService) {
        this.callLogService = callLogService;
        subscribers = new ArrayList<Subscriber>();
    }

    @MotechListener(subjects = "close_call")
    public void handle(MotechEvent event) {
        String callLogDocId = (String) event.getParameters().get("call_id");
        String patientDocId = (String) event.getParameters().get("external_id");
        callLogService.log(callLogDocId, patientDocId);
        notifySubscribers(callLogDocId, patientDocId);
    }

    private void notifySubscribers(String callLogDocId, String patientDocId) {
        for(Subscriber subscriber : subscribers){
            subscriber.handle(Arrays.<Object>asList(callLogDocId, patientDocId));
        }
    }

    public void registerSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }
}
