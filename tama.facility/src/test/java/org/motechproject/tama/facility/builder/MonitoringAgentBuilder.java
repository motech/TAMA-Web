package org.motechproject.tama.facility.builder;

import java.util.Calendar;
import java.util.Date;

import org.motechproject.tama.facility.domain.MonitoringAgent;

public class MonitoringAgentBuilder {

	private MonitoringAgent monitoringAgent = new MonitoringAgent();
	
	public static MonitoringAgentBuilder startRecording() {
        return new MonitoringAgentBuilder();
    }
	
    public MonitoringAgentBuilder withName(String name) {
    	monitoringAgent.setName(name);
        return this;
    }
    
    public MonitoringAgentBuilder withContactNumber(String contactNumber) {
    	monitoringAgent.setContactNumber(contactNumber);
        return this;
    }

    public MonitoringAgent build() {
        return this.monitoringAgent;
    }
    
    public MonitoringAgentBuilder withDefaults() {
        String validContactNumber = "1234567890";
        Date time = Calendar.getInstance().getTime();
        String name = "testName" + time.getTime();
        return MonitoringAgentBuilder.startRecording().
                withName(name).
                withContactNumber(validContactNumber);
    }
}
