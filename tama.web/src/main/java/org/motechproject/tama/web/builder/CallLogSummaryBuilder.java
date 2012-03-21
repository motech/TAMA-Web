package org.motechproject.tama.web.builder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import org.motechproject.tama.web.view.CallLogView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CallLogSummaryBuilder {

    private static final String CALL_LOG_DATETIME_FORMAT = TAMAConstants.DATETIME_FORMAT + ":ss";
    public static final String NOT_REGISTERED_USER = "Not Registered User";
    public static final String TAMA = "TAMA";

    private AllPatients allPatients;
    private Patients allLoadedPatients = new Patients();
    private Clinics allLoadedClinics = new Clinics();
    private IVRLanguages allLoadedIVRLanguages = new IVRLanguages();

    public CallLogSummaryBuilder(AllPatients allPatients, AllClinics allClinics, AllIVRLanguages allIVRLanguages) {
        this.allPatients = allPatients;
        this.allLoadedPatients = new Patients(allPatients.getAll());
        this.allLoadedClinics = new Clinics(allClinics.getAll());
        this.allLoadedIVRLanguages = new IVRLanguages(allIVRLanguages.getAll());
    }

    public CallLogSummary build(CallLog callLog) {
        CallDirection callDirection = callLog.getCallDirection();
        boolean isInboundCall = callDirection == CallDirection.Inbound;
        return new CallLogSummary(getPatientId(callLog),
                getSourcePhoneNumber(callLog, isInboundCall),
                getDestinationPhoneNumber(callLog, isInboundCall),
                callLog.getStartTime().toString(CALL_LOG_DATETIME_FORMAT),
                callLog.getEndTime().toString(CALL_LOG_DATETIME_FORMAT),
                getClinicId(callLog),
                getCallLanguage(callLog),
                getTravelTimeToClinic(callLog),
                getCallLogFlows(callLog)
        );
    }

    private String getPatientId(CallLog callLog) {
        if (StringUtils.isEmpty(callLog.patientId())) {
            if (CollectionUtils.isEmpty(callLog.getLikelyPatientIds())) {
                return NOT_REGISTERED_USER;
            } else {
                return StringUtils.join(ambiguousPatients(callLog.getLikelyPatientIds()), ", ");
            }
        } else {
            return callLog.patientId();
        }
    }

    private String getClinicId(CallLog callLog) {
        if (StringUtils.isEmpty(callLog.clinicId())) {
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
        if (StringUtils.isEmpty(callLog.patientId())) return " - ";
        Patient patient = allLoadedPatients.getBy(callLog.getPatientDocumentId());
        return patient.getTravelTimeToClinicInDays() + " Days, " + patient.getTravelTimeToClinicInHours() + " Hours, and " + patient.getTravelTimeToClinicInMinutes() + " Minutes";
    }

    private String getCallLanguage(CallLog callLog) {
        return StringUtils.isEmpty(callLog.callLanguage()) ? " - " : allLoadedIVRLanguages.getBy(callLog.callLanguage()).getName();
    }

    private String getCallLogFlows(CallLog callLog) {
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
        if (StringUtils.isEmpty(callLog.patientId()) && CollectionUtils.isEmpty(callLog.getLikelyPatientIds())) {
            return NOT_REGISTERED_USER + " : " + callLog.getPhoneNumber();
        } else {
            return callLog.getPhoneNumber();
        }
    }
}