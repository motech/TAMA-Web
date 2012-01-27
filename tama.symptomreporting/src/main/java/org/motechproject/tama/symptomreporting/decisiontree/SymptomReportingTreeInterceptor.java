package org.motechproject.tama.symptomreporting.decisiontree;

import org.motechproject.decisiontree.model.*;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.symptomreporting.command.*;
import org.motechproject.tama.symptomreporting.filter.*;
import org.motechproject.tama.symptomreporting.service.SymptomRecordingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class SymptomReportingTreeInterceptor {
    private static final String QUESTION_RASHALLOVERBODY = "q_rashalloverbody";
    private static final String LOCALRASH = "localrash";
    private static final String USER_OPTION_SYMPTOM_YES = "1";
	private static final String USER_OPTION_SYMPTOM_NO = "3";

    private SymptomReportingAlertsCommand symptomReportingAlertsCommand;
    private DialStateCommand dialStateCommand;
    private SuspendAdherenceCallsCommand suspendAdherenceCallsCommand;
    private AlertFilters alertFilters;
    private SwitchDialPromptFilter switchDialPromptFilter;
    private SuspendAdherenceCallsFilter suspendAdherenceCallsFilter;
    private SMSFilter smsFilter;
    private SmsService smsService;
    private AllPatients allPatients;
    private Properties properties;

    private SymptomRecordingService symptomRecordingService;

    @Autowired
    public SymptomReportingTreeInterceptor(SymptomReportingAlertsCommand symptomReportingAlertsCommand,
                                           DialStateCommand dialStateCommand,
                                           SuspendAdherenceCallsCommand suspendAdherenceCallsCommand,
                                           SymptomRecordingService symptomRecordingService,
                                           AlertFilters alertFilters,
                                           SwitchDialPromptFilter switchDialPromptFilter,
                                           SuspendAdherenceCallsFilter suspendAdherenceCallsFilter,
                                           SMSFilter smsFilter,
                                           SmsService smsService,
                                           AllPatients allPatients,
                                           @Qualifier("symptomSMSProperties") Properties properties) {

        this.symptomReportingAlertsCommand = symptomReportingAlertsCommand;
        this.dialStateCommand = dialStateCommand;
        this.suspendAdherenceCallsCommand = suspendAdherenceCallsCommand;
        this.alertFilters = alertFilters;
        this.switchDialPromptFilter = switchDialPromptFilter;
        this.suspendAdherenceCallsFilter = suspendAdherenceCallsFilter;
        this.smsFilter = smsFilter;
        this.smsService = smsService;
        this.allPatients = allPatients;
        this.properties = properties;
        this.symptomRecordingService = symptomRecordingService;
    }

    public void addCommands(Node node) {
        addAlerts(node);
        addDialPrompt(node);
        addSendSMSCommand(node);
        addSuspendAdherenceCallsCommand(node);
        addCommandToLogSymptoms(node);
    }

    private void addCommandToLogSymptoms(Node node) {
        RegExpBasedTreeNodeFilter filter = new RegExpBasedTreeNodeFilter("^q_.*");
        final List<Node> nodes = filter.filter(node);
        for (Node priorityNode : nodes) {
        	Prompt questionPrompt = filter.selectPrompt(priorityNode);
			setRecordSymptomReportTreeCommand(priorityNode, convertQuestionToSymptomId(questionPrompt), USER_OPTION_SYMPTOM_YES);
			if (QUESTION_RASHALLOVERBODY.equals(questionPrompt.getName())) { 
				setRecordSymptomReportTreeCommand(priorityNode, LOCALRASH, USER_OPTION_SYMPTOM_NO);
			}
        }
    }

	private String convertQuestionToSymptomId(Prompt questionPrompt) {
		return questionPrompt.getName().replaceFirst(".*_", "");
	}

    private void setRecordSymptomReportTreeCommand(Node priorityNode, String symptomId, String userSelectedOption) {
		Transition yesTransition = priorityNode.getTransitions().get(userSelectedOption);
    	Node yesNode = yesTransition.getDestinationNode();
    	if (yesNode != null){
    		yesNode.setTreeCommands(new RecordSymptomCommand(symptomRecordingService, symptomId));
    	}
	}

	private void addAlerts(Node node) {
        List<TreeNodeFilter> finders = alertFilters.getAll();
        for (int i = 0; i < finders.size(); i++) {
            final List<Node> nodes = finders.get(i).filter(node);
            for (Node priorityNode : nodes) {
                boolean isDialPrompt = switchDialPromptFilter.select(priorityNode);
                TAMAConstants.ReportedType symptomReportedToDoctor = isDialPrompt ? TAMAConstants.ReportedType.No : TAMAConstants.ReportedType.NA;
                priorityNode.setTreeCommands(symptomReportingAlertsCommand.symptomReportingAlertWithPriority(i + 1, priorityNode, symptomReportedToDoctor));
            }
        }
    }

    private void addDialPrompt(Node node) {
        final List<Node> nodes = switchDialPromptFilter.filter(node);
        for (Node priorityNode : nodes) {
            DialPrompt dialPrompt = new DialPrompt();
            dialPrompt.setCommand(dialStateCommand);
            priorityNode.addPrompts(dialPrompt);
        }
    }

    private void addSendSMSCommand(Node node) {
        final List<Node> nodes = smsFilter.filter(node);
        for (Node priorityNode : nodes) {
            AudioPrompt audioPrompt = new AudioPrompt();
            SendSMSCommand sendSMSCommand = new SendSMSCommand(priorityNode.getPrompts(), smsService, allPatients, properties);
            audioPrompt.setName("n10:sendSMS").setCommand(sendSMSCommand);
            priorityNode.addPrompts(audioPrompt);
        }
    }

    private void addSuspendAdherenceCallsCommand(Node node) {
        final List<Node> nodes = suspendAdherenceCallsFilter.filter(node);
        for (Node priorityNode : nodes) {
            priorityNode.setTreeCommands(suspendAdherenceCallsCommand);
        }
    }
}
