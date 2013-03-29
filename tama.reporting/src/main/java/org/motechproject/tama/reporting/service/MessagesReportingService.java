package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.MessagesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessagesReportingService extends ReportingService {

    public static final String PATH_TO_MESSAGES = "messages";

    @Autowired
    public MessagesReportingService(HttpClientService httpClientService, ReportingProperties reportingProperties) {
        super(reportingProperties, httpClientService);
    }

    public void save(MessagesRequest messagesRequest) {
        super.save(messagesRequest, PATH_TO_MESSAGES);
    }
}
