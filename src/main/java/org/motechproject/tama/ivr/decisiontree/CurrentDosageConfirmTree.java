package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.*;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CurrentDosageConfirmTree extends TamaDecisionTree {
    @Autowired
    private MessageOnPillTakenDuringIncomingCall messageOnPillTakenDuringIncomingCall;
    @Autowired
    private MessageForMedicines messageForMedicines;
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
    private ThreadLocalTargetSource threadLocalTargetSource;

    protected Node createRootNode() {
        return Node.newBuilder()
                .setPrompts(Arrays.asList(
                        new AudioPrompt().setCommand(messageForMedicines),
                        new MenuAudioPrompt().setName(IVRMessage.PILL_CONFIRM_CALL_MENU)))
                .setTransitions(new Object[][]{
                        {"1", Transition.newBuilder()
                                .setDestinationNode(
                                        Node.newBuilder()
                                                .setTreeCommands(stopTodaysRemindersCommand, updateAdherenceCommand)
                                                .setPrompts(Arrays.<Prompt>asList(
                                                        new AudioPrompt().setCommand(messageOnPillTakenDuringIncomingCall),
                                                        new MenuAudioPrompt().setCommand(messageFromPreviousDosage),
                                                        new AudioPrompt().setCommand(messageForAdherenceWhenPreviousDosageCapturedCommand))
                                                )
                                                .setTransitions(jumpToPreviousDosageTree())
                                                .build())
                                .build()
                        }
                })
                .build();
    }

    private Map<String, Transition> jumpToPreviousDosageTree() {
        ThreadLocalContext threadLocalContext = (ThreadLocalContext) threadLocalTargetSource.getTarget();
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(threadLocalContext.getIvrContext());
        if (pillRegimenSnapshot.isPreviousDosageCaptured()) return new HashMap<String, Transition>();

        return previousDosageReminderTree.getTree().getRootNode().getTransitions();
    }

}

