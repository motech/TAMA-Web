package org.motechproject.tama.ivr.service;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class EndOfCallObserver {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private CallLogService callLogService;
    private List<Subscriber> subscribers = new ArrayList<Subscriber>();

    @Autowired
    public EndOfCallObserver(CallLogService callLogService) {
        this.callLogService = callLogService;
    }

    @MotechListener(subjects = "close_call")
    public void handle(MotechEvent event) {
        try {
            String callDetailRecordId = (String) event.getParameters().get("call_id");
            String patientDocId = (String) event.getParameters().get("external_id");
            callLogService.log(callDetailRecordId, patientDocId);
            notifySubscribers(callDetailRecordId, patientDocId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e.getStackTrace());
        }
    }

    private void notifySubscribers(String callLogDocId, String patientDocId) {
        for (Subscriber subscriber : subscribers) {
            subscriber.handle(Arrays.<Object>asList(callLogDocId, patientDocId));
        }
    }

    public void registerSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }
}
