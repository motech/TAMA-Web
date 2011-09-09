package org.motechproject.tama.ivr.action.event;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.tama.eventlogging.EventLogConstants;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRCallEvent;
import org.motechproject.tama.ivr.IVREvent;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.BaseAction;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseEventAction extends BaseAction{
    @Autowired
	protected EventService eventService;
    
    Map<String, String> eventData = new HashMap<String, String>();

	abstract public IVREvent getCallEventName();
 
	protected void publishIVREvent(IVREvent name, String externalId, String callTypeName, IVRRequest.CallDirection callDirection, String inputData, String responseXML) {
        addEventLogData(EventLogConstants.RESPONSE_XML, responseXML);
        addEventLogData(EventLogConstants.CALL_DIRECTION, callDirection.toString());
		eventService.publishEvent(new IVRCallEvent(name, externalId, callTypeName, DateUtil.now(), eventData));
	}

	public String handleInternal(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
		String responseXML = handle(ivrRequest, request, response);
		IVRSession ivrSession = getIVRSession(request);
		publishIVREvent(getCallEventName(), ivrSession.getPatientId(), getCallTypeName(request), ivrRequest.getCallDirection(), ivrRequest.getData(), responseXML);
		postHandle(ivrRequest, request, response);
		return responseXML;
	}
	
    public void postHandle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return;
    }

	private String getCallTypeName(HttpServletRequest request) {
		return "true".equals(request.getParameter(IVRCallAttribute.SYMPTOMS_REPORTING_PARAM))?"Symptom Reporting":"Pill Reminder";
	}
	
	protected void addEventLogData(String key, String value){
        eventData.put(key, value);
	}
}
