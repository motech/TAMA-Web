package org.motechproject.tama.eventlogging;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.motechproject.eventtracking.domain.Event;
import org.motechproject.tama.ivr.IVRCallEvent;
import org.motechproject.util.DateUtil;

public class EventDataBuilder {
	
	private IVRCallEvent callEvent;
	Map<String, String> eventData = new HashMap<String, String>();
	
	public EventDataBuilder(String eventName, String externalId, String callTypeName, DateTime dateTime) {
		callEvent = new IVRCallEvent(eventName, externalId, callTypeName, DateUtil.now(), eventData);
	}

	public EventDataBuilder withCallerId(String callerId) {
		eventData.put(EventLogConstants.CALLER_ID, callerId);
		return this;
	}
	
	public EventDataBuilder withCallDirection(String callDirection) {
		eventData.put(EventLogConstants.CALL_DIRECTION, callDirection);
		return this;
	}
	
	public EventDataBuilder withResponseXML(String responseXML) {
		eventData.put(EventLogConstants.RESPONSE_XML, responseXML);
		return this;
	}
	 
	public Event build() {
		return callEvent;
	}

	public void withData(Map<String, String> eventData) {
		for (String key : eventData.keySet()) {
			this.eventData.put(key, eventData.get(key));
		}
	}

}
