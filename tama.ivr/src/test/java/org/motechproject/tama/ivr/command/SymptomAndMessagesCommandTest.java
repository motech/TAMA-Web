package org.motechproject.tama.ivr.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomAndMessagesCommandTest {
    @Mock
    private TAMAIVRContext tamaivrContext;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldReturnOutboxAndSymptomsReportingMenuOptions() {
        SymptomAndMessagesCommand symptomAndMessagesCommand = new SymptomAndMessagesCommand();
        symptomAndMessagesCommand.registerOutboxModule();
        String[] messages = symptomAndMessagesCommand.executeCommand(tamaivrContext);
        assertEquals(2, messages.length);
        assertEquals(TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION, messages[0]);
        assertEquals(TamaIVRMessage.OUTBOX_MENU_OPTION, messages[1]);
    }
}
