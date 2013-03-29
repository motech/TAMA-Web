package org.motechproject.tama.reporting.service;

import org.motechproject.tama.reports.contract.MessagesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallLogReportingService {

    private MessagesReportingService messagesReportingService;

    @Autowired
    public CallLogReportingService(MessagesReportingService messagesReportingService) {
        this.messagesReportingService = messagesReportingService;
    }

    public void reportMessages(MessagesRequest request) {
        messagesReportingService.save(request);
    }
}
