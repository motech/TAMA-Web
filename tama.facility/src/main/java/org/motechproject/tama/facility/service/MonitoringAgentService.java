package org.motechproject.tama.facility.service;

import java.util.List;

import org.motechproject.tama.facility.domain.MonitoringAgent;
import org.motechproject.tama.facility.repository.AllMonitoringAgents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MonitoringAgentService {
	
	private AllMonitoringAgents allMonitoringAgents;

	@Autowired
    public MonitoringAgentService(AllMonitoringAgents allMonitoringAgents){
		this.allMonitoringAgents = allMonitoringAgents;
	}
	
	public List<MonitoringAgent> getAllMonitoringAgents(){
		return allMonitoringAgents.getAll();
	}
}
