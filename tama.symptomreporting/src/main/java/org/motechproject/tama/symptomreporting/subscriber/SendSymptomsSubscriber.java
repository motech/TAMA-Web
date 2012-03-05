package org.motechproject.tama.symptomreporting.subscriber;

import org.motechproject.tama.ivr.service.EndOfCallObserver;
import org.motechproject.tama.ivr.service.Subscriber;
import org.motechproject.tama.symptomreporting.service.SymptomReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendSymptomsSubscriber implements Subscriber {

    private SymptomReportingService symptomReportingService;

    @Autowired
    public SendSymptomsSubscriber(SymptomReportingService symptomReportingService, EndOfCallObserver endOfCallObserver) {
        this.symptomReportingService = symptomReportingService;
//        endOfCallObserver.registerSubscriber(this);
    }

    @Override
    public void handle(List<Object> objects) {
        String callDetailRecordId = (String) objects.get(0);
        String patientDocId = (String) objects.get(1);
        symptomReportingService.smsOTCAdviceToAllClinicians(callDetailRecordId, patientDocId);
    }
}