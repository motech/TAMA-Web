package org.motechproject.tama.ivr.outbound;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

@Service
public class OutboundCallService {
    public static final String KOOKOO_OUTBOUND_URL = "kookoo.outbound.url";
    public static final String KOOKOO_API_KEY = "kookoo.api.key";
    public static final String APPLICATION_URL = "application.url";

    @Autowired
    @Qualifier("ivrProperties")
    private Properties properties;
    @Autowired
    private Patients patients;
    private HttpClient client;

    public OutboundCallService() {
        client = new HttpClient();
    }

    public OutboundCallService(Patients patients, HttpClient client, Properties properties) {
        this.patients = patients;
        this.client = client;
        this.properties = properties;
    }

    public void call(String patientDocId) {
        Patient patient = patients.get(patientDocId);
        if (patient == null || patient.isNotActive()) return;

        GetMethod getMethod = new GetMethod(properties.get(KOOKOO_OUTBOUND_URL).toString());
        getMethod.setQueryString(new NameValuePair[]{
                new NameValuePair("api_key", properties.get(KOOKOO_API_KEY).toString()),
                new NameValuePair("url", properties.get(APPLICATION_URL).toString()),
                new NameValuePair("phone_no", patient.getIVRMobilePhoneNumber())
        });
        try {
            client.executeMethod(getMethod);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
