package org.motechproject.tama.eventlogging.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.tama.domain.CouchEntity;

import java.util.Map;

@TypeDiscriminator("doc.documentType == 'EventLog'")
public class EventLog extends CouchEntity {
	@JsonProperty("type")
	private String type = "EventLog";
	@JsonProperty("code")
	String code;

    private String sessionId;
    private String patientDocId;
    private String logType;
    private String name;
    private String description;
    private DateTime dateTime;
    private Map<String, String> data;

    public EventLog() {
    }

    public EventLog(String sessionId, String patientDocId, String logType, String name, String description, DateTime dateTime, Map<String, String> data) {
        this.sessionId = sessionId;
        this.patientDocId = patientDocId;
        this.logType = logType;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.data = data;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPatientDocId() {
        return patientDocId;
    }

    public void setPatientDocId(String patientDocId) {
        this.patientDocId = patientDocId;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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