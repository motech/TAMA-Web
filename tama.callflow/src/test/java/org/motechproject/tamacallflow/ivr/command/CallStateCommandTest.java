package org.motechproject.tamacallflow.ivr.command;

import org.junit.Test;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tamacallflow.ivr.CallState;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamacallflow.ivr.factory.TAMAIVRContextFactory;

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
