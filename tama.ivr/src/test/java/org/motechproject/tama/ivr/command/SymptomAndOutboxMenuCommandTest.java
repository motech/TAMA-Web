package org.motechproject.tama.ivr.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.OutboxModuleStratergy;
import org.motechproject.tama.ivr.context.TAMAIVRContext;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomAndOutboxMenuCommandTest {
    @Mock
    private OutboxModuleStratergy outboxModuleStratergy;
    @Mock
    private TAMAIVRContext tamaivrContext;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldReturnOutboxAndSymptomsReportingMenuOptions() {
        when(outboxModuleStratergy.getNumberPendingMessages(Matchers.<String>any())).thenReturn(4);
        SymptomAndOutboxMenuCommand symptomAndOutboxMenuCommand = new SymptomAndOutboxMenuCommand();
        symptomAndOutboxMenuCommand.registerOutboxModule(outboxModuleStratergy);
        String[] messages = symptomAndOutboxMenuCommand.executeCommand(tamaivrContext);
        assertEquals(2, messages.length);
        assertEquals(TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION, messages[0]);
        assertEquals(TamaIVRMessage.OUTBOX_MENU_OPTION, messages[1]);
    }

    @Test
    public void shouldReturnOutboxMenuOptionOnlyIfThereArePendingOutboxMessages() {
        when(outboxModuleStratergy.getNumberPendingMessages(Matchers.<String>any())).thenReturn(0);
        SymptomAndOutboxMenuCommand symptomAndOutboxMenuCommand = new SymptomAndOutboxMenuCommand();
        symptomAndOutboxMenuCommand.registerOutboxModule(outboxModuleStratergy);
        String[] messages = symptomAndOutboxMenuCommand.executeCommand(tamaivrContext);
        assertEquals(1, messages.length);
        assertEquals(TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION, messages[0]);
    }
}
