package org.motechproject.tama.eventlogging.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.eventlogging.repository.AllCallDetails;

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
        /*Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, String> data = new HashMap<String, String>();
        CallEvent ivrCallEvent = new CallEvent("callId", "callEvent", "patientDocId", requestParams, DateTime.now(), data);

        when(allCallDetails.getByCallId("callId")).thenReturn(null);

        eventLoggingService.create(ivrCall1Event);

        verify(allCallDetails, times(1)).add(any(CallLog.class));*/
    }

    @Test
    public void shouldAddTheEventToTheCallDetailDocument() {
        /*Map<String, String> requestParams = new HashMap<String, String>();
        Map<String, String> data = new HashMap<String, String>();
        IVRCallEvent ivrCallEvent = new IVRCallEvent("callId", "callEvent", "patientDocId", requestParams, DateTime.now(), data);

        CallLog callLogForTheEvent = mock(CallLog.class);

        when(allCallDetails.getByCallId("callId")).thenReturn(callLogForTheEvent);
        eventLoggingService.create(ivrCallEvent);

        verify(callLogForTheEvent, times(1)).add(Mockito.<CallDetailUnit>any());
        verify(allCallDetails, times(1)).update(callLogForTheEvent);*/
    }
}
