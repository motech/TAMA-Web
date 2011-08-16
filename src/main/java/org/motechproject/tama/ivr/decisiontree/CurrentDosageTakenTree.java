package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.*;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.ThreadLocalContext;
import org.motechproject.tama.web.command.MessageForAdherenceWhenPreviousDosageCapturedCommand;
import org.motechproject.tama.web.command.MessageFromPreviousDosage;
import org.motechproject.tama.web.command.NextCallDetails;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CurrentDosageTakenTree extends TamaDecisionTree {

    @Autowired
    private NextCallDetails nextCallDetails;

    @Autowired
    private ThreadLocalTargetSource threadLocalTargetSource;

    @Autowired
    private MessageFromPreviousDosage messageFromPreviousDosage;

    @Autowired
    private MessageForAdherenceWhenPreviousDosageCapturedCommand messageForAdherenceWhenPreviousDosageCapturedCommand;

    @Autowired
    private PreviousDosageReminderTree previousDosageReminderTree;

    @Override
    protected Node createRootNode() {
        return Node.newBuilder()
                .setPrompts(Arrays.<Prompt>asList(
                        new AudioPrompt().setCommand(nextCallDetails)))
                .setTransitions(jumpToPreviousDosageTree())
                .build();
    }

    private Map<String, Transition> jumpToPreviousDosageTree() {
        ThreadLocalContext threadLocalContext = (ThreadLocalContext) threadLocalTargetSource.getTarget();
        PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(threadLocalContext.getIvrContext());
        if (pillRegimenSnapshot.isPreviousDosageCaptured()) return new HashMap<String, Transition>();

        return previousDosageReminderTree.getTree().getRootNode().getTransitions();
    }
}
