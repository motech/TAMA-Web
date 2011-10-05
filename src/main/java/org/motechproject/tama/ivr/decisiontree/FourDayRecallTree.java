package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.web.command.fourdayrecall.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FourDayRecallTree extends TamaDecisionTree {
    @Autowired
    private WelcomeGreetingMessage welcomeGreetingMessage;
    @Autowired
    private MainMenu mainMenu;
    @Autowired
    private AllDosagesTaken allDosagesTaken;
    @Autowired
    private DosagesMissedOnOneDay dosagesMissedOnOneDay;
    @Autowired
    private DosageMissedOnMultipleDays dosageMissedOnMultipleDays;

    @Override
    protected Node createRootNode(IVRContext ivrContext) {
        Transition missedMultiplePillsTransition = new Transition()
                .setDestinationNode(
                        new Node()
                                .setPrompts(new AudioPrompt().setCommand(dosageMissedOnMultipleDays))
                );
        return new Node()
                .setPrompts(
                        new AudioPrompt().setCommand(welcomeGreetingMessage),
                        new MenuAudioPrompt().setCommand(mainMenu)
                )
                .setTransitions(new Object[][]{
                        {"0", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(allDosagesTaken)))
                        },
                        {"1", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setPrompts(new AudioPrompt().setCommand(dosagesMissedOnOneDay))
                                )
                        },
                        {"2", missedMultiplePillsTransition
                        },
                        {"3", missedMultiplePillsTransition
                        },
                        {"4", missedMultiplePillsTransition
                        }
                });
    }
}

