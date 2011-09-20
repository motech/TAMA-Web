package org.motechproject.tama.eventlogging.service;

import org.joda.time.DateTime;

import java.util.Map;

public interface EventLoggingService {
    void create(String sessionId, String externalId, String logType, String name, String description, DateTime dateTime, Map<String, String> data);
}