package org.motechproject.tama.ivr.call;

import org.motechproject.server.service.ivr.CallRequest;
import org.motechproject.server.service.ivr.IVRService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class IvrCall {
    public static final String APPLICATION_URL = "application.url";
    @Autowired
    protected AllPatients allPatients;
    @Autowired
    protected IVRService ivrService;
    @Autowired
    @Qualifier("ivrProperties")
    private Properties properties;

    public IvrCall(Properties properties) {
        this.properties = properties;
    }

    public IvrCall(AllPatients allPatients, IVRService ivrService, Properties properties) {
        this.allPatients = allPatients;
        this.ivrService = ivrService;
        this.properties = properties;
    }

    public void makeCall(String patientDocId) {
        makeCall(patientDocId, new HashMap<String, String>());
    }

    public void makeCall(String patientDocId, Map<String, String> params) {
        Patient patient = allPatients.get(patientDocId);
        if (patient == null || patient.isNotActive()) return;

        CallRequest callRequest = new CallRequest(StringUtil.ivrMobilePhoneNumber(patient.getMobilePhoneNumber()), params, getApplicationUrl());
        ivrService.initiateCall(callRequest);
    }

    protected String getApplicationUrl() {
        return (String) properties.get(APPLICATION_URL);
    }
}