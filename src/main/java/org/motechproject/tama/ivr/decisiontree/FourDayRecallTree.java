package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.*;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.web.command.fourdayrecall.AllDosagesTaken;
import org.motechproject.tama.web.command.fourdayrecall.DosagesMissedOnOneDay;
import org.motechproject.tama.web.command.fourdayrecall.MainMenu;
import org.motechproject.tama.web.command.fourdayrecall.WelcomeGreetingMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FourDayRecallTree extends TamaDecisionTree {
    @Autowired
    private WelcomeGreetingMessage welcomeGreetingMessage;
    @Autowired
    private AllDosagesTaken allDosagesTaken;
    @Autowired
    private DosagesMissedOnOneDay dosagesMissedOnOneDay;
    @Autowired
    private MainMenu mainMenu;

    @Override
    protected Node createRootNode(IVRContext ivrContext) {
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
                        {"2", new Transition()},
                        {"3", new Transition()},
                        {"4", new Transition()}
                }
                );
    }
}

