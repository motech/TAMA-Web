package org.motechproject.tama.eventlogging.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.tama.eventlogging.domain.CallDetail;
import org.motechproject.tama.eventlogging.domain.CallEvent;
import org.motechproject.tama.eventlogging.repository.AllCallDetails;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class EventLoggingServiceImplTest {
    private EventLoggingService eventLoggingService;
    @Mock
    private AllCallDetails allCallLogs;

    @Before
    public void setUp(){
      initMocks(this);
      eventLoggingService = new EventLoggingServiceImpl(allCallLogs);
    }

    @Test
    public void shouldAddTheEventToTheCallLogDocument() {
        CallDetail callLogForTheEvent = mock(CallDetail.class);
        String callId = "callId";

        when(allCallLogs.getByCallId(callId)).thenReturn(callLogForTheEvent);
        eventLoggingService.create(callId, null, null, null, null, null, null);

        verify(callLogForTheEvent, times(1)).add(Mockito.<CallEvent>any());
        verify(allCallLogs, times(1)).update(callLogForTheEvent);
    }
}
