package org.motechproject.tama.symptomreporting.integration;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.symptomreporting.controller.SymptomReportingChartController;
import org.motechproject.tama.symptomreporting.domain.SymptomReport;
import org.motechproject.tama.symptomreporting.service.SymptomRecordingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationSymptomReportingContext.xml", inheritLocations = false)
public class SymptomReportingChartControllerIT extends SpringIntegrationTest {

    @Autowired
    private SymptomReportingChartController controller;
    
    @Autowired
    private SymptomRecordingService recordingService;

    private String patientDocId = "patientDocId";

    @Before
    public void setUp() {
        LocalDate today = DateUtil.today();

        recordSymptomAt(today.minusDays(1), "1", "fever");
        recordSymptomAt(today.minusDays(2), "1", "depression");
    }

    @Test
    public void shouldListAllSymptomReportsForAPatientOverGivenNumberOfMonths() throws JSONException {
        String symptomReports = controller.list(patientDocId, 1);

        JSONObject resultAsJsonObject = new JSONObject(symptomReports);
        JSONArray events = resultAsJsonObject.getJSONArray("events");
        JSONObject event = events.getJSONObject(0);
        assertTrue(event.has("start"));
        assertTrue(event.has("title"));
    }

    private void recordSymptomAt(LocalDate asOfDate, String callId, String symptom) {
        SymptomReport symptomReport = recordingService.save(symptom, patientDocId, callId, asOfDate.toDateTimeAtCurrentTime());
        markForDeletion(symptomReport);
    }
}
