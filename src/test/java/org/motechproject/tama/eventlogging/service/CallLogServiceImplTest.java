package org.motechproject.tama.eventlogging.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ivr.IVRCallEvent;
import org.motechproject.tama.eventlogging.domain.CallDetail;
import org.motechproject.tama.eventlogging.domain.CallDetailUnit;
import org.motechproject.tama.eventlogging.repository.AllCallDetails;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallLogServiceImplTest {
    private CallLogService eventLoggingService;
    @Mock
    private AllCallDetails allCallDetails;

    @Before
    public void setUp(){
      initMocks(this);
      eventLoggingService = new CallLogServiceImpl(allCallDetails);
    }

    @Test
    public void shouldCreateCallDetailIfCallLogIsNotPresent() {
        Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, String> data = new HashMap<String, String>();
        IVRCallEvent ivrCallEvent = new IVRCallEvent("callId", "callEvent", "patientDocId", requestParams, DateTime.now(), data);

        when(allCallDetails.getByCallId("callId")).thenReturn(null);

        eventLoggingService.create(ivrCallEvent);

        verify(allCallDetails, times(1)).add(any(CallDetail.class));
    }

    @Test
    public void shouldAddTheEventToTheCallDetailDocument() {
        Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, String> data = new HashMap<String, String>();
        IVRCallEvent ivrCallEvent = new IVRCallEvent("callId", "callEvent", "patientDocId", requestParams, DateTime.now(), data);

        CallDetail callLogForTheEvent = mock(CallDetail.class);

        when(allCallDetails.getByCallId("callId")).thenReturn(callLogForTheEvent);
        eventLoggingService.create(ivrCallEvent);

        verify(callLogForTheEvent, times(1)).add(Mockito.<CallDetailUnit>any());
        verify(allCallDetails, times(1)).update(callLogForTheEvent);
    }
}
