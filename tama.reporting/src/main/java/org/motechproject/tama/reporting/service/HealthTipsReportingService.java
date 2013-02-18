package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.HealthTipsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HealthTipsReportingService extends ReportingService<HealthTipsRequest> {

    public static final String PATH_TO_HEALTH_TIPS = "healthTips";

    @Autowired
    public HealthTipsReportingService(HttpClientService httpClientService, ReportingProperties reportingProperties) {
        super(reportingProperties, httpClientService);
    }

    public void save(HealthTipsRequest healthTipsRequest) {
        super.save(healthTipsRequest, PATH_TO_HEALTH_TIPS);
    }
}
