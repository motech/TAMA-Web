package org.motechproject.tamafunctional.ivr;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import org.motechproject.tamafunctional.framework.KooKooResponseParser;
import org.motechproject.tamafunctional.framework.MyWebClient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.apache.commons.lang.StringUtils.replace;

public class Caller {
    private String sid;
    private String phoneNumber;
    private MyWebClient webClient;

    public Caller(String sid, String phoneNumber, MyWebClient webClient) {
        this.sid = sid;
        this.phoneNumber = phoneNumber;
        this.webClient = webClient;
    }

    public IVRResponse call() throws IOException {
        Page page = webClient.getPage(urlWith(sid, phoneNumber, "NewCall"));
        WebResponse webResponse = page.getWebResponse();
        return KooKooResponseParser.fromXml(webResponse.getContentAsString().toLowerCase());
    }

    protected String urlWith(String sid, String cid, String event) {
        return String.format("http://localhost:%s/tama/ivr/reply?sid=%s&cid=%s&event=%s", System.getProperty("jetty.port", "8080"), sid, cid, event);
    }

    protected String urlWith(String sid, String cid, String event, String data) {
        return String.format("%s&testdata={%s}", urlWith(sid, cid, event), data);
    }

    public IVRResponse enter(String number) {
        Page page = webClient.getPage(urlWith(sid, phoneNumber, "GotDTMF", number));
        return KooKooResponseParser.fromXml(page.getWebResponse().getContentAsString());
    }
}
