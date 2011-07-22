package org.motechproject.tama.ivr.action;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class IVROutgoingCall {
    public static final String KOOKOO_OUTBOUND_URL = "kookoo.outbound.url";
    public static final String KOOKOO_API_KEY = "kookoo.api.key";
    public static final String APPLICATION_URL = "application.url";

    private String phoneNumber;

    @Autowired
    public IVROutgoingCall(@Qualifier("ivrProperties")Properties properties) {
        this.properties = properties;
        this.client = new HttpClient();
    }

    public IVROutgoingCall() {
    }

    private Properties properties;
    private HttpClient client;

    public void makeCall(String phoneNumber) {
        GetMethod getMethod = new GetMethod(properties.get(KOOKOO_OUTBOUND_URL).toString());
        getMethod.setQueryString(new NameValuePair[]{
                new NameValuePair("api_key", properties.get(KOOKOO_API_KEY).toString()),
                new NameValuePair("url", properties.get(APPLICATION_URL).toString()),
                new NameValuePair("phone_no", phoneNumber)
        });
        try {
            client.executeMethod(getMethod);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
