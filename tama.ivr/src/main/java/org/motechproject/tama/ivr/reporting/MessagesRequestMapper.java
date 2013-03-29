
package org.motechproject.tama.ivr.reporting;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.log.CallFlowDetailMap;
import org.motechproject.tama.ivr.log.CallFlowDetails;
import org.motechproject.tama.ivr.log.CallFlowGroupViews;
import org.motechproject.tama.reports.contract.MessagesRequest;

import java.util.Collections;
import java.util.List;

public class MessagesRequestMapper {

    private String patientDocumentId;
    private CallFlowGroupViews flowGroupViews;
    private CallLog callLog;
    private CallFlowDetailMap callFlowDetailMap;

    public MessagesRequestMapper(CallLog callLog) {
        this.callLog = callLog;
        this.patientDocumentId = callLog.getPatientDocumentId();
        this.flowGroupViews = new CallFlowGroupViews(callLog);
        initializeCallFlowDetails(this.flowGroupViews);
    }

    private void initializeCallFlowDetails(CallFlowGroupViews flowGroupViews) {
        this.callFlowDetailMap = new CallFlowDetailMap();
        this.callFlowDetailMap.populateFlowDetails(flowGroupViews);
    }

    public MessagesRequest map(String pullMessagesFlowType, String pushMessageFlowType) {
        MessagesRequest messagesRequest = new MessagesRequest();
        messagesRequest.setPatientDocumentId(patientDocumentId);
        if (StringUtils.isNotBlank(pushMessageFlowType)) {
            messagesRequest.setPushedMessages(messagesPlayed(pushMessageFlowType));
        }
        messagesRequest.setNumberOfTimesMessagesAccessed(numberOfTimesMessagesAccessed(pullMessagesFlowType));
        messagesRequest.setMessagesPlayed(messagesPlayed(pullMessagesFlowType));
        messagesRequest.setIndividualMessagesAccessDurations(individualMessagesAccessDurations(pullMessagesFlowType));
        messagesRequest.setTotalMessagesAccessDuration(totalDurationOfMessaagesFlows(pullMessagesFlowType));
        messagesRequest.setCallDirection(callDirection());
        messagesRequest.setCallDate(callLog.getStartTime().toDate());
        return messagesRequest;
    }

    private String callDirection() {
        return (CallDirection.Inbound.equals(callLog.getCallDirection())) ? "Incoming" : "Outgoing";
    }

    private Long totalDurationOfMessaagesFlows(String flowType) {
        CallFlowDetails callFlowDetails = messagesDetails(flowType);
        return (null == callFlowDetails) ? 0L : callFlowDetails.getTotalAccessDuration();
    }

    private List<Integer> individualMessagesAccessDurations(String flowType) {
        CallFlowDetails callFlowDetails = messagesDetails(flowType);
        return (null == callFlowDetails) ? Collections.<Integer>emptyList() : callFlowDetails.getAllIndividualAccessDurations();
    }

    private List<String> messagesPlayed(String flowType) {
        CallFlowDetails callFlowDetails = messagesDetails(flowType);
        return (null == callFlowDetails) ? Collections.<String>emptyList() : callFlowDetails.getResponses();
    }

    private int numberOfTimesMessagesAccessed(String flowType) {
        CallFlowDetails callFlowDetails = messagesDetails(flowType);
        return (null == callFlowDetails) ? 0 : callFlowDetails.getNumberOfTimesAccessed();
    }

    private CallFlowDetails messagesDetails(String flowType) {
        return callFlowDetailMap.getCallFlowDetailsMap().get(flowType);
    }
}
