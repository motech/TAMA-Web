package org.motechproject.tama.ivr.action.event;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.tama.eventlogging.EventDataBuilder;
import org.motechproject.tama.eventlogging.EventLogConstants;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVREvent;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.action.BaseAction;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseEventAction extends BaseAction{
    @Autowired
	protected EventService eventService;
    
    Map<String, String> eventData = new HashMap<String, String>();

	protected void publishIVREvent(IVREvent name, String externalId, String callTypeName, IVRRequest.CallDirection callDirection, String callerId, String inputData, String responseXML) {
		
		EventDataBuilder builder = new EventDataBuilder(name.toString(), externalId, callTypeName, DateUtil.now());
        builder.withResponseXML(responseXML)
        .withCallDirection(callDirection.toString())
        .withCallerId(callerId)
        .withData(eventData);
		eventService.publishEvent(builder.build());
	}

	public String handleInternal(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
		String responseXML = handle(ivrRequest, request, response);
		IVRSession ivrSession = getIVRSession(request);
        String patientId = ivrSession.isValid() ? ivrSession.getPatientId() : "Unknown";
        publishIVREvent(ivrRequest.callEvent(), patientId, getCallTypeName(request), ivrRequest.getCallDirection(), ivrRequest.getCid(), ivrRequest.getData(), responseXML);
		postHandle(ivrRequest, request, response);
		return responseXML;
	}
	
    public void postHandle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return;
    }

	private String getCallTypeName(HttpServletRequest request) {
		return "true".equals(request.getParameter(IVRCallAttribute.SYMPTOMS_REPORTING_PARAM))?EventLogConstants.CALL_TYPE_SYMPTOM_REPORTING:EventLogConstants.CALL_TYPE_PILL_REMINDER;
	}
	
	protected void addEventLogData(String key, String value){
        eventData.put(key, value);
	}
}
