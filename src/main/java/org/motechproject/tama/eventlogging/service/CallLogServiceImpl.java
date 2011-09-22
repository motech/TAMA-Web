package org.motechproject.tama.eventlogging.service;

import org.motechproject.ivr.IVRCallEvent;
import org.motechproject.ivr.eventlogging.EventLogConstants;
import org.motechproject.tama.eventlogging.domain.CallDetail;
import org.motechproject.tama.eventlogging.repository.AllCallDetails;
import org.motechproject.tama.eventlogging.domain.CallDetailUnit;
import org.motechproject.tama.util.TamaSessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CallLogServiceImpl implements CallLogService {
    public static final String CALL_TYPE_SYMPTOM_REPORTING = "Symptom Reporting";
    public static final String CALL_TYPE_PILL_REMINDER = "Pill Reminder";
    private final AllCallDetails allCallDetails;

    @Autowired
    public CallLogServiceImpl(AllCallDetails allCallDetails) {
        this.allCallDetails = allCallDetails;
    }

    @Override
    public void create(IVRCallEvent ivrCallEvent) {
        String callId = ivrCallEvent.getCallId();
        CallDetail callDetail = allCallDetails.getByCallId(callId);
        CallDetailUnit callDetailUnit = new CallDetailUnit(ivrCallEvent);

        if (callDetail != null) {
            callDetail.add(callDetailUnit);
            allCallDetails.update(callDetail);
            return;
        }

        Map<String, String> requestParams = ivrCallEvent.getRequestParams();
        String isSymptomsReporting = requestParams.get(TamaSessionUtil.TamaSessionAttribute.SYMPTOMS_REPORTING_PARAM);
        String callType = "true".equals(isSymptomsReporting) ? CALL_TYPE_SYMPTOM_REPORTING : CALL_TYPE_PILL_REMINDER;

        Map<String, String> ivrCallEventData = ivrCallEvent.getData();
        String callDirection = ivrCallEventData.get(EventLogConstants.CALL_DIRECTION);
        String patientPhoneNo = ivrCallEventData.get(EventLogConstants.CALLER_ID);
        String patientDocId = ivrCallEvent.getExternalID();

        callDetail = new CallDetail(callId, callType, callDirection, patientDocId, patientPhoneNo);
        callDetail.add(callDetailUnit);
        allCallDetails.add(callDetail);
    }
}