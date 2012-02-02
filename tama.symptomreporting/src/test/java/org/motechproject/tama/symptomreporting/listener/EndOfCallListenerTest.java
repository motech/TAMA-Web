package org.motechproject.tama.symptomreporting.listener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.symptomreporting.service.SymptomReportingService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class EndOfCallListenerTest {

    @Mock
    SymptomReportingService symptomReportingService;

    EndOfCallListener listener;

    @Before
    public void setUp(){
        initMocks(this);
        listener = new EndOfCallListener(symptomReportingService);
    }

    @Test
    public void should(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("call_id", "callId");
        map.put("external_id", "patientDocId");
        MotechEvent motechEvent = new MotechEvent("close_call", map);

        listener.handle(motechEvent);

        verify(symptomReportingService).notifyCliniciansIfCallMissed("callId", "patientDocId");
    }

}