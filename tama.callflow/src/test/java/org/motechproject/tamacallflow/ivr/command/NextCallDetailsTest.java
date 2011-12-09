package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamacallflow.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tamadomain.builder.PillRegimenResponseBuilder;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class NextCallDetailsTest {
    private NextCallDetails nextCallDetails;
    private TAMAIVRContextForTest context;

    @Before
    public void setup() {
        initMocks(this);
        nextCallDetails = new NextCallDetails(null);
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        context = new TAMAIVRContextForTest().pillRegimen(pillRegimenResponse).callStartTime(new DateTime(2010, 10, 10, 16, 0, 0)).preferredLanguage("en");
        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(new LocalDate(2010, 10, 10));
    }

    @Test
    public void shouldReturnLastReminderWarningMessageNonLastReminder() {
        context.callDirection(CallDirection.Inbound);

        String[] messages = nextCallDetails.executeCommand(context);
        assertEquals(7, messages.length);
        assertEquals("010_04_01_nextDoseIs1", messages[0]);
        assertEquals("timeOfDayToday", messages[1]);
        assertEquals("timeOfDayAt", messages[2]);
        assertEquals("Num_010", messages[3]);
        assertEquals("Num_005", messages[4]);
        assertEquals("PM", messages[5]);
        assertEquals("010_04_06_nextDoseIs2", messages[6]);
    }

    @Test
    public void shouldNotReturnNextCallDetailMessagesForRepeatMenu() {
        ArrayList<String> completedTrees = new ArrayList<String>();
        context.addLastCompletedTreeToListOfCompletedTrees(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN);
        completedTrees.add(TAMATreeRegistry.CURRENT_DOSAGE_TAKEN);
        String[] messages = nextCallDetails.executeCommand(context);
        assertEquals(0, messages.length);
    }
}
