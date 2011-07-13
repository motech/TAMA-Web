package org.motechproject.tama.ivr.action;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.builder.IVRDtmfBuilder;
import org.motechproject.tama.ivr.builder.IVRResponseBuilder;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DTMFAction implements IVRAction {
    @Autowired
    private Patients patients;
    @Autowired
    private IVRMessage messages;

    @Value("#{ivrProperties['MAX_NUM_OF_IVR_RETRIES']}")
    private Integer maximumNumberOfRetries;

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        IVR.CallState callState = (IVR.CallState) session.getAttribute(IVR.Attributes.CALL_STATE);
        if (!callState.isCollectPin()) return StringUtils.EMPTY;

        String passcode = ivrRequest.getData();
        String mobileNumber = String.valueOf(session.getAttribute(IVR.Attributes.CALLER_ID));
        Patient patient = patients.findByMobileNumber(mobileNumber);

        if (isValid(patient, passcode)) {
            session.invalidate();
            session = request.getSession();
            session.setAttribute(IVR.Attributes.PATIENT_DOCUMENT_ID, patient.getId());
            session.setAttribute(IVR.Attributes.CALL_STATE, IVR.CallState.AUTH_SUCCESS);
            return dtmfResponseWith(ivrRequest, IVRMessage.Key.TAMA_IVR_RESPONSE_AFTER_AUTH);
        }

        int numberOfRetries = getNumberOfRetries(session);
        if (thisIsTheLastTry(numberOfRetries)) {
            session.invalidate();
            return hangupResponseWith(ivrRequest);
        }
        session.setAttribute(IVR.Attributes.NUMBER_OF_RETRIES, numberOfRetries + 1);
        return dtmfResponseWith(ivrRequest, IVRMessage.Key.TAMA_IVR_ASK_FOR_PIN_AFTER_FAILURE);
    }

    private String hangupResponseWith(IVRRequest ivrRequest) {
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withHangUp().create();
        return ivrResponse.getXML();
    }

    private String dtmfResponseWith(IVRRequest ivrRequest, IVRMessage.Key message) {
        CollectDtmf collectDtmf = new IVRDtmfBuilder().withPlayText(messages.get(message)).create();
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withCollectDtmf(collectDtmf).create();
        return ivrResponse.getXML();
    }

    private boolean isValid(Patient patient, String passcode) {
        return patient != null && patient.hasPasscode(passcode);
    }

    private Integer getNumberOfRetries(HttpSession session) {
        Object numOfRetriesAttrValue = session.getAttribute(IVR.Attributes.NUMBER_OF_RETRIES);
        return numOfRetriesAttrValue == null ? 0 : (Integer) numOfRetriesAttrValue;
    }

    private boolean thisIsTheLastTry(double noOfRetries) {
        return maximumNumberOfRetries.equals(String.valueOf(noOfRetries + 1));
    }

}
