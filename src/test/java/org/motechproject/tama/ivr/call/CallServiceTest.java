package org.motechproject.tama.ivr.call;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallServiceTest {
    private CallService callService;
    private Properties properties;
    private String phoneNumber;
    @Mock
    private HttpClient httpClient;

    @Before
    public void setUp() {
        initMocks(this);
        phoneNumber = "9876543211";
        properties = new Properties();
        properties.setProperty(CallService.KOOKOO_OUTBOUND_URL, "http://kookoo/outbound.php");
        properties.setProperty(CallService.KOOKOO_API_KEY, "KKbedce53758c2e0b0e9eed7191ec2a466");
        properties.setProperty(CallService.APPLICATION_URL, "http://localhost/tama/ivr/reply");
        callService = new CallService(properties, httpClient);
    }

    @Test
    public void shouldMakeACallWithThePhoneNumberAndEmptyTamaDataParamsProvided() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        callService.dial(phoneNumber, params);
        verify(httpClient).executeMethod(argThat(new GetMethodMatcher("http://kookoo/outbound.php?api_key=KKbedce53758c2e0b0e9eed7191ec2a466&url=http://localhost/tama/ivr/reply?tamaData={}&phone_no=9876543211")));
    }

    @Test
    public void shouldMakeACallWithPhoneNumberAndSomeTamaDataParams() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("hero", "batman");
        callService.dial(phoneNumber, params);
        verify(httpClient).executeMethod(argThat(new GetMethodMatcher("http://kookoo/outbound.php?api_key=KKbedce53758c2e0b0e9eed7191ec2a466&url=http://localhost/tama/ivr/reply?tamaData={\"hero\":\"batman\"}&phone_no=9876543211")));
    }


    public class GetMethodMatcher extends ArgumentMatcher<GetMethod> {
        private String url;

        public GetMethodMatcher(String url) {
            this.url = url;
        }

        @Override
        public boolean matches(Object o) {
            GetMethod getMethod = (GetMethod) o;
            try {
                return getMethod.getURI().getURI().equals(url);
            } catch (URIException e) {
                return false;
            }
        }
    }


}
