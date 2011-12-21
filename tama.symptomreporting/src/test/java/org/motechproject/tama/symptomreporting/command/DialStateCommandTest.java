package org.motechproject.tama.symptomreporting.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.util.Cookies;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DialStateCommandTest {

    @Mock
    private Cookies cookies;
    @Mock
    private KooKooIVRContext kooKooIVRContext;

    @Before
    public void setUp() {
        initMocks(this);
        when(kooKooIVRContext.cookies()).thenReturn(cookies);
    }

    @Test
    public void shouldStartCall() {
        DialStateCommand dialStateCommand = new DialStateCommand();
        dialStateCommand.execute(kooKooIVRContext);

        verify(cookies).add(TAMAIVRContext.SWITCH_TO_DIAL_STATE, "true");
    }
}