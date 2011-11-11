package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.DialPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.tama.ivr.decisiontree.filter.DecisionTreeNodesFilter;
import org.motechproject.tama.ivr.decisiontree.filter.alerts.*;
import org.motechproject.tama.web.command.SymptomReportingAlertsCommand;
import org.motechproject.tama.web.command.callforwarding.DialStateCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SymptomReportingService {

    private FirstPrioritySymptomReportingFilter firstPriorityNodeFinder;
    private SecondPrioritySymptomReportingFilter secondPriorityNodeFinder;
    private ThirdPrioritySymptomReportingFilter thirdPriorityNodeFinder;
    private FourthPrioritySymptomReportingFilter fourthPriorityNodeFinder;
    private FifthPrioritySymptomReportingFilter fifthPriorityNodeFinder;

    private SymptomReportingAlertsCommand symptomReportingAlertsCommand;
    private DialStateCommand dialStateCommand;

    @Autowired
    public SymptomReportingService(FirstPrioritySymptomReportingFilter firstPriorityNodeFinder, SecondPrioritySymptomReportingFilter secondPriorityNodeFinder, ThirdPrioritySymptomReportingFilter thirdPriorityNodeFinder, FourthPrioritySymptomReportingFilter fourthPriorityNodeFinder, FifthPrioritySymptomReportingFilter fifthPriorityNodeFinder, SymptomReportingAlertsCommand symptomReportingAlertsCommand, DialStateCommand dialStateCommand) {
        this.firstPriorityNodeFinder = firstPriorityNodeFinder;
        this.secondPriorityNodeFinder = secondPriorityNodeFinder;
        this.thirdPriorityNodeFinder = thirdPriorityNodeFinder;
        this.fourthPriorityNodeFinder = fourthPriorityNodeFinder;
        this.fifthPriorityNodeFinder = fifthPriorityNodeFinder;
        this.symptomReportingAlertsCommand = symptomReportingAlertsCommand;
        this.dialStateCommand = dialStateCommand;
    }

    public void addCommands(Node node) {
        addAlerts(node);
        addDialPrompt(node);
    }

    private Node addAlerts(Node node) {
        List<DecisionTreeNodesFilter> finders = Arrays.
                asList(firstPriorityNodeFinder,
                        secondPriorityNodeFinder,
                        thirdPriorityNodeFinder,
                        fourthPriorityNodeFinder,
                        fifthPriorityNodeFinder);
        for (int i = 0; i < finders.size(); i++) {
            final List<Node> nodes = finders.get(i).filter(node);
            for (Node priorityNode : nodes) {
               priorityNode.setTreeCommands(symptomReportingAlertsCommand.symptomReportingAlertWithPriority(i + 1, priorityNode));
           }
        }
        return node;
    }

    private Node addDialPrompt(Node node) {
        final List<Node> nodes = firstPriorityNodeFinder.filter(node);
        for (Node priorityNode : nodes) {
            DialPrompt dialPrompt = new DialPrompt();
            dialPrompt.setCommand(dialStateCommand);
            priorityNode.setPrompts(dialPrompt);
        }
        return node;
    }
}
