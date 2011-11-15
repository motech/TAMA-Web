package org.motechproject.tamafunctional.framework;

import com.gargoylesoftware.htmlunit.WebResponse;
import org.json.JSONObject;
import org.motechproject.deliverytools.kookoo.QueryParams;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

public class ScheduledTaskRunner {
    private MyWebClient webClient;

    public ScheduledTaskRunner(MyWebClient webClient) {
        this.webClient = webClient;
    }

    public void trigger(Class handlerClass, String handlerMethod, HashMap<String, Object> eventParams) {
        QueryParams queryParams = new QueryParams().
                put("className", handlerClass.getSimpleName()).
                put("methodName", handlerMethod).
                put("jobData", new JSONObject(eventParams).toString());

        WebResponse webResponse = webClient.getWebResponse(TamaUrl.baseFor("motech-delivery-tools/jobhandler/invoke"), queryParams);
        assertEquals(200, webResponse.getStatusCode());
    }
}
