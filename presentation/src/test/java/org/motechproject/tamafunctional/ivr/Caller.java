package org.motechproject.tamafunctional.ivr;

import com.gargoylesoftware.htmlunit.util.Cookie;
import org.junit.Assert;
import org.motechproject.server.service.ivr.IVREvent;
import org.motechproject.tamafunctional.framework.FunctionalTestObject;
import org.motechproject.tamafunctional.framework.KooKooResponseParser;
import org.motechproject.tamafunctional.framework.MyWebClient;
import org.motechproject.tamafunctional.framework.TamaUrl;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdata.ivrrequest.CallInfo;
import org.motechproject.tamafunctional.testdata.ivrrequest.NoCallInfo;

import java.util.Set;

//TODO Probably we need a URL builder to not duplicate URLs
public class Caller extends FunctionalTestObject {
    private String sid;
    private String phoneNumber;
    private MyWebClient webClient;
    private CallInfo callInfo = new NoCallInfo();
    private boolean hangedUp;

    public Caller(String sid, String phoneNumber, MyWebClient webClient) {
        this.sid = sid;
        this.phoneNumber = phoneNumber;
        this.webClient = webClient;
    }

    public IVRResponse call() {
        return invokeAndGetResponse(String.format("%s/reply?sid=%s&cid=%s&event=%s", TamaUrl.ivrURL(), sid, phoneNumber, IVREvent.NewCall.toString()));
    }

    private String urlFor(IVREvent event, String sid, String data) {
        String url = String.format("%s/reply?sid=%s&event=%s&data=%s", TamaUrl.ivrURL(), sid, event.toString(), data);
        return callInfo.appendDataMapTo(url);
    }

    private String urlFor(IVREvent event, String data) {
        return urlFor(event, sid, data);
    }

    private IVRResponse invokeAndGetResponse(String completeUrl) {
        return KooKooResponseParser.fromXml(invoke(completeUrl));
    }

    private String invoke(String completeUrl) {
        return webClient.getResponse(completeUrl).toLowerCase();
    }

    public IVRResponse replyToCall(CallInfo callInfo) {
        this.callInfo = callInfo.outgoingCall();
        String completeUrl = String.format("%s/reply?sid=%s&cid=%s&event=%s&dataMap=%s", TamaUrl.ivrURL(), sid, phoneNumber, "NewCall", callInfo.asQueryParameter());
        return invokeAndGetResponse(completeUrl);
    }

    public IVRResponse enter(String number) {
        IVRResponse ivrResponse = invokeAndGetResponse(urlFor(IVREvent.GotDTMF, number));
        if (ivrResponse.audiosPlayed().isEmpty() && ivrResponse.isEmpty()) {
            ivrResponse = listenMore();
        }
        return ivrResponse;
    }

    public IVRResponse listenMore() {
        String url = String.format("%s/reply?", TamaUrl.ivrURL());
        return invokeAndGetResponse(callInfo.appendDataMapTo(url));
    }

    public void hangup() {
        if (hangedUp) return;
        IVRResponse ivrResponse = invokeAndGetResponse(urlFor(IVREvent.Hangup, ""));
        hangedUp = true;
        webClient.clearCookies();
        Assert.assertNotNull(ivrResponse);
    }

    public void logCookies() {
        StringBuilder buffer = new StringBuilder("Cookies {");
        Set<Cookie> cookies = webClient.cookies();
        for (Cookie cookie : cookies) {
            buffer.append(String.format("%s=%s|", cookie.getName(), cookie.getValue()));

        }
        logger.info(buffer.append("}").toString());
    }

    public void tearDown() {
        hangedUp = false;
    }
}