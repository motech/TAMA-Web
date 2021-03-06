package org.motechproject.tama.dailypillreminder.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.dailypillreminder.command.AdherenceMessageWhenPreviousDosageCapturedCommand;
import org.motechproject.tama.dailypillreminder.command.MessageForMedicinesDuringIncomingCall;
import org.motechproject.tama.dailypillreminder.command.MessageOnPillTakenDuringIncomingCall;
import org.motechproject.tama.dailypillreminder.command.UpdateAdherenceAsCapturedForCurrentDosageCommand;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.command.IncomingWelcomeMessage;
import org.motechproject.tama.ivr.command.SymptomAndMessagesCommand;
import org.motechproject.tama.ivr.decisiontree.TAMATransitionFactory;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.decisiontree.TamaDecisionTree;
import org.motechproject.tama.ivr.domain.CallState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CurrentDosageConfirmTree extends TamaDecisionTree {

    @Autowired
    private IncomingWelcomeMessage incomingWelcomeMessage;
    @Autowired
    private MessageForMedicinesDuringIncomingCall messageForMedicinesDuringIncomingCall;
    @Autowired
    private MessageOnPillTakenDuringIncomingCall messageOnPillTakenDuringIncomingCall;
    @Autowired
    private UpdateAdherenceAsCapturedForCurrentDosageCommand updateAdherenceAsCapturedCommand;
    @Autowired
    private AdherenceMessageWhenPreviousDosageCapturedCommand adherenceWhenPreviousDosageCapturedCommand;
    @Autowired
    private SymptomAndMessagesCommand symptomAndMessagesCommand;

    @Autowired
    public CurrentDosageConfirmTree(TAMATreeRegistry tamaTreeRegistry) {
        tamaTreeRegistry.register(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM, this);
    }

    protected Node createRootNode() {
        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(incomingWelcomeMessage),
                        new AudioPrompt().setCommand(messageForMedicinesDuringIncomingCall),
                        new MenuAudioPrompt().setName(TamaIVRMessage.DOSE_TAKEN_MENU_OPTION),
                        new MenuAudioPrompt().setCommand(symptomAndMessagesCommand))
                .setTransitions(new Object[][]{
                        {"1", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(updateAdherenceAsCapturedCommand)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(messageOnPillTakenDuringIncomingCall),
                                                        new AudioPrompt().setCommand(adherenceWhenPreviousDosageCapturedCommand)
                                                ))
                        },
                        {"2", TAMATransitionFactory.createCallStateTransition(CallState.SYMPTOM_REPORTING)},
                        {"3", TAMATransitionFactory.createCallStateTransition(CallState.PULL_MESSAGES_TREE)}
                });
    }
}

