package org.motechproject.tama.reporting.service;

import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.reports.contract.MonitoringAgentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class MonitoringAgentReportingService extends ReportingService  {

	public static final String PATH_TO_MONITORING_AGENT = "monitoringAgent";
	
	 @Autowired
	    public MonitoringAgentReportingService(HttpClientService httpClientService, ReportingProperties reportingProperties) {
	        super(reportingProperties, httpClientService);
	    }
	 
	 public void save(MonitoringAgentRequest request) {
	        super.save(request, PATH_TO_MONITORING_AGENT);
	    }

	    public void update(MonitoringAgentRequest request) {
	        super.update(request, PATH_TO_MONITORING_AGENT);
	    }
}
