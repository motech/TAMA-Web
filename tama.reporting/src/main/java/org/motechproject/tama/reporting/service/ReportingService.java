package org.motechproject.tama.reporting.service;


import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;

public class ReportingService<RequestObject> {

    protected HttpClientService httpClientService;
    protected ReportingProperties reportingProperties;

    public ReportingService(ReportingProperties reportingProperties, HttpClientService httpClientService) {
        this.reportingProperties = reportingProperties;
        this.httpClientService = httpClientService;
    }

    protected void save(RequestObject requestObject, String path) {
        httpClientService.post(reportingProperties.reportingURL() + path, requestObject);
    }

    protected void update(RequestObject requestObject, String path) {
        httpClientService.post(reportingProperties.reportingURL() + path + "/update", requestObject);
    }
}
