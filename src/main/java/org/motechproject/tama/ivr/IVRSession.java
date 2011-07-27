package org.motechproject.tama.ivr;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class IVRSession {
    private HttpSession session;

    public IVRSession(HttpSession session) {
        this.session = session;
    }

    public IVRCallState getState() {
        return (IVRCallState) session.getAttribute(IVRCallAttribute.CALL_STATE);
    }

    public void setState(IVRCallState callState) {
        session.setAttribute(IVRCallAttribute.CALL_STATE, callState);
    }

    public String get(String name) {
        return (String) session.getAttribute(name);
    }

    public Integer getInt(String name) {
        return (Integer) session.getAttribute(name);
    }

    public String getPatientId() {
        return (String) session.getAttribute(IVRCallAttribute.PATIENT_DOC_ID);
    }

    public void set(String key, Object value) {
        session.setAttribute(key, value);
    }

    public void renew(HttpServletRequest request) {
        session.invalidate();
        session = request.getSession();
    }

    public boolean isAuthentication() {
        return getState().isCollectPin();
    }

    public boolean isDoseResponse() {
        return getState().isCollectDoseResponse();
    }


}
