package org.motechproject.tamacallflow.ivr.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomAndOutboxMenuCommandTest {
    @Mock
    private PillReminderService pillReminderService;
    @Mock
    private VoiceOutboxService voiceOutboxService;
    @Mock
    private TAMAIVRContext tamaivrContext;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldReturnOutboxAndSymptomsReportingMenuOptions() {
        when(voiceOutboxService.getNumberPendingMessages(Matchers.<String>any())).thenReturn(4);
        SymptomAndOutboxMenuCommand symptomAndOutboxMenuCommand = new SymptomAndOutboxMenuCommand(voiceOutboxService, pillReminderService);
        String[] messages = symptomAndOutboxMenuCommand.executeCommand(tamaivrContext);
        assertEquals(2, messages.length);
        assertEquals(TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION, messages[0]);
        assertEquals(TamaIVRMessage.OUTBOX_MENU_OPTION, messages[1]);
    }

    @Test
    public void shouldReturnOutboxMenuOptionOnlyIfThereArePendingOutboxMessages() {
        when(voiceOutboxService.getNumberPendingMessages(Matchers.<String>any())).thenReturn(0);
        SymptomAndOutboxMenuCommand symptomAndOutboxMenuCommand = new SymptomAndOutboxMenuCommand(voiceOutboxService, pillReminderService);
        String[] messages = symptomAndOutboxMenuCommand.executeCommand(tamaivrContext);
        assertEquals(1, messages.length);
        assertEquals(TamaIVRMessage.SYMPTOMS_REPORTING_MENU_OPTION, messages[0]);
    }

}
