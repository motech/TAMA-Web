package org.motechproject.tama.ivr.service;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EndOfCallObserver {

    private CallLogService callLogService;

    @Autowired
    public EndOfCallObserver(CallLogService callLogService) {
        this.callLogService = callLogService;
    }

    @MotechListener(subjects = "close_call")
    public void handle(MotechEvent event) {
        callLogService.log((String) event.getParameters().get("call_id"), (String) event.getParameters().get("external_id"));
    }
}
