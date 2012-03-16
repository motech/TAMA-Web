package org.motechproject.tama.web.builder;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.repository.AllIVRLanguages;
import org.motechproject.tama.web.mapper.CallLogViewMapper;
import org.motechproject.tama.web.model.CallLogSummary;
import org.motechproject.tama.web.view.CallLogView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CallLogSummaryBuilder {

    private static final String CALL_LOG_DATETIME_FORMAT = TAMAConstants.DATETIME_FORMAT + ":ss";

    private AllPatients allPatients;
    private AllClinics allClinics;
    private AllIVRLanguages allIVRLanguages;
    private CallLogViewMapper callLogViewMapper;

    @Autowired
    public CallLogSummaryBuilder(AllPatients allPatients, CallLogViewMapper callLogViewMapper, AllClinics allClinics, AllIVRLanguages allIVRLanguages) {
        this.allPatients = allPatients;
        this.callLogViewMapper = callLogViewMapper;
        this.allClinics = allClinics;
        this.allIVRLanguages = allIVRLanguages;
    }

    public CallLogSummary build(CallLog callLog) {
        CallDirection callDirection = callLog.getCallDirection();
        boolean isInboundCall = callDirection == CallDirection.Inbound;
        Patient patient = null;
        if(!StringUtils.isEmpty(callLog.patientId())) {
            patient = allPatients.get(callLog.getPatientDocumentId());
        }
        return new CallLogSummary(getPatientId(callLog),
                getSourcePhoneNumber(callLog, isInboundCall),
                getDestinationPhoneNumber(callLog, isInboundCall),
                callLog.getStartTime().toString(CALL_LOG_DATETIME_FORMAT),
                callLog.getEndTime().toString(CALL_LOG_DATETIME_FORMAT),
                getClinicId(callLog),
                StringUtils.isEmpty(callLog.callLanguage()) ? " - " : allIVRLanguages.findByLanguageCode(callLog.callLanguage()).getName(),
                patient != null ? getTravelTimeToClinic(patient) : " - ",
                getCallLogFlows(callLog)
        );
    }

    private String getClinicId(CallLog callLog) {
        Patient patient;
        if(callLog.clinicId() == null) {
            if (callLog.getLikelyPatientIds() != null){
                patient = allPatients.get(callLog.getLikelyPatientIds().get(0));
                return patient.getClinic().getName();
            } else {
                return "Not Registered User";
            }
        } else {
            return allClinics.get(callLog.clinicId()).getName();
        }
    }

    private String getSourcePhoneNumber(CallLog callLog, boolean isInboundCall) {
        if (isInboundCall) {
            return getRegisteredPhoneNumber(callLog);
        } else {
            return "TAMA";
        }
    }

    private String getDestinationPhoneNumber(CallLog callLog, boolean isInboundCall) {
        if (!isInboundCall) {
            return getRegisteredPhoneNumber(callLog);
        } else {
            return "TAMA";
        }
    }

    private String getRegisteredPhoneNumber(CallLog callLog) {
        if (StringUtils.isEmpty(callLog.getPhoneNumber())) {
            return "Not Registered User";
        } else {
            return callLog.getPhoneNumber();
        }
    }

    private String getPatientId(CallLog callLog) {
        if (StringUtils.isEmpty(callLog.patientId())) {
            if (callLog.getLikelyPatientIds() == null) {
                return "Not Registered User";
            } else {
                return StringUtils.join(ambiguousPatients(callLog.getLikelyPatientIds()), ", ");
            }
        } else {
            return callLog.patientId();
        }
    }

    private List<String> ambiguousPatients(List<String> likelyPatientIds) {
        List<String> patientIds = new ArrayList<String>();
        for (String likelyPatientId : likelyPatientIds) {
            patientIds.add(allPatients.get(likelyPatientId).getPatientId());
        }
        return patientIds;
    }

    private String getCallLogFlows(CallLog callLog) {
        List<CallLogView> callLogViews = callLogViewMapper.toCallLogView(Arrays.asList(callLog));
        return callLogViews.get(0).getFlows();
    }

    private String getTravelTimeToClinic(Patient patient) {
        return patient.getTravelTimeToClinicInDays() + " Days, " + patient.getTravelTimeToClinicInHours() + " Hours, And " + patient.getTravelTimeToClinicInMinutes() + " Minutes.";
    }


}