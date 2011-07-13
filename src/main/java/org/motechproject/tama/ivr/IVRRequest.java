package org.motechproject.tama.ivr;

import org.motechproject.tama.ivr.action.IVRAction;

public class IVRRequest {
    private String sid;
    private String cid;
    private String event;
    private String data;

    public IVRRequest(String sid, String cid, String event, String data) {
        this.sid = sid;
        this.cid = cid;
        this.event = event;
        this.data = data;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public IVRAction getAction() {
        IVR.Event event = IVR.Event.valueOf(this.event);
        return event.getAction();
    }

}
