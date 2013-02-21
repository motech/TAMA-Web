package org.motechproject.tama.reporting.service;


import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;

public class ReportingService {

    protected HttpClientService httpClientService;
    protected ReportingProperties reportingProperties;

    public ReportingService(ReportingProperties reportingProperties, HttpClientService httpClientService) {
        this.reportingProperties = reportingProperties;
        this.httpClientService = httpClientService;
    }

    protected void save(Object requestObject, String path) {
        httpClientService.post(reportingProperties.reportingURL() + path, requestObject);
    }

    protected void update(Object requestObject, String path) {
        httpClientService.put(reportingProperties.reportingURL() + path, requestObject);
    }
}
