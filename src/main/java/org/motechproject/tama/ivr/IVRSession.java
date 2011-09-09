package org.motechproject.tama.ivr;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;

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

    public Object get(String name) {
        return session.getAttribute(name);
    }

    public Integer getInt(String name) {
        return (Integer) session.getAttribute(name);
    }

    public String getPatientId() {
        return (String) session.getAttribute(IVRCallAttribute.PATIENT_DOC_ID);
    }
    
    public String getPrefferedLanguageCode() {
    	return (String) session.getAttribute(IVRCallAttribute.PREFERRED_LANGUAGE_CODE);
    }

    public boolean isSymptomsReportingCall() {
    	return "true".equals(session.getAttribute(IVRCallAttribute.SYMPTOMS_REPORTING_PARAM));
    }
    
    public PillRegimenResponse getPillRegimen() {
        return (PillRegimenResponse) session.getAttribute(IVRCallAttribute.REGIMEN_FOR_PATIENT);
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

    public void close() {
        if (session != null) session.invalidate();
    }

    public String currentDecisionTreePath() {
        String currentDecisionTreePosition = (String) get(IVRCallAttribute.CURRENT_DECISION_TREE_POSITION);
        return currentDecisionTreePosition == null ? "" : currentDecisionTreePosition;
    }

    public void currentDecisionTreePath(String nextCurrentPosition) {
        set(IVRCallAttribute.CURRENT_DECISION_TREE_POSITION, nextCurrentPosition);
    }


    public DateTime getCallTime() {
        return (DateTime) get(IVRCallAttribute.CALL_TIME);
    }

	
}
