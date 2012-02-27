package org.motechproject.tama.ivr.call;

import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.tama.common.util.StringUtil;
import org.motechproject.tama.patient.domain.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class IVRCall {
    public static final String APPLICATION_URL = "application.url";
    protected IVRService ivrService;
    private Properties properties;

    @Autowired
    public IVRCall(IVRService ivrService, @Qualifier("ivrProperties") Properties properties) {
        this.ivrService = ivrService;
        this.properties = properties;
    }

    public void makeCall(Patient patient, String callType, Map<String, String> params) {
        params.put(IVRService.EXTERNAL_ID, patient.getId());
        params.put(IVRService.CALL_TYPE, callType);
        CallRequest callRequest = new CallRequest(StringUtil.ivrMobilePhoneNumber(patient.getMobilePhoneNumber()), params, getApplicationUrl());
        ivrService.initiateCall(callRequest);
    }

    public String getApplicationUrl() {
        return (String) properties.get(APPLICATION_URL);
    }
}