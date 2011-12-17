package org.motechproject.tamadatasetup.service;

import com.gargoylesoftware.htmlunit.WebResponse;
import org.joda.time.DateTime;
import org.motechproject.deliverytools.kookoo.QueryParams;
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
        String url = TamaUrl.baseFor("motech-delivery-tools/datetime/update");
        QueryParams queryParams = new QueryParams().put("date", dateTime.toString("yyyy-MM-dd")).put("hour", dateTime.getHourOfDay()).put("minute", dateTime.getMinuteOfHour());
        WebResponse response = webClient.getWebResponse(url, queryParams);
        assertEquals(200, response.getStatusCode());
    }
}
