package org.motechproject.tama.symptomreporting.subscriber;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.service.EndOfCallObserver;
import org.motechproject.tama.symptomreporting.service.SymptomReportingService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SendSymptomsSubscriberTest {

    @Mock
    SymptomReportingService symptomReportingService;
    @Mock
    EndOfCallObserver endOfCallObserver;

    SendSymptomsSubscriber listener;

    @Before
    public void setUp() {
        initMocks(this);
        listener = new SendSymptomsSubscriber(symptomReportingService, endOfCallObserver);
    }

    @Test
    @Ignore("Clinician should only be notified of the OTC advice(through SMS) before being contacted.")
    public void should() {
        List<Object> objects = new ArrayList<Object>();
        objects.add("callId");
        objects.add("patientDocId");

        listener.handle(objects);

        verify(symptomReportingService).smsOTCAdviceToAllClinicianWhenDialToClinicianFails("callId", "patientDocId");
    }

}