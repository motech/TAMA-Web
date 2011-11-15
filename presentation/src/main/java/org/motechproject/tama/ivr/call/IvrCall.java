package org.motechproject.tama.ivr.call;

import org.motechproject.server.service.ivr.CallRequest;
import org.motechproject.server.service.ivr.IVRService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class IvrCall {
    public static final String APPLICATION_URL = "application.url";
    @Autowired
    protected IVRService ivrService;
    @Autowired
    @Qualifier("ivrProperties")
    private Properties properties;

    public IvrCall(IVRService ivrService, Properties properties) {
        this.ivrService = ivrService;
        this.properties = properties;
    }

    public void makeCall(Patient patient) {
        makeCall(patient, new HashMap<String, String>());
    }

    public void makeCall(Patient patient, Map<String, String> params) {
        CallRequest callRequest = new CallRequest(StringUtil.ivrMobilePhoneNumber(patient.getMobilePhoneNumber()), params, getApplicationUrl());
        ivrService.initiateCall(callRequest);
    }

    protected String getApplicationUrl() {
        return (String) properties.get(APPLICATION_URL);
    }
}