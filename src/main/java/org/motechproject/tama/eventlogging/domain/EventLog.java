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

    private String kookooSid;

    private String logType;
    private String name;
    private String description;
    private DateTime dateTime;
    private Map<String, String> data;
    public EventLog() {
    }

    public EventLog(String kookooSid, String patientDocId, String logType, String name, String description, DateTime dateTime, Map<String, String> data) {
        this.patientDocId = patientDocId;
        this.logType = logType;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.data = data;
    }

    public String getKookooSid() {
        return kookooSid;
    }

    public void setKookooSid(String kookooSid) {
        this.kookooSid = kookooSid;
    }

    private String patientDocId;

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