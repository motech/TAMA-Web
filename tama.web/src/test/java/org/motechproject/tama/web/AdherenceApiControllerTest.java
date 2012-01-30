package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class AdherenceApiControllerTest {

    private DailyPillReminderAdherenceService dailyPillReminderAdherenceService;

    private AdherenceApiController adherenceApiController;

    @Before
    public void setUp() {
        dailyPillReminderAdherenceService = mock(DailyPillReminderAdherenceService.class);
        adherenceApiController = new AdherenceApiController(dailyPillReminderAdherenceService);
    }

    @Test
    public void shouldListAdherencePerWeekForThePatient() throws JSONException {
        Map<DateTime, Double> adherencePerWeek = new HashMap<DateTime, Double>();
        DateTime monday = DateUtil.newDateTime(new LocalDate(2012, 01, 02), 0, 0, 0);
        adherencePerWeek.put(monday, (Double) 80.3);
        adherencePerWeek.put(monday.plusDays(7), 90.4);
        when(dailyPillReminderAdherenceService.getAdherenceOverTime("patientDocId")).thenReturn(adherencePerWeek);

        String response = adherenceApiController.list("patientDocId");

        JSONObject resultAsJsonObject = new JSONObject(response);
        JSONArray adherencePerWeekResponse = resultAsJsonObject.getJSONArray("adherencePerWeek");

        JSONObject adherenceForFirstWeek = adherencePerWeekResponse.getJSONObject(0);
        assertEquals(monday.toLocalDate().toString(), adherenceForFirstWeek.get("date").toString());
        assertEquals(80.3, adherenceForFirstWeek.get("percentage"));

        JSONObject adherenceForSecondWeek = adherencePerWeekResponse.getJSONObject(1);
        assertEquals(monday.plusDays(7).toLocalDate().toString(), adherenceForSecondWeek.get("date").toString());
        assertEquals(90.4, adherenceForSecondWeek.get("percentage"));
    }
}
