package org.motechproject.tamafunctional.ivr;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.motechproject.deliverytools.kookoo.IncomingCall;
import org.motechproject.deliverytools.kookoo.QueryParams;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.tamafunctional.framework.FunctionalTestObject;
import org.motechproject.tamafunctional.framework.KooKooResponseParser;
import org.motechproject.tamafunctional.framework.MyWebClient;
import org.motechproject.tamafunctional.framework.TamaUrl;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdata.ivrrequest.CallInfo;
import org.motechproject.tamafunctional.testdata.ivrrequest.NoCallInfo;
import org.motechproject.tamafunctional.testdata.ivrrequest.OutgoingCallInfo;

import java.io.IOException;
import java.util.Set;

import static junit.framework.Assert.assertNotNull;

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

    private Caller(MyWebClient webClient) {
        this("irrelevant", null, webClient);
    }

    public IVRResponse call() {
        QueryParams queryParams = new QueryParams().put("sid", sid).put("cid", phoneNumber).put("event", IVREvent.NewCall);
        return invokeAndGetResponse(queryParams);
    }

    private QueryParams paramsFor(IVREvent event, String sid, String data) {
        QueryParams queryParams = new QueryParams().put("sid", sid).put("event", event).put("data", data);
        callInfo.appendDataMapTo(queryParams);
        return queryParams;
    }

    private QueryParams paramsFor(IVREvent event, String data) {
        return paramsFor(event, sid, data);
    }

    private IVRResponse invokeAndGetResponse(QueryParams params) {
        return KooKooResponseParser.fromXml(invoke(params));
    }

    private String invoke(QueryParams params) {
        return webClient.getResponse(TamaUrl.ivrURL(), params).toLowerCase();
    }

    public IVRResponse replyToCall(CallInfo callInfo) {
        this.callInfo = callInfo;
        QueryParams params = new QueryParams().put("sid", sid).put("cid", phoneNumber).put("event", IVREvent.NewCall).put("dataMap", callInfo.asString());
        return invokeAndGetResponse(params);
    }

    public IVRResponse enter(String number) {
        IVRResponse ivrResponse = invokeAndGetResponse(paramsFor(IVREvent.GotDTMF, number));
        if (ivrResponse.audiosPlayed().isEmpty() && ivrResponse.isEmpty()) {
            ivrResponse = listenMore();
        }
        return ivrResponse;
    }

    public IVRResponse listenMore() {
        return invokeAndGetResponse(callInfo.appendDataMapTo(new QueryParams()));
    }

    public IVRResponse answered() {
        return invokeAndGetResponse(paramsFor(IVREvent.Dial, "").put("status", "answered"));
    }

    public IVRResponse notAnswered() {
        return invokeAndGetResponse(paramsFor(IVREvent.Dial, "").put("status", "not_answered"));
    }

    public void hangup() {
        if (hangedUp) return;
        IVRResponse ivrResponse = invokeAndGetResponse(paramsFor(IVREvent.Hangup, ""));
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

    public static Caller receiveCall(MyWebClient webClient, int waitInSeconds) throws IOException {
        String incomingCallRequest = incomingCall(webClient, waitInSeconds);
        Caller caller = new Caller(webClient);
        caller.logInfo("Incoming call: %s", incomingCallRequest);
        IncomingCall incomingCall = new IncomingCall(incomingCallRequest);
        assertNotNull(incomingCall.apiKey());
        assertNotNull(incomingCall.phoneNumber());

        caller.phoneNumber = incomingCall.phoneNumber();
        caller.replyToCall(new OutgoingCallInfo(incomingCall.customParams()));
        return caller;
    }

    public static boolean gotCall(MyWebClient webClient, int waitInSeconds) {
        String response = incomingCall(webClient, waitInSeconds);
        return StringUtils.isNotEmpty(response);
    }

    private static String incomingCall(MyWebClient webClient, int waitInSeconds) {
        WebResponse webResponse = webClient.getWebResponse(TamaUrl.baseFor("motech-delivery-tools/outbound/lastreceived"), new QueryParams().put("waitForSeconds", waitInSeconds));
        return webResponse.getContentAsString();
    }
}