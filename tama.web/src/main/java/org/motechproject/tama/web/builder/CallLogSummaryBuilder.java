package org.motechproject.tama.web.builder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Patients;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.web.mapper.CallLogViewMapper;
import org.motechproject.tama.web.model.CallFlowDetailMap;
import org.motechproject.tama.web.model.CallFlowDetails;
import org.motechproject.tama.web.model.CallLogSummary;
import org.motechproject.tama.web.view.CallLogView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.join;
import static org.motechproject.tama.common.TAMAConstants.DATETIME_YYYY_MM_DD_FORMAT;

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
                getClinicName(callLog),
                getCallLanguage(callLog),
                getTravelTimeToClinic(callLog),
                getCallLogFlows(callLog),
                getCallFlowDetails(callLog),
                getGender(callLog),
                getAge(callLog));
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

    private String getClinicName(CallLog callLog) {
        if (CollectionUtils.isEmpty(callLog.getLikelyPatientIds()) && StringUtils.isEmpty(callLog.getPatientDocumentId())) {
            return NOT_REGISTERED_USER;
        } else {
            final String patientDocId = StringUtils.isNotEmpty(callLog.getPatientDocumentId()) ? callLog.getPatientDocumentId() : callLog.getLikelyPatientIds().get(0);
            Patient patient = allLoadedPatients.getBy(patientDocId);
            return patient.getClinic().getName();
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
        if (isInboundCall) {
            return TAMA;
        } else {
            return getRegisteredPhoneNumber(callLog);
        }
    }

    private String getInitiatedTime(CallLog callLog, boolean inboundCall) {
        return inboundCall ? "NA" : callLog.getStartTime().toString(DATETIME_YYYY_MM_DD_FORMAT);
    }

    private DateTime getTimeStampOfFirstEvent(CallLog callLog) {
        return callLog.getCallEvents().isEmpty() ? callLog.getStartTime() : callLog.getCallEvents().get(0).getTimeStamp();
    }

    private String getCallLanguage(CallLog callLog) {
        return isEmpty(callLog.callLanguage()) ? " - " : allIVRLanguages.getByCode(callLog.callLanguage()).getName();
    }

    private String getTravelTimeToClinic(CallLog callLog) {
        Patient patient = getPatientWhenPatientIdNotEmpty(callLog);
        return patient != null ? patient.getTravelTimeToClinicInDays() + " Days, " + patient.getTravelTimeToClinicInHours() + " Hours, and " + patient.getTravelTimeToClinicInMinutes() + " Minutes" : " - ";
    }

    private String getCallLogFlows(CallLog callLog) {
        CallLogView callLogView = mapCallLogToCallLogView(callLog);
        if (null == callLogView) {
            return " - ";
        } else {
            return StringUtils.join(callLogView.getCallFlowGroupViews(), ", ");
        }
    }

    public Map<String, CallFlowDetails> getCallFlowDetails(CallLog callLog) {
        CallFlowDetailMap callFlowDetailMap = new CallFlowDetailMap();
        callFlowDetailMap.populateFlowDetails(mapCallLogToCallLogView(callLog).getFlowGroupViews());
        return callFlowDetailMap.getCallFlowDetailsMap();
    }

    private String getGender(CallLog callLog) {
        Patient patient = getPatientWhenPatientIdNotEmpty(callLog);
        return patient != null ? String.valueOf(patient.getGenderType()) : " - ";
    }

    private String getAge(CallLog callLog) {
        Patient patient = getPatientWhenPatientIdNotEmpty(callLog);
        return patient != null ? String.valueOf(patient.getAge()) : " - ";
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

    private Patient getPatientWhenPatientIdNotEmpty(CallLog callLog) {
        if (isEmpty(callLog.patientId())) return null;
        return allLoadedPatients.getBy(callLog.getPatientDocumentId());
    }

    private CallLogView mapCallLogToCallLogView(CallLog callLog) {
        CallLogViewMapper callLogViewMapper = new CallLogViewMapper(allPatients, allLoadedPatients);
        List<CallLogView> views = callLogViewMapper.toCallLogView(asList(callLog));
        return CollectionUtils.isEmpty(views) ? null : views.get(0);
    }
}

