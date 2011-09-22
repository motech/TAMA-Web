package org.motechproject.tama.eventlogging;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.IVRCallEvent;
import org.motechproject.tama.eventlogging.service.EventLoggingService;
import org.motechproject.tama.util.TamaSessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

@Component
public class EventLogger implements Observer {

    public static final String CALL_TYPE_SYMPTOM_REPORTING = "Symptom Reporting";
    public static final String CALL_TYPE_PILL_REMINDER = "Pill Reminder";

    private EventLoggingService eventLoggingService;

    @Autowired
    public EventLogger(EventService eventService, EventLoggingService eventLoggingService) {
        this.eventLoggingService = eventLoggingService;
        eventService.subscribeEvent(this);
    }

    @Override
    public void update(Observable o, Object objEvent) {
        if (objEvent instanceof IVRCallEvent) {
            IVRCallEvent event = (IVRCallEvent) objEvent;
            Map<String, String> requestParams = event.getRequestParams();
            String isSymptomsReporting = requestParams.get(TamaSessionUtil.TamaSessionAttribute.SYMPTOMS_REPORTING_PARAM);
            String callType = "true".equals(isSymptomsReporting) ? CALL_TYPE_SYMPTOM_REPORTING : CALL_TYPE_PILL_REMINDER;
            eventLoggingService.create(event.getCallId(), event.getExternalID(), callType, event.getCallEvent(), "", event.getDateTime(), event.getData());
        }
    }
}
