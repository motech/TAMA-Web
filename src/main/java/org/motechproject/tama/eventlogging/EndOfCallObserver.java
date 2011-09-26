package org.motechproject.tama.eventlogging;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.tama.eventlogging.service.CallLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Observable;
import java.util.Observer;

@Component
public class EndOfCallObserver implements Observer {

    private CallLogService callLogService;

    @Autowired
    public EndOfCallObserver(EventService eventService, CallLogService callLogService) {
        this.callLogService = callLogService;
        eventService.subscribeEvent(this);
    }

    @Override
    public void update(Observable o, Object objEvent) {
        if (objEvent instanceof CallEvent) {
            //callLogService.create((CallEvent) objEvent);
        }
    }
}
