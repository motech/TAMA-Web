package org.motechproject.tamacallflow.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tamacallflow.ivr.CallState;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.command.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CurrentDosageConfirmTree extends TamaDecisionTree {
    @Autowired
    private MessageOnPillTakenDuringIncomingCall messageOnPillTakenDuringIncomingCall;
    @Autowired
    private MessageForMedicinesDuringIncomingCall messageForMedicinesDuringIncomingCall;
    @Autowired
    private StopTodaysRemindersCommand stopTodaysRemindersCommand;
    @Autowired
    private UpdateAdherenceCommand updateAdherenceCommand;
    @Autowired
    private AdherenceMessageWhenPreviousDosageCapturedCommand adherenceWhenPreviousDosageCapturedCommand;
    @Autowired
    private SymptomAndOutboxMenuCommand symptomAndOutboxMenuCommand;

    protected Node createRootNode() {
        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(messageForMedicinesDuringIncomingCall),
                        new MenuAudioPrompt().setName(TamaIVRMessage.DOSE_TAKEN_MENU_OPTION),
                        new MenuAudioPrompt().setCommand(symptomAndOutboxMenuCommand),
                        new MenuAudioPrompt().setName(TamaIVRMessage.HEALTH_TIPS_MENU_OPTION))
                .setTransitions(new Object[][]{
                        {"1", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(stopTodaysRemindersCommand, updateAdherenceCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(messageOnPillTakenDuringIncomingCall),
                                                        new AudioPrompt().setCommand(adherenceWhenPreviousDosageCapturedCommand)
                                                ))
                        },
                        {"2", TAMATransitionFactory.createCallStateTransition(CallState.SYMPTOM_REPORTING)},
                        {"3", TAMATransitionFactory.createCallStateTransition(CallState.OUTBOX)},
                        {"5", TAMATransitionFactory.createCallStateTransition(CallState.HEALTH_TIPS)}
                });
    }

}

