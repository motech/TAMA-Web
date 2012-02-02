package org.motechproject.tama.symptomreporting.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.symptomreporting.service.SymptomReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EndOfCallListener {

    private SymptomReportingService symptomReportingService;

    @Autowired
    public EndOfCallListener(SymptomReportingService symptomReportingService) {
        this.symptomReportingService = symptomReportingService;
    }

    @MotechListener(subjects = "close_call")
    public void handle(MotechEvent event) {
        String callId = (String) event.getParameters().get("call_id");
        String patientDocId = (String) event.getParameters().get("external_id");
        symptomReportingService.notifyCliniciansIfCallMissed(callId, patientDocId);
    }
}