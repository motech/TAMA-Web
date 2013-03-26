package org.motechproject.tama.reporting.service;


import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;

import java.io.Serializable;

public class ReportingService {

    protected HttpClientService httpClientService;
    protected ReportingProperties reportingProperties;

    public ReportingService(ReportingProperties reportingProperties, HttpClientService httpClientService) {
        this.reportingProperties = reportingProperties;
        this.httpClientService = httpClientService;
    }

    protected void save(Object requestObject, String path) {
        Serializable serializableRequest = (Serializable) requestObject;
        httpClientService.post(reportingProperties.reportingURL() + path, serializableRequest);
    }

    protected void update(Object requestObject, String path) {
        Serializable serializableRequest = (Serializable) requestObject;
        httpClientService.put(reportingProperties.reportingURL() + path, serializableRequest);
    }
}
