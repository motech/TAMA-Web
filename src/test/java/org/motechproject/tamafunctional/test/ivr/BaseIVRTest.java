package org.motechproject.tamafunctional.test.ivr;


import org.apache.commons.lang.StringUtils;
import org.motechproject.tamafunctional.framework.BaseTest;

import static org.apache.commons.lang.StringUtils.replace;

public abstract class BaseIVRTest extends BaseTest{

    protected String urlWith(String sid, String cid, String event) {
        String url = "http://localhost:" + System.getProperty("jetty.port", "8080") + "/tama/ivr/reply?sid={sid}&cid={cid}&event={event}";
        url = replace(url, "{sid}", sid);
        url = replace(url, "{cid}", cid);
        url = replace(url, "{event}", event);
        return url;
    }

    protected String urlWith(String sid, String cid, String event, String data) {
        return urlWith(sid, cid, event) + "&data={" + data + "}";
    }

    protected String print(String response){
       return StringUtils.replace(response, System.getProperty("line.separator"), "");
    }
}
