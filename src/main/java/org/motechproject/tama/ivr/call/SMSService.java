package org.motechproject.tama.ivr.call;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SMSService {
    public static final String KOOKOO_SMS_URL = "http://www.kookoo.in/outbound/outbound_sms.php";
    public static final String KOOKOO_API_KEY = "KKbedce53758c2e0b0e9eed7191ec2a466";

    private HttpClient httpClient = new HttpClient();

    public SMSService() {
    }

    public SMSService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void sendSMS(String phoneNumber, String message) {
        try {
            GetMethod getMethod = new GetMethod(KOOKOO_SMS_URL);
            getMethod.setQueryString(new NameValuePair[]{
                    new NameValuePair("api_key", KOOKOO_API_KEY),
                    new NameValuePair("phone_no", phoneNumber),
                    new NameValuePair("message", message)
            });
            httpClient.executeMethod(getMethod);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
