package org.motechproject.tama.ivr.logging;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.kookoo.EndOfCallEvent;
import org.motechproject.tama.ivr.logging.service.CallLogService;
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
    public void update(Observable o, Object event) {
        if (event instanceof EndOfCallEvent) {
            EndOfCallEvent endOfCallEvent = (EndOfCallEvent) event;
            callLogService.log(endOfCallEvent.getCallId(), endOfCallEvent.getReferenceId());
        }
    }
}
