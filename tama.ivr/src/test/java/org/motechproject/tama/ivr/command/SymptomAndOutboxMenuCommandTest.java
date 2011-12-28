package org.motechproject.tama.ivr.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.OutboxModuleStrategy;
import org.motechproject.tama.ivr.context.TAMAIVRContext;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomAndOutboxMenuCommandTest {
    @Mock
    private OutboxModuleStrategy outboxModuleStrategy;
    @Mock
    private TAMAIVRContext tamaivrContext;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldReturnOutboxAndSymptomsReportingMenuOptions() {
        when(outboxModuleStrategy.hasPendingOutboxMessages(Matchers.<String>any())).thenReturn(true);
        SymptomAndOutboxMenuCommand symptomAndOutboxMenuCommand = new SymptomAndOutboxMenuCommand();
        symptomAndOutboxMenuCommand.registerOutboxModule(outboxModuleStrategy);
        String[] messages = symptomAndOutboxMenuCommand.executeCommand(tamaivrContext);
        assertEquals(2, messages.length);
        assertEquals(TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION, messages[0]);
        assertEquals(TamaIVRMessage.OUTBOX_MENU_OPTION, messages[1]);
    }

    @Test
    public void shouldReturnOutboxMenuOptionOnlyIfThereArePendingOutboxMessages() {
        when(outboxModuleStrategy.hasPendingOutboxMessages(Matchers.<String>any())).thenReturn(false);
        SymptomAndOutboxMenuCommand symptomAndOutboxMenuCommand = new SymptomAndOutboxMenuCommand();
        symptomAndOutboxMenuCommand.registerOutboxModule(outboxModuleStrategy);
        String[] messages = symptomAndOutboxMenuCommand.executeCommand(tamaivrContext);
        assertEquals(1, messages.length);
        assertEquals(TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION, messages[0]);
    }
}
