package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.web.command.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CurrentDosageReminderTree extends TamaDecisionTree {
    @Autowired
    private MessageOnPillTaken messageOnPillTaken;
    @Autowired
    private MessageForMedicines messageForMedicines;
    @Autowired
    private PillsDelayWarning pillsDelayWarning;
    @Autowired
    private RecordDeclinedDosageReasonCommand recordDeclinedDosageReasonCommand;
    @Qualifier("stopTodaysRemindersCommand")
    @Autowired
    private StopTodaysRemindersCommand stopTodaysRemindersCommand;
    @Qualifier("updateAdherenceCommand")
    @Autowired
    private UpdateAdherenceCommand updateAdherenceCommand;
    @Autowired
    private PreviousDosageReminderTree previousDosageReminderTree;
    @Autowired
    private MessageFromPreviousDosage messageFromPreviousDosage;
    @Autowired
    private MessageForAdherenceWhenPreviousDosageCapturedCommand messageForAdherenceWhenPreviousDosageCapturedCommand;
    @Autowired
    private MessageForMissedPillFeedbackCommand messageForMissedPillFeedbackCommand;
    @Autowired
    private SymptomReportingTransitionsUtility symptomReportingTransitionsUtility;
    
    protected Node createRootNode(IVRContext ivrContext) {
        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(messageForMedicines),
                        new MenuAudioPrompt().setName(TamaIVRMessage.PILL_REMINDER_RESPONSE_MENU))
                .setTransitions(new Object[][]{
                        {"1", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(stopTodaysRemindersCommand, updateAdherenceCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(messageOnPillTaken),
                                                        new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageCapturedCommand),
                                                        new MenuAudioPrompt().setCommand(messageFromPreviousDosage)
                                                )
                                                .setTransitions(jumpToPreviousDosageTree(ivrContext)))
                        },
                        {"2", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(updateAdherenceCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(pillsDelayWarning),
                                                        new MenuAudioPrompt().setCommand(messageFromPreviousDosage))
                                                .setTransitions(jumpToPreviousDosageTree(ivrContext)))
                        },
                        {"3", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(stopTodaysRemindersCommand, updateAdherenceCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(messageForMissedPillFeedbackCommand),
                                                        new MenuAudioPrompt().setName(TamaIVRMessage.DOSE_CANNOT_BE_TAKEN_MENU))
                                                .setTransitions(new Object[][]{
                                                		{"1", symptomReportingTransitionsUtility.newInstance() },
                                                        {"2", new Transition()
                                                                .setDestinationNode(new Node()
                                                                        .setTreeCommands(recordDeclinedDosageReasonCommand)
                                                                        .setPrompts(
                                                                                new AudioPrompt().setName(TamaIVRMessage.PLEASE_CARRY_SMALL_BOX),
                                                                                new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageCapturedCommand),
                                                                                new MenuAudioPrompt().setCommand(messageFromPreviousDosage)
                                                                        )
                                                                        .setTransitions(jumpToPreviousDosageTree(ivrContext)))
                                                        },
                                                        {"3", new Transition()
                                                                .setDestinationNode(new Node()
                                                                        .setTreeCommands(recordDeclinedDosageReasonCommand)
                                                                        .setPrompts(
                                                                                new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageCapturedCommand),
                                                                                new MenuAudioPrompt().setCommand(messageFromPreviousDosage)
                                                                        )
                                                                        .setTransitions(jumpToPreviousDosageTree(ivrContext)))
                                                        }
                                                }))
                        }
                });
    }

    private Map<String, Transition> jumpToPreviousDosageTree(IVRContext ivrContext) {
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        if (pillRegimenSnapshot.isPreviousDosageCaptured()) return new HashMap<String, Transition>();

        return previousDosageReminderTree.getTree(ivrContext).getRootNode().getTransitions();
    }

}
