package org.motechproject.tama.eventlogging;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.tama.eventlogging.service.EventLoggingService;
import org.motechproject.tama.ivr.IVRCallEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Observable;
import java.util.Observer;

@Component
public class EventLogger implements Observer{
	
	private EventService eventService;
	private EventLoggingService eventLoggingService;
	
	@Autowired
	public EventLogger(EventService eventService, EventLoggingService eventLoggingService) {
		this.eventService = eventService;
		this.eventLoggingService = eventLoggingService;
		eventService.subscribeEvent(this);
	}
	
	@Override
	public void update(Observable o, Object objEvent) {
		if (objEvent instanceof IVRCallEvent){
			IVRCallEvent event = (IVRCallEvent) objEvent;
			eventLoggingService.create("", event.getExternalID(), event.getCallType(), event.getEventName().toString(), "", event.getDateTime(), event.getData());
		}
	}
}
