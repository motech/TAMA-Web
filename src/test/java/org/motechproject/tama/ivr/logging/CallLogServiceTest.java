package org.motechproject.tama.ivr.logging;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.motechproject.tama.ivr.logging.mapper.CallLogMapper;
import org.motechproject.tama.ivr.logging.repository.AllCallLogs;
import org.motechproject.tama.ivr.logging.service.CallLogService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallLogServiceTest {

    private CallLogService callLoggingService;

    @Mock
    private AllCallLogs allCallLogs;

    @Mock
    private KookooCallDetailRecordsService kookooCallDetailRecordsService;

    private CallLogMapper callLogMapper;

    @Before
    public void setUp(){
      initMocks(this);
      callLogMapper = new CallLogMapper();
      callLoggingService = new CallLogService(allCallLogs, kookooCallDetailRecordsService, callLogMapper);
    }

    @Test
    public void shouldCreateInboundCallLog() {
        CallDetailRecord callDetailRecord = CallDetailRecord.newIncomingCallRecord("phoneNumber");
        callDetailRecord.setCallDirection(IVRRequest.CallDirection.Inbound);
        KookooCallDetailRecord kookooCallDetailRecord = new KookooCallDetailRecord(callDetailRecord);

        when(kookooCallDetailRecordsService.get("callId")).thenReturn(kookooCallDetailRecord);
        callLoggingService.log("callId", "patientDocId");

        verify(allCallLogs).add(Matchers.<CallLog>any());
    }

    @Test
    public void shouldReturnAllCallLogs() {
        callLoggingService.getAll();
        verify(allCallLogs).getAll();
    }

}