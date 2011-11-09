package org.motechproject.tamadatasetup.service;

import com.gargoylesoftware.htmlunit.WebResponse;
import org.joda.time.DateTime;
import org.motechproject.tamafunctional.framework.FunctionalTestObject;
import org.motechproject.tamafunctional.framework.MyWebClient;
import org.motechproject.tamafunctional.framework.TamaUrl;

import static junit.framework.Assert.assertEquals;

public class TAMADateTimeService extends FunctionalTestObject {
    private MyWebClient webClient;

    public TAMADateTimeService(MyWebClient webClient) {
        this.webClient = webClient;
    }

    public void adjustDateTime(DateTime dateTime) {
        String resource = String.format("motech-delivery-tools/datetime/update?date=%s&hour=%s&minute=%s", dateTime.toString("yyyy-MM-dd"), dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
        String url = TamaUrl.baseFor(resource);
        logInfo("Invoking: %s", url);
        WebResponse response = webClient.getWebResponse(url);
        assertEquals(200, response.getStatusCode());
    }
}
