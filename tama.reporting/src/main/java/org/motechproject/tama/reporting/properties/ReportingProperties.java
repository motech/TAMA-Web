package org.motechproject.tama.reporting.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class ReportingProperties {

    private String reportingHost;
    private String reportingPort;
    private String contextPath;

    @Autowired
    public ReportingProperties(@Value("#{tamaReportsProperties['tama.reports.host']}") String reportingHost,
                               @Value("#{tamaReportsProperties['tama.reports.port']}") String reportingPort,
                               @Value("#{tamaReportsProperties['tama.reports.context.path']}") String contextPath) {
        this.reportingHost = reportingHost;
        this.reportingPort = reportingPort;
        this.contextPath = contextPath;
    }

    public String reportingURL() {
        return format("http://%s:%s/%s/", reportingHost, reportingPort, contextPath);
    }
}
