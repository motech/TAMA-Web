package org.motechproject.tama.web.builder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.common.CallTypeConstants;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Patients;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.web.mapper.CallLogViewMapper;
import org.motechproject.tama.web.model.CallFlowDetails;
import org.motechproject.tama.web.model.CallLogSummary;
import org.motechproject.tama.web.view.CallFlowGroupView;
import org.motechproject.tama.web.view.CallLogView;

import java.util.*;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.join;
import static org.motechproject.tama.common.TAMAConstants.DATETIME_YYYY_MM_DD_FORMAT;
import static org.motechproject.tama.web.view.CallFlowConstants.TREE_TO_FLOW_MAP;

public class CallLogSummaryBuilder {

    public static final String NOT_REGISTERED_USER = "Not Registered User";
    public static final String TAMA = "TAMA";

    private AllPatients allPatients;
    private Patients allLoadedPatients = new Patients();
    private AllIVRLanguagesCache allIVRLanguages;

    public CallLogSummaryBuilder(AllPatients allPatients, Patients allLoadedPatients, AllIVRLanguagesCache allIVRLanguages) {
        this.allPatients = allPatients;
        this.allLoadedPatients = allLoadedPatients;
        this.allIVRLanguages = allIVRLanguages;
    }

    public CallLogSummary build(CallLog callLog) {
        CallDirection callDirection = callLog.getCallDirection();
        boolean isInboundCall = callDirection == CallDirection.Inbound;
        return new CallLogSummary(getPatientId(callLog),
                getSourcePhoneNumber(callLog, isInboundCall),
                getDestinationPhoneNumber(callLog, isInboundCall),
                getInitiatedTime(callLog, isInboundCall),
                getTimeStampOfFirstEvent(callLog).toString(DATETIME_YYYY_MM_DD_FORMAT),
                callLog.getEndTime().toString(DATETIME_YYYY_MM_DD_FORMAT),
                getClinicId(callLog),
                getCallLanguage(callLog),
                getTravelTimeToClinic(callLog),
                getCallLogFlows(callLog),
                getCallFlowDetails(callLog),
                getGender(callLog),
                getAge(callLog));
    }

    public Map<String, CallFlowDetails> getCallFlowDetails(CallLog callLog) {
        List<CallLogView> callLogViews = mapCallLogToCallLogView(callLog);
        HashMap<String, Integer> numberOfTimesFlowAccessedMap = new HashMap<String, Integer>();
        HashMap<String, Integer> flowDurationMap = new HashMap<String, Integer>();
        HashMap<String, String> allDurationsForFlow = new HashMap<String, String>();
        Map<String, CallFlowDetails> callFlowDetailsMap = new HashMap<String, CallFlowDetails>();

        initializeFlowDetails(numberOfTimesFlowAccessedMap, flowDurationMap, allDurationsForFlow);

        populateFlowDetails(callLogViews, numberOfTimesFlowAccessedMap, flowDurationMap, allDurationsForFlow);

        buildCallFlowDetails(numberOfTimesFlowAccessedMap, flowDurationMap, allDurationsForFlow, callFlowDetailsMap);

        return callFlowDetailsMap;
    }

    private void buildCallFlowDetails(HashMap<String, Integer> numberOfTimesFlowAccessedMap, HashMap<String, Integer> flowDurationMap, HashMap<String, String> allDurationsForFlow, Map<String, CallFlowDetails> callFlowDetailsMap) {
        for (String flow : allDurationsForFlow.keySet()) {
            CallFlowDetails callFlowDetails = new CallFlowDetails();
            callFlowDetails.setTotalAccessDuration(flowDurationMap.get(flow));
            callFlowDetails.setIndividualAccessDurations(allDurationsForFlow.get(flow));
            callFlowDetails.setNumberOfTimesAccessed(numberOfTimesFlowAccessedMap.get(flow));
            callFlowDetailsMap.put(flow , callFlowDetails);
        }
    }

    private void populateFlowDetails(List<CallLogView> callLogViews, HashMap<String, Integer> numberOfTimesFlowAccessedMap, HashMap<String, Integer> flowDurationMap, HashMap<String, String> allDurationsForFlow) {
        if (!callLogViews.isEmpty()) {
            List<CallFlowGroupView> callFlowGroupViews = callLogViews.get(0).getCallFlowGroupViews();
            for (CallFlowGroupView callFlowGroupView : callFlowGroupViews) {
                updateNumberOfTimesCurrentFlowAccessed(numberOfTimesFlowAccessedMap, callFlowGroupView);
                updateTotalDurationOfCurrentFlow(flowDurationMap, callFlowGroupView);
                appendCurrentFlowDurationToExistingDurationsForFlow(allDurationsForFlow, callFlowGroupView);
            }
        }
    }

    private void initializeFlowDetails(HashMap<String, Integer> numberOfTimesFlowAccessedMap, HashMap<String, Integer> flowDurationMap, HashMap<String, String> allDurationsForFlow) {
        for (String tree : TREE_TO_FLOW_MAP.keySet()) {
            String flow = TREE_TO_FLOW_MAP.get(tree);
            flowDurationMap.put(flow, 0);
            numberOfTimesFlowAccessedMap.put(flow, 0);
            allDurationsForFlow.put(flow, "NA");
        }

        flowDurationMap.put(CallTypeConstants.HEALTH_TIPS, 0);
        numberOfTimesFlowAccessedMap.put(CallTypeConstants.HEALTH_TIPS, 0);
        allDurationsForFlow.put(CallTypeConstants.HEALTH_TIPS, "NA");
    }

    private void updateTotalDurationOfCurrentFlow(HashMap<String, Integer> flowDurationMap, CallFlowGroupView callFlowGroupView) {
        Integer netDurationPerFlow;
        netDurationPerFlow = flowDurationMap.get(callFlowGroupView.getFlow()) + callFlowGroupView.getFlowDuration();
        flowDurationMap.put(callFlowGroupView.getFlow(), netDurationPerFlow);
    }

    private void updateNumberOfTimesCurrentFlowAccessed(HashMap<String, Integer> numberOfTimesFlowAccessedMap, CallFlowGroupView callFlowGroupView) {
        Integer numberOfTimesFlowAccessed;
        numberOfTimesFlowAccessed = numberOfTimesFlowAccessedMap.get(callFlowGroupView.getFlow()) + 1;
        numberOfTimesFlowAccessedMap.put(callFlowGroupView.getFlow(), numberOfTimesFlowAccessed);
    }

    private void appendCurrentFlowDurationToExistingDurationsForFlow(HashMap<String, String> allDurationsForFlow, CallFlowGroupView callFlowGroupView) {
        String durationsForFlow;
        durationsForFlow = allDurationsForFlow.get(callFlowGroupView.getFlow());
        String flowDurationAsString = String.valueOf(callFlowGroupView.getFlowDuration());
        if (durationsForFlow.contains("NA")) {
            allDurationsForFlow.put(callFlowGroupView.getFlow(), flowDurationAsString);
        } else {
            durationsForFlow = StringUtils.join(Arrays.asList(durationsForFlow, flowDurationAsString), ", ");
            allDurationsForFlow.put(callFlowGroupView.getFlow(), durationsForFlow);
        }
    }

    private String getInitiatedTime(CallLog callLog, boolean inboundCall) {
        return inboundCall ? "NA" : callLog.getStartTime().toString(DATETIME_YYYY_MM_DD_FORMAT);
    }


    private DateTime getTimeStampOfFirstEvent(CallLog callLog) {
        return callLog.getCallEvents().isEmpty() ? callLog.getStartTime() : callLog.getCallEvents().get(0).getTimeStamp();
    }

    private String getPatientId(CallLog callLog) {
        if (isEmpty(callLog.patientId())) {
            if (CollectionUtils.isEmpty(callLog.getLikelyPatientIds())) {
                return NOT_REGISTERED_USER;
            } else {
                return join(ambiguousPatients(callLog.getLikelyPatientIds()), ", ");
            }
        } else {
            return callLog.patientId();
        }
    }

    private String getClinicId(CallLog callLog) {
        if (isEmpty(callLog.clinicId())) {
            if (CollectionUtils.isEmpty(callLog.getLikelyPatientIds())) {
                return NOT_REGISTERED_USER;
            } else {
                Patient patient = allLoadedPatients.getBy(callLog.getLikelyPatientIds().get(0));
                return patient.getClinic().getName();
            }
        } else {
            return getPatientWhenPatientIdNotEmpty(callLog).getClinic().getName();
        }
    }

    private String getSourcePhoneNumber(CallLog callLog, boolean isInboundCall) {
        if (isInboundCall) {
            return getRegisteredPhoneNumber(callLog);
        } else {
            return TAMA;
        }
    }

    private String getDestinationPhoneNumber(CallLog callLog, boolean isInboundCall) {
        if (!isInboundCall) {
            return getRegisteredPhoneNumber(callLog);
        } else {
            return TAMA;
        }
    }

    private String getTravelTimeToClinic(CallLog callLog) {
        Patient patient = getPatientWhenPatientIdNotEmpty(callLog);
        return patient != null ? patient.getTravelTimeToClinicInDays() + " Days, " + patient.getTravelTimeToClinicInHours() + " Hours, and " + patient.getTravelTimeToClinicInMinutes() + " Minutes" : " - ";
    }

    private String getCallLanguage(CallLog callLog) {
        return isEmpty(callLog.callLanguage()) ? " - " : allIVRLanguages.getByCode(callLog.callLanguage()).getName();
    }

    private String getCallLogFlows(CallLog callLog) {
        List<CallLogView> callLogViews = mapCallLogToCallLogView(callLog);
        if (callLogViews.isEmpty()){
            return " - ";
        }else {
            CallLogView callLogView = callLogViews.get(0);
            return StringUtils.join(callLogView.getCallFlowGroupViews(), ", ");
        }
    }

    private List<String> ambiguousPatients(List<String> likelyPatientIds) {
        List<String> patientIds = new ArrayList<String>();
        for (String likelyPatientId : likelyPatientIds) {
            patientIds.add(allLoadedPatients.getBy(likelyPatientId).getPatientId());
        }
        return patientIds;
    }

    private String getRegisteredPhoneNumber(CallLog callLog) {
        if (isEmpty(callLog.patientId()) && CollectionUtils.isEmpty(callLog.getLikelyPatientIds())) {
            return NOT_REGISTERED_USER + " : " + callLog.getPhoneNumber();
        } else {
            return callLog.getPhoneNumber();
        }
    }

    private String getGender(CallLog callLog) {
        Patient patient = getPatientWhenPatientIdNotEmpty(callLog);
        return patient != null ? String.valueOf(patient.getGenderType()) : " - ";
    }

    private String getAge(CallLog callLog) {
        Patient patient = getPatientWhenPatientIdNotEmpty(callLog);
        return patient != null ? String.valueOf(patient.getAge()) : " - ";
    }

    private Patient getPatientWhenPatientIdNotEmpty(CallLog callLog) {
        if (isEmpty(callLog.patientId())) return null;
        return allLoadedPatients.getBy(callLog.getPatientDocumentId());
    }

    private List<CallLogView> mapCallLogToCallLogView(CallLog callLog) {
        CallLogViewMapper callLogViewMapper = new CallLogViewMapper(allPatients, allLoadedPatients);
        return callLogViewMapper.toCallLogView(Arrays.asList(callLog));
    }
}

