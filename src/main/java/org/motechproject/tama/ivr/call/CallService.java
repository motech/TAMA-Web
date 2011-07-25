package org.motechproject.tama.ivr.call;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Service
public class CallService {
    public static final String KOOKOO_OUTBOUND_URL = "kookoo.outbound.url";
    public static final String KOOKOO_API_KEY = "kookoo.api.key";
    public static final String APPLICATION_URL = "application.url";

    @Autowired
    @Qualifier("ivrProperties")
    private Properties properties;
    private HttpClient httpClient = new HttpClient();

    public CallService() {
    }

    public CallService(Properties properties, HttpClient httpClient) {
        this.properties = properties;
        this.httpClient = httpClient;
    }

    public void call(String phoneNumber, Map<String, String> params) {
        try {
            GetMethod getMethod = new GetMethod(properties.get(KOOKOO_OUTBOUND_URL).toString());

            JSONObject json = new JSONObject();
            for (String key : params.keySet()) json.put(key, params.get(key));
            String applicationUrl = properties.get(APPLICATION_URL) + "?tamaData=" + json.toString();

            getMethod.setQueryString(new NameValuePair[]{
                    new NameValuePair("api_key", properties.get(KOOKOO_API_KEY).toString()),
                    new NameValuePair("url", applicationUrl),
                    new NameValuePair("phone_no", phoneNumber)
            });

            httpClient.executeMethod(getMethod);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


}
