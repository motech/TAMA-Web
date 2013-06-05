package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.WeeklyAdherenceLogRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class WeeklyPatientReportingService extends ReportingService {

    public static final String PATH_TO_WEEKLY = "/weekly";

    @Autowired
    public WeeklyPatientReportingService(HttpClientService httpClientService, ReportingProperties reportingProperties) {
        super(reportingProperties, httpClientService);
    }


    public void save(WeeklyAdherenceLogRequest weeklyAdherenceLogRequest) {
        super.save(weeklyAdherenceLogRequest, PATH_TO_WEEKLY);

    }

    public void update(WeeklyAdherenceLogRequest weeklyAdherenceLogRequest) {
        super.update(weeklyAdherenceLogRequest, PATH_TO_WEEKLY);
    }
}
