package org.motechproject.tama.ivr;

import org.joda.time.DateTime;
import org.motechproject.eventtracking.domain.Event;

import java.util.Map;

public class IVRCallEvent implements Event{
    String eventName;
    String kookooSid;

    String externalID;
    String callType;
    DateTime dateTime;
    Map<String, String> data;
	public IVRCallEvent() {}

	public IVRCallEvent(String kookooSid, String eventName, String externalID, String callType,
                        DateTime dateTime, Map<String, String> data) {
		super();
		this.eventName = eventName;
		this.externalID = externalID;
		this.callType = callType;
		this.dateTime = dateTime;
		this.data = data;
	}

    public String getKookooSid() {
        return kookooSid;
    }

    public void setKookooSid(String kookooSid) {
        this.kookooSid = kookooSid;
    }

	public String getEventName() {
		return eventName;
	}
    public void setEventName(String eventName) {
		this.eventName = eventName;
	}
    public String getExternalID() {
		return externalID;
	}
	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}
	public String getCallType() {
		return callType;
	}
	public void setCallType(String callType) {
		this.callType = callType;
	}
	public DateTime getDateTime() {
		return dateTime;
	}
	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	
	
}
