package org.motechproject.tama.web.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.motechproject.tama.facility.domain.MonitoringAgent;
import org.motechproject.tama.facility.repository.AllMonitoringAgents;

public class MonitoringAgentsView {

	private final AllMonitoringAgents monitoringAgents;
	//private final List<MonitoringAgentDto> monitoringAgentDtos ;

	public MonitoringAgentsView(AllMonitoringAgents monitoringAgents) {
		this.monitoringAgents = monitoringAgents;
		//monitoringAgentDtos = new ArrayList<MonitoringAgentDto>();
	}

	public List<MonitoringAgent> getAll() {
		List<MonitoringAgent> allMonitoringAgents = monitoringAgents.getAll();
		for(MonitoringAgent monitoringAgent : allMonitoringAgents){
			monitoringAgents.getClinicNames(monitoringAgent);
		}
		Collections.sort(allMonitoringAgents,new Comparator<MonitoringAgent>() {
					@Override
					public int compare(MonitoringAgent monitoringAgent,MonitoringAgent otherMonitoringAgent) {
						return monitoringAgent.getName().toLowerCase().compareTo(otherMonitoringAgent.getName().toLowerCase());
					}
				});
		return allMonitoringAgents;

		//List<MonitoringAgentDto> allMonitoringAgentDtos = new ArrayList<MonitoringAgentDto>();
		/*for (MonitoringAgent monitoringAgent : allMonitoringAgents) {
			monitoringAgentDtos.add(monitoringAgents
					.getMonitoringAgentDto(monitoringAgent));
		}
		return monitoringAgentDtos;*/
	}
}
