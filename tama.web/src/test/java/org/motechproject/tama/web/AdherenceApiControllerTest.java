package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.common.domain.AdherenceSummaryForAWeek;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class AdherenceApiControllerTest {

    private DailyPillReminderAdherenceService dailyPillReminderAdherenceService;
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;

    private AdherenceApiController adherenceApiController;

    @Before
    public void setUp() {
        dailyPillReminderAdherenceService = mock(DailyPillReminderAdherenceService.class);
        fourDayRecallAdherenceService = mock(FourDayRecallAdherenceService.class);
        adherenceApiController = new AdherenceApiController(dailyPillReminderAdherenceService, fourDayRecallAdherenceService);
    }

    @Test
    public void shouldListDailyAdherencePerWeekForThePatient() throws JSONException, NoAdherenceRecordedException {
        AdherenceSummaryForAWeek adherenceSummaryForAWeek_1 = new AdherenceSummaryForAWeek();
        AdherenceSummaryForAWeek adherenceSummaryForAWeek_2 = new AdherenceSummaryForAWeek();
        DateTime monday = DateUtil.newDateTime(new LocalDate(2012, 01, 02), 0, 0, 0);
        adherenceSummaryForAWeek_1.setWeekStartDate(monday).setPercentage(80.0).setTaken(8).setTotal(10);
        adherenceSummaryForAWeek_2.setWeekStartDate(monday.plusDays(7)).setPercentage(90.0).setTaken(9).setTotal(10);

        when(dailyPillReminderAdherenceService.getAdherenceOverTime("patientDocId")).thenReturn(Arrays.asList(adherenceSummaryForAWeek_1, adherenceSummaryForAWeek_2));
        when(fourDayRecallAdherenceService.getAdherenceOverTime("patientDocId")).thenReturn(new ArrayList<AdherenceSummaryForAWeek>());

        String response = adherenceApiController.list("patientDocId");

        JSONObject resultAsJsonObject = new JSONObject(response);
        JSONArray adherencePerWeekResponse = resultAsJsonObject.getJSONArray("dailyAdherenceSummary");

        JSONObject adherenceForFirstWeek = adherencePerWeekResponse.getJSONObject(0);
        assertEquals(monday.toLocalDate().toString(), adherenceForFirstWeek.get("date"));
        assertEquals(80, adherenceForFirstWeek.get("percentage"));
        assertEquals(8, adherenceForFirstWeek.get("taken"));
        assertEquals(10, adherenceForFirstWeek.get("total"));

        JSONObject adherenceForSecondWeek = adherencePerWeekResponse.getJSONObject(1);
        assertEquals(monday.plusDays(7).toLocalDate().toString(), adherenceForSecondWeek.get("date"));
        assertEquals(90, adherenceForSecondWeek.get("percentage"));
        assertEquals(9, adherenceForSecondWeek.get("taken"));
        assertEquals(10, adherenceForSecondWeek.get("total"));
    }

    @Test
    public void shouldListWeeklyAdherencePerWeekForThePatient() throws JSONException, NoAdherenceRecordedException {
        AdherenceSummaryForAWeek adherenceSummaryForAWeek_1 = new AdherenceSummaryForAWeek();
        AdherenceSummaryForAWeek adherenceSummaryForAWeek_2 = new AdherenceSummaryForAWeek();
        DateTime monday = DateUtil.newDateTime(new LocalDate(2012, 01, 02), 0, 0, 0);
        adherenceSummaryForAWeek_1.setWeekStartDate(monday).setPercentage(80.0).setTaken(8).setTotal(10);
        adherenceSummaryForAWeek_2.setWeekStartDate(monday.plusDays(7)).setPercentage(90.0).setTaken(9).setTotal(10);

        when(dailyPillReminderAdherenceService.getAdherenceOverTime("patientDocId")).thenReturn(new ArrayList<AdherenceSummaryForAWeek>());
        when(fourDayRecallAdherenceService.getAdherenceOverTime("patientDocId")).thenReturn(Arrays.asList(adherenceSummaryForAWeek_1, adherenceSummaryForAWeek_2));

        String response = adherenceApiController.list("patientDocId");

        JSONObject resultAsJsonObject = new JSONObject(response);
        JSONArray adherencePerWeekResponse = resultAsJsonObject.getJSONArray("weeklyAdherenceSummary");

        JSONObject adherenceForFirstWeek = adherencePerWeekResponse.getJSONObject(0);
        assertEquals(monday.toLocalDate().toString(), adherenceForFirstWeek.get("date"));
        assertEquals(80, adherenceForFirstWeek.get("percentage"));
        assertEquals(8, adherenceForFirstWeek.get("taken"));
        assertEquals(10, adherenceForFirstWeek.get("total"));

        JSONObject adherenceForSecondWeek = adherencePerWeekResponse.getJSONObject(1);
        assertEquals(monday.plusDays(7).toLocalDate().toString(), adherenceForSecondWeek.get("date"));
        assertEquals(90, adherenceForSecondWeek.get("percentage"));
        assertEquals(9, adherenceForSecondWeek.get("taken"));
        assertEquals(10, adherenceForSecondWeek.get("total"));
    }
}
