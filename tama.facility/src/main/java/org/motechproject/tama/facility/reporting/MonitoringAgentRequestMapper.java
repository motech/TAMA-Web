package org.motechproject.tama.facility.reporting;

import org.motechproject.tama.facility.domain.MonitoringAgent;
import org.motechproject.tama.reports.contract.MonitoringAgentRequest;

public class MonitoringAgentRequestMapper {
	
	private MonitoringAgent monitoringAgent;

	public MonitoringAgentRequestMapper(MonitoringAgent monitoringAgent) {
		this.monitoringAgent = monitoringAgent;
	}
	
	public MonitoringAgentRequest map(){
		MonitoringAgentRequest request = new MonitoringAgentRequest();
		request.setMonitoringAgentId(monitoringAgent.getId());
		request.setName(monitoringAgent.getName());
		request.setContactNumber(monitoringAgent.getContactNumber());
		return request;
		
	}
	

}
