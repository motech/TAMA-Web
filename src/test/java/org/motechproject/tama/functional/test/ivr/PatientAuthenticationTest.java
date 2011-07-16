package org.motechproject.tama.functional.test.ivr;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.functional.framework.MyWebClient;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class PatientAuthenticationTest extends BaseIVRTest {

    private MyWebClient webClient;

    @Before
    public void setUp() {
        webClient = new MyWebClient();
    }

    @Test
    public void shouldTestConversationForPatientWhoseNumberIsNotActivated() throws MalformedURLException {
        Page page = webClient.getPage(urlWith("123", "9876543210", "NewCall"));
        WebResponse webResponse = page.getWebResponse();
        String response = print(webResponse.getContentAsString());
        assertEquals("<response sid=\"123\"><collectdtmf><playaudio>http://119.82.102.200/tama.wav</playaudio></collectdtmf></response>", response);

        page = webClient.getPage(urlWith("123", "9876543210", "GotDTMF"));
        webResponse = page.getWebResponse();
    }
}

