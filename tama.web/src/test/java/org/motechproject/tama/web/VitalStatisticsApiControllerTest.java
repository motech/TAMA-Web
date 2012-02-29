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
import static junit.framework.Assert.assertFalse;
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
        LocalDate day3 = DateUtil.newDate(2012, 3, 27);
        VitalStatistics vitalStatistics_1 = new VitalStatisticsBuilder().withWeight(86).withCaptureDate(day1).build();
        VitalStatistics vitalStatistics_2 = new VitalStatisticsBuilder().withWeight(90).withCaptureDate(day2).build();
        VitalStatistics vitalStatistics_3 = new VitalStatisticsBuilder().withCaptureDate(day3).build();

        when(vitalStatisticsService.getAllFor("patientId", 36)).thenReturn(Arrays.asList(vitalStatistics_1, vitalStatistics_2, vitalStatistics_3));

        String weightOverTime = vitalStatisticsApiController.listWeightOverTime("patientId", 36);

        JSONArray expectedResult = new JSONArray();
        expectedResult.put(new JSONObject().put("value", 86).put("date", "23/01/2012"));
        expectedResult.put(new JSONObject().put("value", 90).put("date", "13/02/2012"));

        assertEquals(expectedResult.toString(), weightOverTime);
        assertFalse(weightOverTime.contains("27/03/2012"));
    }

    @Test
    public void shouldReturnBPOverTime() throws JSONException {
        LocalDate day1 = DateUtil.newDate(2012, 1, 23);
        LocalDate day2 = DateUtil.newDate(2012, 2, 13);
        LocalDate day3 = DateUtil.newDate(2012, 3, 27);
        VitalStatistics vitalStatistics_1 = new VitalStatisticsBuilder().withSystolicBp(20).withDiastolicBp(30).withCaptureDate(day1).build();
        VitalStatistics vitalStatistics_2 = new VitalStatisticsBuilder().withSystolicBp(50).withDiastolicBp(40).withCaptureDate(day2).build();
        VitalStatistics vitalStatistics_3 = new VitalStatisticsBuilder().withSystolicBp(80).withCaptureDate(day3).build();
        VitalStatistics vitalStatistics_4 = new VitalStatisticsBuilder().withCaptureDate(day3).build();

        when(vitalStatisticsService.getAllFor("patientId", 36)).thenReturn(Arrays.asList(vitalStatistics_1, vitalStatistics_2, vitalStatistics_3, vitalStatistics_4));

        String bpOverTime = vitalStatisticsApiController.listBPOverTime("patientId", 36);

        JSONArray expectedResult = new JSONArray();
        expectedResult.put(new JSONObject().put("date", "23/01/2012").put("systolic", 20).put("diastolic", 30));
        expectedResult.put(new JSONObject().put("date", "13/02/2012").put("systolic", 50).put("diastolic", 40));
        expectedResult.put(new JSONObject().put("date", "27/03/2012").put("systolic", 80));

        assertEquals(expectedResult.toString(), bpOverTime);
    }
}
