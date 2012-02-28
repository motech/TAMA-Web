package org.motechproject.tama.web;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.patient.builder.VitalStatisticsBuilder;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.service.VitalStatisticsService;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class VitalStatisticsApiControllerTest {

    private VitalStatisticsService vitalStatisticsService;
    private VitalStatisticsApiController vitalStatisticsApiController;

    @Before
    public void setUp(){
        vitalStatisticsService = mock(VitalStatisticsService.class);
        vitalStatisticsApiController = new VitalStatisticsApiController(vitalStatisticsService);
    }

    @Test
    public void shouldReturnWeightOverTime() throws JSONException {
        LocalDate day1 = DateUtil.newDate(2012, 1, 23);
        LocalDate day2 = DateUtil.newDate(2012, 2, 13);
        VitalStatistics vitalStatistics_1 = new VitalStatisticsBuilder().withDefaults().withWeight(86).withCaptureDate(day1).build();
        VitalStatistics vitalStatistics_2 = new VitalStatisticsBuilder().withDefaults().withWeight(90).withCaptureDate(day2).build();

        when(vitalStatisticsService.getAllFor("patientId", 36)).thenReturn(Arrays.asList(vitalStatistics_1, vitalStatistics_2));

        String weightOverTime = vitalStatisticsApiController.listWeightOverTime("patientId", 36);

        JSONArray expectedResult = new JSONArray();
        expectedResult.put(new JSONObject().put("value", 86).put("date", "23/01/2012"));
        expectedResult.put(new JSONObject().put("value", 90).put("date", "13/02/2012"));

        assertEquals(expectedResult.toString(), weightOverTime);
    }
}
