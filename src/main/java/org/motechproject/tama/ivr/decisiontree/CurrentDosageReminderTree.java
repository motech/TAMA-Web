package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.ThreadLocalContext;
import org.motechproject.tama.web.command.*;
import org.springframework.aop.target.ThreadLocalTargetSource;
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
    private ThreadLocalTargetSource threadLocalTargetSource;

    protected Node createRootNode() {
        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(messageForMedicines),
                        new MenuAudioPrompt().setName(IVRMessage.PILL_REMINDER_RESPONSE_MENU))
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
                                                .setTransitions(jumpToPreviousDosageTree()))
                        },
                        {"2", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(updateAdherenceCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(pillsDelayWarning),
                                                        new MenuAudioPrompt().setCommand(messageFromPreviousDosage))
                                                .setTransitions(jumpToPreviousDosageTree()))
                        },
                        {"3", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(stopTodaysRemindersCommand, updateAdherenceCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(messageForMissedPillFeedbackCommand),
                                                        new MenuAudioPrompt().setName(IVRMessage.DOSE_CANNOT_BE_TAKEN_MENU))
                                                .setTransitions(new Object[][]{
                                                        {"2", new Transition()
                                                                .setDestinationNode(new Node()
                                                                        .setTreeCommands(recordDeclinedDosageReasonCommand)
                                                                        .setPrompts(
                                                                                new AudioPrompt().setName(IVRMessage.PLEASE_CARRY_SMALL_BOX),
                                                                                new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageCapturedCommand),
                                                                                new MenuAudioPrompt().setCommand(messageFromPreviousDosage)
                                                                        )
                                                                        .setTransitions(jumpToPreviousDosageTree()))
                                                        },
                                                        {"3", new Transition()
                                                                .setDestinationNode(new Node()
                                                                        .setTreeCommands(recordDeclinedDosageReasonCommand)
                                                                        .setPrompts(
                                                                                new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageCapturedCommand),
                                                                                new MenuAudioPrompt().setCommand(messageFromPreviousDosage)
                                                                        )
                                                                        .setTransitions(jumpToPreviousDosageTree()))
                                                        }
                                                }))
                        }
                });
    }

    private Map<String, Transition> jumpToPreviousDosageTree() {
        ThreadLocalContext threadLocalContext = (ThreadLocalContext) threadLocalTargetSource.getTarget();
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(threadLocalContext.getIvrContext());
        if (pillRegimenSnapshot.isPreviousDosageCaptured()) return new HashMap<String, Transition>();

        return previousDosageReminderTree.getTree().getRootNode().getTransitions();
    }

}
