package org.motechproject.tamafunctional.framework;

import com.gargoylesoftware.htmlunit.WebResponse;
import org.motechproject.deliverytools.kookoo.QueryParams;
import org.motechproject.util.SerializationUtil;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

public class ScheduledTaskRunner {
    private MyWebClient webClient;

    public ScheduledTaskRunner(MyWebClient webClient) {
        this.webClient = webClient;
    }

    public void trigger(Class handlerClass, String handlerMethod, HashMap<String, Object> eventParams) {
        String serializedEventParams = SerializationUtil.toString(eventParams);
        QueryParams queryParams = new QueryParams().
                put("className", handlerClass.getSimpleName()).
                put("methodName", handlerMethod).
                put("jobData", serializedEventParams);

        WebResponse webResponse = webClient.getWebResponse(TamaUrl.baseFor("motech-delivery-tools/jobhandler/invoke"), queryParams);
        assertEquals(200, webResponse.getStatusCode());
    }
}
