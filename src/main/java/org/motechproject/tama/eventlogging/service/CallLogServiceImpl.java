package org.motechproject.tama.eventlogging.service;

import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.eventlogging.domain.CallLog;
import org.motechproject.tama.eventlogging.repository.AllCallDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallLogServiceImpl implements CallLogService {

    private final AllCallDetails allCallDetails;

    @Autowired
    public CallLogServiceImpl(AllCallDetails allCallDetails) {
        this.allCallDetails = allCallDetails;
    }

    @Override
    public CallLog create(String callType, Patient patient) {
        CallLog callLog = new CallLog(callType, patient.getPatientId());
        allCallDetails.add(callLog);
        return callLog;

//        String callId = ivrCallEvent.getCallId();
//        CallDetail callDetail = allCallDetails.getByCallId(callId);
//        CallDetailUnit callDetailUnit = new CallDetailUnit(ivrCallEvent);
//
//        if (callDetail != null) {
//            callDetail.add(callDetailUnit);
//            allCallDetails.update(callDetail);
//            return;
//        }
//
//
//        Map<String, String> ivrCallEventData = ivrCallEvent.getData();
//        String callDirection = ivrCallEventData.get(EventLogConstants.CALL_DIRECTION);
//        String patientPhoneNo = ivrCallEventData.get(EventLogConstants.CALLER_ID);
//        String patientDocId = ivrCallEvent.getExternalID();
//
//        callDetail = new CallDetail(callId, callType, callDirection, patientDocId, patientPhoneNo);
//        callDetail.add(callDetailUnit);
//        allCallDetails.add(callDetail);
    }
}