package org.motechproject.tama.eventlogging.domain;

import org.joda.time.DateTime;
import org.motechproject.ivr.IVRCallEvent;
import org.motechproject.tama.domain.CouchEntity;
import org.motechproject.util.DateUtil;

import java.util.Map;

public class CallDetailUnit extends CouchEntity {

    private String action;

    private DateTime dateTime;

    private Map<String, String> data;

    public CallDetailUnit() {
    }

    public CallDetailUnit(IVRCallEvent ivrCallEvent) {
        this.action = ivrCallEvent.getCallEvent();
        this.data = ivrCallEvent.getData();
        this.dateTime = DateUtil.now();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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