package org.motechproject.tama.ivr.call;

import org.motechproject.server.service.ivr.CallRequest;
import org.motechproject.server.service.ivr.IVRService;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;
import java.util.Properties;

public abstract class IvrCall {
    public static final String APPLICATION_URL = "application.url";
    @Autowired
    protected AllPatients allPatients;
    @Autowired
    protected IVRService ivrService;

    @Autowired
    @Qualifier("ivrProperties")
    private Properties properties;

    public IvrCall() {
    }

    protected IvrCall(AllPatients allPatients, IVRService ivrService) {
        this.allPatients = allPatients;
        this.ivrService = ivrService;
    }

    protected void makeCall(String patientDocId, Map<String, String> params) {
        Patient patient = allPatients.get(patientDocId);
        if (patient == null || patient.isNotActive()) return;

        CallRequest callRequest = new CallRequest(patient.getIVRMobilePhoneNumber(), params, getApplicationUrl());
        ivrService.initiateCall(callRequest);
    }

    protected String getApplicationUrl() {
        return (String) properties.get(APPLICATION_URL);
    }
}