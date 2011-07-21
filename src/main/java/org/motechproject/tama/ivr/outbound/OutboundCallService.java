package org.motechproject.tama.ivr.outbound;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

@Service
public class OutboundCallService {
    public static final String KOOKOO_OUTBOUND_URL = "kookoo.outbound.url";
    public static final String KOOKOO_API_KEY = "kookoo.api.key";
    public static final String APPLICATION_URL = "application.url";

    private Properties properties;
    private Patients patients;
    private HttpClient client;

    @Autowired
    public OutboundCallService(Patients patients, @Qualifier("ivrProperties") Properties properties) {
        this(patients, new HttpClient(), properties);
    }

    public OutboundCallService(Patients patients, HttpClient client, Properties properties) {
        this.patients = patients;
        this.client = client;
        this.properties = properties;
    }

    @MotechListener(subjects = "org.motechproject.server.pillreminder.scheduler-reminder")
    public void handlePillReminderEvent(MotechEvent motechEvent) {
        call((String) motechEvent.getParameters().get("ExternalID"));
    }

    private void call(String patientDocId) {
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
