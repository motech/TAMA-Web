package org.motechproject.tama.ivr;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IVRRequest {
    private String sid;
    private String cid;
    private String event;
    private String data;
    private String tamaData;

    public IVRRequest() {
    }

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

    public IVR.Event callEvent() {
        return IVR.Event.keyOf(this.event);
    }

    public boolean hasNoData() {
        return StringUtils.isBlank(this.data);
    }

    public String getTamaData() {
        return tamaData;
    }

    public void setTamaData(String tamaData) {
        this.tamaData = tamaData;
    }

    public Map getTamaParams() {
        Map params = new HashMap();
        try {
            JSONObject json = new JSONObject(tamaData);
            Iterator keys = json.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                params.put(key, json.get(key));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return params;
    }
}
