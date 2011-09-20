package org.motechproject.tama.eventlogging.service;

import org.joda.time.DateTime;
import org.motechproject.tama.eventlogging.dao.AllEventLogs;
import org.motechproject.tama.eventlogging.domain.EventLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EventLoggingServiceImpl implements EventLoggingService {
    private AllEventLogs allEventLogs;

    @Autowired
    public EventLoggingServiceImpl(AllEventLogs allEventLogs) {
        this.allEventLogs = allEventLogs;
    }

    @Override
    public void create(String sessionId, String externalId, String logType, String name, String description, DateTime dateTime, Map<String, String> data) {
        EventLog eventLog = new EventLog(sessionId, externalId, logType, name, description, dateTime, data);
        allEventLogs.add(eventLog);
    }
}