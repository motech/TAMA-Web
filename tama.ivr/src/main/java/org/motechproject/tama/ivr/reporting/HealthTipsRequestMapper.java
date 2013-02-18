package org.motechproject.tama.ivr.reporting;

import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.log.CallFlowDetailMap;
import org.motechproject.tama.ivr.log.CallFlowDetails;
import org.motechproject.tama.ivr.log.CallFlowGroupViews;
import org.motechproject.tama.reports.contract.HealthTipsRequest;

import java.util.Collections;
import java.util.List;

public class HealthTipsRequestMapper {

    private String patientDocumentId;
    private CallFlowGroupViews flowGroupViews;
    private CallLog callLog;
    private CallFlowDetailMap callFlowDetailMap;

    public HealthTipsRequestMapper(CallLog callLog) {
        this.callLog = callLog;
        this.patientDocumentId = callLog.getPatientDocumentId();
        this.flowGroupViews = new CallFlowGroupViews(callLog);
        initializeCallFlowDetails(this.flowGroupViews);
    }

    private void initializeCallFlowDetails(CallFlowGroupViews flowGroupViews) {
        this.callFlowDetailMap = new CallFlowDetailMap();
        this.callFlowDetailMap.populateFlowDetails(flowGroupViews);
    }

    public HealthTipsRequest map() {
        HealthTipsRequest healthTipsRequest = new HealthTipsRequest();
        healthTipsRequest.setPatientDocumentId(patientDocumentId);
        healthTipsRequest.setNumberOfTimesHealthTipsAccessed(numberOfTimesHealthTipsAccessed());
        healthTipsRequest.setHealthTipsPlayed(healthTipsPlayed());
        healthTipsRequest.setIndividualHealthTipsAccessDurations(individualHealthTipsAccessDurations());
        healthTipsRequest.setTotalHealthTipsAccessDuration(totalDurationOfHealthTipFlows());
        healthTipsRequest.setCallDirection(callDirection());
        healthTipsRequest.setCallDate(callLog.getStartTime().toDate());
        return healthTipsRequest;
    }

    private String callDirection() {
        return (CallDirection.Inbound.equals(callLog.getCallDirection())) ? "Incoming" : "Outgoing";
    }

    private Long totalDurationOfHealthTipFlows() {
        CallFlowDetails callFlowDetails = healthTipsDetails();
        return (null == callFlowDetails) ? 0L : callFlowDetails.getTotalAccessDuration();
    }

    private List<Integer> individualHealthTipsAccessDurations() {
        CallFlowDetails callFlowDetails = healthTipsDetails();
        return (null == callFlowDetails) ? Collections.<Integer>emptyList() : callFlowDetails.getAllIndividualAccessDurations();
    }

    private List<String> healthTipsPlayed() {
        CallFlowDetails callFlowDetails = healthTipsDetails();
        return (null == callFlowDetails) ? Collections.<String>emptyList() : callFlowDetails.getResponses();
    }

    private int numberOfTimesHealthTipsAccessed() {
        CallFlowDetails callFlowDetails = healthTipsDetails();
        return (null == callFlowDetails) ? 0 : callFlowDetails.getNumberOfTimesAccessed();
    }

    private CallFlowDetails healthTipsDetails() {
        return callFlowDetailMap.getCallFlowDetailsMap().get(CallTypeConstants.HEALTH_TIPS);
    }
}
