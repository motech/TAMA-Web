package org.motechproject.tama.reporting.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class ReportingProperties {

    private String reportingHost;
    private String reportingPort;

    @Autowired
    public ReportingProperties(@Value("#{tamaReportsProperties['tama.reports.host']}") String reportingHost, @Value("#{tamaReportsProperties['tama.reports.port']}") String reportingPort) {
        this.reportingHost = reportingHost;
        this.reportingPort = reportingPort;
    }

    public String reportingURL() {
        return format("http://%s:%s/", reportingHost, reportingPort);
    }
}
