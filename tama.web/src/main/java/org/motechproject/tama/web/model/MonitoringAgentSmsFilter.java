package org.motechproject.tama.web.model;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.facility.domain.MonitoringAgent;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class MonitoringAgentSmsFilter {

	 private DateFilter dateFilter = new DateFilter();
	 
	 private List<MonitoringAgent> agents;
	 
	 private String externalId;
	 
	 public MonitoringAgentSmsFilter(List<MonitoringAgent> monitoringAgents){
		 MonitoringAgent defaultMonitoringAgent = defaultMonitoringAgent();
	        this.agents = new ArrayList<>(asList(defaultMonitoringAgent));
	        this.agents.addAll(monitoringAgents);
		 
		 /*this.agentNames = new ArrayList<String>();
		 for(MonitoringAgent mAgent:monitoringAgents){
			 agentNames.add(mAgent.getName());
		 }*/
	 }
	 
	 private MonitoringAgent defaultMonitoringAgent() {
		 MonitoringAgent defaultMonitoringAgent = MonitoringAgent.newMonitoringAgent();
	        defaultMonitoringAgent.setName("");
	        return defaultMonitoringAgent;
	    }

	 
	 
	 public String getExternalId() {
	        return externalId;
	    }

	    public void setExternalId(String externalId) {
	        this.externalId = externalId;
	    }

	    public List<MonitoringAgent> getAllMonitoringAgents() {
	        return agents;
	    }
	    @Temporal(TemporalType.DATE)
	    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
	    public LocalDate getStartDate() {
	        return dateFilter.getStartDate();
	    }

	    @Temporal(TemporalType.DATE)
	    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
	    public LocalDate getEndDate() {
	        return dateFilter.getEndDate();
	    }

	    public void setStartDate(LocalDate startDate) {
	        dateFilter.setStartDate(startDate);
	    }

	    public void setEndDate(LocalDate endDate) {
	        dateFilter.setEndDate(endDate);
	    }
	    
	    
	}

