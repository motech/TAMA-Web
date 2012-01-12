package org.motechproject.tama.symptomreporting.decisiontree;

import org.apache.commons.lang.ArrayUtils;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.DialPrompt;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.symptomreporting.command.DialStateCommand;
import org.motechproject.tama.symptomreporting.command.RecordSymptomCommand;
import org.motechproject.tama.symptomreporting.command.SuspendAdherenceCallsCommand;
import org.motechproject.tama.symptomreporting.command.SymptomReportingAlertsCommand;
import org.motechproject.tama.symptomreporting.decisiontree.filter.RegExpBasedTreeNodeFilter;
import org.motechproject.tama.symptomreporting.decisiontree.filter.TreeNodeFilter;
import org.motechproject.tama.symptomreporting.service.SymptomRecordingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SymptomReportingTreeInterceptor {

    private String[] firstPriorityFilterCriteria = {"adv_crocin01", "adv_noteatanythg"};
    private String[] secondPriorityFilterCriteria = {"adv_stopmedicineseeclinicasap", "adv_seeclinicasapdepression"};
    private String[] thirdPriorityFilterCriteria = {"adv_continuemedicineseeclinicasap"};
    private String[] fourthPriorityFilterCriteria = {"adv_callclinic"};
    private String[] fifthPriorityFilterCriteria = {"adv_tingpainfeetcropanto", "adv_tingpainfeetcro", "adv_crocin02", "adv_crocin03", "adv_crocinpanto01", "adv_crocinpanto02",
            "adv_halfhourcontmed01", "adv_halfhourcro01", "adv_halfhourcrocinpanto01", "adv_halfhourpanto01", "adv_levo01", "adv_levopanto01", "adv_panto01",
            "adv_panto02", "adv_tingpainfeet", "adv_tingpainfeetpanto"};
    private String[] switchToDialPromptFilterCriteria = (String[]) ArrayUtils.addAll(firstPriorityFilterCriteria, secondPriorityFilterCriteria);
    private String[] suspendAdherenceCallsFilterCriteria = {"adv_crocin01", "adv_noteatanythg", "adv_stopmedicineseeclinicasap"};


    private SymptomReportingAlertsCommand symptomReportingAlertsCommand;
    private DialStateCommand dialStateCommand;
    private SuspendAdherenceCallsCommand suspendAdherenceCallsCommand;

    private TAMAIVRContextFactory contextFactory;
    private SymptomRecordingService symptomRecordingService;

    @Autowired
    public SymptomReportingTreeInterceptor(SymptomReportingAlertsCommand symptomReportingAlertsCommand, DialStateCommand dialStateCommand, SuspendAdherenceCallsCommand suspendAdherenceCallsCommand,
                                           SymptomRecordingService symptomRecordingService) {
        this.symptomReportingAlertsCommand = symptomReportingAlertsCommand;
        this.dialStateCommand = dialStateCommand;
        this.suspendAdherenceCallsCommand = suspendAdherenceCallsCommand;
        this.contextFactory = new TAMAIVRContextFactory();
        this.symptomRecordingService = symptomRecordingService;
    }

    SymptomReportingTreeInterceptor(SymptomReportingAlertsCommand symptomReportingAlertsCommand, DialStateCommand dialStateCommand, SuspendAdherenceCallsCommand suspendAdherenceCallsCommand,
                                    TAMAIVRContextFactory contextFactory, SymptomRecordingService symptomRecordingService) {
        this.symptomReportingAlertsCommand = symptomReportingAlertsCommand;
        this.dialStateCommand = dialStateCommand;
        this.suspendAdherenceCallsCommand = suspendAdherenceCallsCommand;
        this.contextFactory = contextFactory;
        this.symptomRecordingService = symptomRecordingService;
    }

    public void addCommands(Node node) {
        addAlerts(node);
        addDialPrompt(node);
        addSuspendAdherenceCallsCommand(node);
        addCommandToLogSymptoms(node);
    }

    private void addCommandToLogSymptoms(Node node) {
        RegExpBasedTreeNodeFilter filter = new RegExpBasedTreeNodeFilter("^q_.*");
        final List<Node> nodes = filter.filter(node);
        for (Node priorityNode : nodes) {
        	Prompt questionPrompt = filter.selectPrompt(priorityNode);
            setRecordSymptomReportTreeCommand(priorityNode, convertQuestionToSymptomId(questionPrompt));
        }
    }

	private String convertQuestionToSymptomId(Prompt questionPrompt) {
		return questionPrompt.getName().replaceFirst(".*_", "");
	}

    private void setRecordSymptomReportTreeCommand(Node priorityNode, String symptomId) {
    	Transition yesTransition = priorityNode.getTransitions().get("1");
    	Node yesNode = yesTransition.getDestinationNode();
    	if (yesNode != null){
    		yesNode.setTreeCommands(new RecordSymptomCommand(contextFactory, symptomRecordingService, symptomId));
    	}
	}

	private void addAlerts(Node node) {
        List<TreeNodeFilter> finders = Arrays.asList(
                new TreeNodeFilter(firstPriorityFilterCriteria),
                new TreeNodeFilter(secondPriorityFilterCriteria),
                new TreeNodeFilter(thirdPriorityFilterCriteria),
                new TreeNodeFilter(fourthPriorityFilterCriteria),
                new TreeNodeFilter(fifthPriorityFilterCriteria)
        );
        for (int i = 0; i < finders.size(); i++) {
            final List<Node> nodes = finders.get(i).filter(node);
            for (Node priorityNode : nodes) {
                boolean isDialPrompt = new TreeNodeFilter(switchToDialPromptFilterCriteria).select(priorityNode);
                TAMAConstants.ReportedType symptomReportedToDoctor = isDialPrompt ? TAMAConstants.ReportedType.No : TAMAConstants.ReportedType.NA;
                priorityNode.setTreeCommands(symptomReportingAlertsCommand.symptomReportingAlertWithPriority(i + 1, priorityNode, symptomReportedToDoctor));
            }
        }
    }

    private void addDialPrompt(Node node) {
        final List<Node> nodes = new TreeNodeFilter(switchToDialPromptFilterCriteria).filter(node);
        for (Node priorityNode : nodes) {
            DialPrompt dialPrompt = new DialPrompt();
            dialPrompt.setCommand(dialStateCommand);
            priorityNode.addPrompts(dialPrompt);
        }
    }

    private void addSuspendAdherenceCallsCommand(Node node) {
        TreeNodeFilter filter = new TreeNodeFilter(suspendAdherenceCallsFilterCriteria);
        final List<Node> nodes = filter.filter(node);
        for (Node priorityNode : nodes) {
            priorityNode.setTreeCommands(suspendAdherenceCallsCommand);
        }
    }
}
