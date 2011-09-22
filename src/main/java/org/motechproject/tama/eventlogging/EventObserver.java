package org.motechproject.tama.eventlogging;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.IVRCallEvent;
import org.motechproject.tama.eventlogging.service.CallLogService;
import org.motechproject.tama.util.TamaSessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

@Component
public class EventObserver implements Observer {

    private CallLogService callLogService;

    @Autowired
    public EventObserver(EventService eventService, CallLogService callLogService) {
        this.callLogService = callLogService;
        eventService.subscribeEvent(this);
    }

    @Override
    public void update(Observable o, Object objEvent) {
        if (objEvent instanceof IVRCallEvent) {
            callLogService.create((IVRCallEvent) objEvent);
        }
    }
}
