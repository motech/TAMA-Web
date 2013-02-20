package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.SMSLogRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SMSReportingService extends ReportingService<SMSLogRequest> {

    public static final String PATH_TO_SMS_LOG = "smsLog";

    @Autowired
    public SMSReportingService(ReportingProperties reportingProperties, HttpClientService httpClientService) {
        super(reportingProperties, httpClientService);
    }

    public void save(SMSLogRequest request) {
        super.save(request, PATH_TO_SMS_LOG);
    }
}
