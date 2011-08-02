package org.motechproject.tama.web.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillTakenCommandTest {

    @Mock
    private IVRContext context;
    @Mock
    private IVRRequest ivrRequest;
    @Mock
    private IVRSession ivrSession;
    @Mock
    private PillReminderService pillReminderService;
    private PillTakenCommand pillTakenCommand;

    @Before
    public void setup() {
        initMocks(this);

        pillTakenCommand = new PillTakenCommand(pillReminderService);
        when(context.ivrSession()).thenReturn(ivrSession);
        when(context.ivrRequest()).thenReturn(ivrRequest);
    }

    @Test
    public void shouldUpdateDosageDateOnPillTaken() {

        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.REGIMEN_ID, "regimenId");
        params.put(PillReminderCall.DOSAGE_ID, "dosageId");
        when(ivrRequest.getTamaParams()).thenReturn(params);

        pillTakenCommand.execute(context);

        verify(pillReminderService).updateDosageTaken("regimenId", "dosageId");
    }
}
