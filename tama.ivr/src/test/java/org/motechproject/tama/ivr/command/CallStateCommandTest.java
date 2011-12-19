package org.motechproject.tama.ivr.command;

import org.junit.Test;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CallStateCommandTest {
    @Test
    public void updateIVRContextOnExecute() {
        TAMAIVRContextFactory tamaivrContextFactory = mock(TAMAIVRContextFactory.class);
        TAMAIVRContextForTest context = new TAMAIVRContextForTest();
        KooKooIVRContext kooKooIVRContext = any(KooKooIVRContext.class);
        when(tamaivrContextFactory.create(kooKooIVRContext)).thenReturn(context);
        context.callState(CallState.AUTHENTICATED);
        CallStateCommand callStateCommand = new CallStateCommand(CallState.ALL_TREES_COMPLETED, tamaivrContextFactory);
        callStateCommand.execute(kooKooIVRContext);
        assertEquals(CallState.ALL_TREES_COMPLETED, context.callState());
    }
}
