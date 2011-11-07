package org.motechproject.tama.ivr.decisiontree;

import org.motechproject.decisiontree.model.Node;
import org.motechproject.tama.ivr.decisiontree.filter.DecisionTreeNodesFilter;
import org.motechproject.tama.ivr.decisiontree.filter.alerts.*;
import org.motechproject.tama.web.command.SymptomReportingAlertsCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SymptomReportingAlertService {

    private FirstPrioritySymptomReportingFilter firstPriorityNodeFinder;
    private SecondPrioritySymptomReportingFilter secondPriorityNodeFinder;
    private ThirdPrioritySymptomReportingFilter thirdPriorityNodeFinder;
    private FourthPrioritySymptomReportingFilter fourthPriorityNodeFinder;
    private FifthPrioritySymptomReportingFilter fifthPriorityNodeFinder;

    private SymptomReportingAlertsCommand symptomReportingAlertsCommand;

    @Autowired
    public SymptomReportingAlertService(FirstPrioritySymptomReportingFilter firstPriorityNodeFinder, SecondPrioritySymptomReportingFilter secondPriorityNodeFinder, ThirdPrioritySymptomReportingFilter thirdPriorityNodeFinder, FourthPrioritySymptomReportingFilter fourthPriorityNodeFinder, FifthPrioritySymptomReportingFilter fifthPriorityNodeFinder, SymptomReportingAlertsCommand symptomReportingAlertsCommand) {
        this.firstPriorityNodeFinder = firstPriorityNodeFinder;
        this.secondPriorityNodeFinder = secondPriorityNodeFinder;
        this.thirdPriorityNodeFinder = thirdPriorityNodeFinder;
        this.fourthPriorityNodeFinder = fourthPriorityNodeFinder;
        this.fifthPriorityNodeFinder = fifthPriorityNodeFinder;
        this.symptomReportingAlertsCommand = symptomReportingAlertsCommand;
    }

    public Node addAlerts(Node node) {
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
}
