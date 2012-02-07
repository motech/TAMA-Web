package org.motechproject.tamafunctionalframework.framework;

import com.gargoylesoftware.htmlunit.WebResponse;
import org.motechproject.deliverytools.kookoo.QueryParams;

import static junit.framework.Assert.assertEquals;

public class ScheduledTaskManager {
    private MyWebClient webClient;

    public static final String BASE_SCHEDULER_INVOKER_URL = "motech-delivery-tools/jobhandler/";

    public ScheduledTaskManager(MyWebClient webClient) {
        this.webClient = webClient;
    }

    public void trigger(Class handlerClass, String handlerMethod, String jobId) {
        QueryParams queryParams = new QueryParams().
                put("className", handlerClass.getSimpleName()).
                put("methodName", handlerMethod).
                put("jobId", jobId);

        WebResponse webResponse = webClient.getWebResponse(TamaUrl.baseFor(BASE_SCHEDULER_INVOKER_URL + "invoke"), queryParams);
        assertEquals(200, webResponse.getStatusCode());
    }

    public boolean exists(Class handlerClass, String handlerMethod, String jobId) {
        QueryParams queryParams = new QueryParams().
                put("className", handlerClass.getSimpleName()).
                put("methodName", handlerMethod).
                put("jobId", jobId);

        WebResponse webResponse = webClient.getWebResponse(TamaUrl.baseFor(BASE_SCHEDULER_INVOKER_URL + "exists"), queryParams);
        assertEquals(200, webResponse.getStatusCode());
        return "true".equals(webResponse.getContentAsString());
    }



    public void clear() {
        webClient.getWebResponse(TamaUrl.baseFor(BASE_SCHEDULER_INVOKER_URL + "clear"), new QueryParams());
    }
}
