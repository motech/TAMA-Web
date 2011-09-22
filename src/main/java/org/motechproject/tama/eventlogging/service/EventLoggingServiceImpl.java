package org.motechproject.tama.eventlogging.service;

import org.joda.time.DateTime;
import org.motechproject.tama.eventlogging.repository.AllCallDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EventLoggingServiceImpl{// implements EventLoggingService {
    private final AllCallDetails allCallDetails;

    @Autowired
    public EventLoggingServiceImpl(AllCallDetails allCallDetails) {
        this.allCallDetails = allCallDetails;
    }

    //@Override
    public void create(String callId, String externalId, String logType, String name, String description, DateTime dateTime, Map<String, String> data) {
//        CallDetail callDetail = allCallDetails.getByCallId(callId);
//        if (callDetail == null) return;mvn jetty:rin
//        callDetail.add(new CallEvent(callId, externalId, logType, name, description, dateTime, data));
//        allCallDetails.update(callDetail);
    }
}