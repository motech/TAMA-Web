package org.motechproject.tama.web.builder;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.facility.domain.Clinics;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Patients;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.domain.IVRLanguages;
import org.motechproject.tama.refdata.repository.AllIVRLanguages;
import org.motechproject.tama.web.mapper.CallLogViewMapper;
import org.motechproject.tama.web.model.CallLogSummary;
import org.motechproject.tama.web.view.CallFlowGroupView;
import org.motechproject.tama.web.view.CallLogView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.join;

public class CallLogSummaryBuilder {

    private static final String CALL_LOG_DATETIME_FORMAT = TAMAConstants.DATETIME_FORMAT + ":ss";
    public static final String NOT_REGISTERED_USER = "Not Registered User";
    public static final String TAMA = "TAMA";

    private AllPatients allPatients;
    private Patients allLoadedPatients = new Patients();
    private Clinics allLoadedClinics = new Clinics();
    private IVRLanguages allLoadedIVRLanguages = new IVRLanguages();

    public CallLogSummaryBuilder(AllPatients allPatients, Patients allLoadedPatients, Clinics allLoadedClinics, IVRLanguages allLoadedIVRLanguages) {
        this.allPatients = allPatients;
        this.allLoadedPatients = allLoadedPatients;
        this.allLoadedClinics = allLoadedClinics;
        this.allLoadedIVRLanguages = allLoadedIVRLanguages;
    }

    public CallLogSummary build(CallLog callLog) {
        CallDirection callDirection = callLog.getCallDirection();
        boolean isInboundCall = callDirection == CallDirection.Inbound;
        return new CallLogSummary(getPatientId(callLog),
                getSourcePhoneNumber(callLog, isInboundCall),
                getDestinationPhoneNumber(callLog, isInboundCall),
                getInitiatedTime(callLog, isInboundCall),
                getTimeStampOfFirstEvent(callLog).toString(CALL_LOG_DATETIME_FORMAT),
                callLog.getEndTime().toString(CALL_LOG_DATETIME_FORMAT),
                getClinicId(callLog),
                getCallLanguage(callLog),
                getTravelTimeToClinic(callLog),
                getCallLogFlowsAndSetFlowDurations(callLog),
                getFlowDurations(callLog),
                getGender(callLog),
                getAge(callLog));
    }

    private String getFlowDurations(CallLog callLog) {
        CallLogViewMapper callLogViewMapper = new CallLogViewMapper(allPatients, allLoadedPatients);
        List<CallLogView> callLogViews = callLogViewMapper.toCallLogView(Arrays.asList(callLog));
        String flowDurations = null;
        if (!callLogViews.isEmpty()) {
            List<CallFlowGroupView> callFlowGroupViews = callLogViews.get(0).getCallFlowGroupViews();
            for (CallFlowGroupView callFlowGroupView : callFlowGroupViews) {
                if (callFlowGroupView.getFlowDurationInSeconds() != 0) {
                    flowDurations = join(Arrays.asList(flowDurations, callFlowGroupView.getFlow() + " : " + callFlowGroupView.getFlowDuration()), "\n");
                }
            }
            return flowDurations;
        }
        return " - ";
    }

    private String getInitiatedTime(CallLog callLog, boolean inboundCall) {
        return inboundCall ? "NA" : callLog.getStartTime().toString(CALL_LOG_DATETIME_FORMAT);
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
            return allLoadedClinics.getBy(callLog.clinicId()).getName();
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
        Patient patient = getPatientWhenCallLogForPatientNotEmpty(callLog);
        return patient !=null ? patient.getTravelTimeToClinicInDays() + " Days, " + patient.getTravelTimeToClinicInHours() + " Hours, and " + patient.getTravelTimeToClinicInMinutes() + " Minutes" : " - ";
    }

    private String getCallLanguage(CallLog callLog) {
        return isEmpty(callLog.callLanguage()) ? " - " : allLoadedIVRLanguages.getBy(callLog.callLanguage()).getName();
    }

    private String getCallLogFlowsAndSetFlowDurations(CallLog callLog) {
        CallLogViewMapper callLogViewMapper = new CallLogViewMapper(allPatients, allLoadedPatients);
        List<CallLogView> callLogViews = callLogViewMapper.toCallLogView(Arrays.asList(callLog));
        return callLogViews.isEmpty() ? " - " : callLogViews.get(0).getFlows();
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
        Patient patient = getPatientWhenCallLogForPatientNotEmpty(callLog);
        return patient != null ? String.valueOf(patient.getGenderType()) : " - ";
    }

    private String getAge(CallLog callLog) {
        Patient patient = getPatientWhenCallLogForPatientNotEmpty(callLog);
        return patient != null ? String.valueOf(patient.getAge()) : " - ";
    }

    private Patient getPatientWhenCallLogForPatientNotEmpty(CallLog callLog) {
        if (isEmpty(callLog.patientId())) return null;
        return allLoadedPatients.getBy(callLog.getPatientDocumentId());
    }
}