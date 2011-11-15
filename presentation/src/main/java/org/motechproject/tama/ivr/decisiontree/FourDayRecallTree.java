package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.MenuAudioPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.web.command.fourdayrecall.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FourDayRecallTree extends TamaDecisionTree {
    @Autowired
    private OutgoingWelcomeGreetingMessage welcomeGreetingMessage;
    @Autowired
    private MainMenu mainMenu;
    @Autowired
    private AllDosagesTaken allDosagesTaken;
    @Autowired
    private DosagesMissedOnOneDay dosagesMissedOnOneDay;
    @Autowired
    private DosageMissedOnMultipleDays dosageMissedOnMultipleDays;
    @Autowired
    private CreateWeeklyAdherenceLogs createWeeklyAdherenceLogs;
    @Autowired
    private WeeklyAdherencePercentage weeklyAdherencePercentage;

    @Override
    protected Node createRootNode() {
        Transition missedMultipleDosagesTransition = new Transition()
                .setDestinationNode(
                        new Node()
                                .setTreeCommands(createWeeklyAdherenceLogs)
                                .setPrompts(
                                        new AudioPrompt().setCommand(dosageMissedOnMultipleDays),
                                        new AudioPrompt().setCommand(weeklyAdherencePercentage)
                                )
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
                                                .setTreeCommands(createWeeklyAdherenceLogs)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(allDosagesTaken),
                                                        new AudioPrompt().setCommand(weeklyAdherencePercentage)
                                                ))
                        },
                        {"1", new Transition()
                                .setDestinationNode(
                                        new Node()
                                                .setTreeCommands(createWeeklyAdherenceLogs)
                                                .setPrompts(
                                                        new AudioPrompt().setCommand(dosagesMissedOnOneDay),
                                                        new AudioPrompt().setCommand(weeklyAdherencePercentage)
                                                )
                                )
                        },
                        {"2", missedMultipleDosagesTransition
                        },
                        {"3", missedMultipleDosagesTransition
                        },
                        {"4", missedMultipleDosagesTransition
                        }
                });
    }
}