package org.motechproject.tama.symptomreporting.integration.repository;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.symptomreporting.domain.SymptomReport;
import org.motechproject.tama.symptomreporting.repository.AllSymptomReports;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationSymptomReportingContext.xml", inheritLocations = false)
public class AllSymptomReportsTest extends SpringIntegrationTest {

    @Autowired
    private AllSymptomReports allSymptomReports;
    private String patientDocId = "patientDocId";

    @Test
    public void shouldRecordReportAgainstCallId() {
        SymptomReport report = new SymptomReport("patientDocumentId", "callId");
        SymptomReport result = allSymptomReports.insertOrMerge(report);
        markForDeletion(result);
        assertEquals(report, allSymptomReports.get(report.getId()));
    }

    @Test
    public void shouldRecordOnlyOneReportAgainstCallId() {
        SymptomReport existingReport = new SymptomReport("patientDocumentId", "callId");
        SymptomReport newReport = new SymptomReport("patientDocumentId", "callId");

        SymptomReport mergedReport = existingReport.merge(newReport);

        existingReport = allSymptomReports.insertOrMerge(existingReport);
        markForDeletion(existingReport);
        newReport = allSymptomReports.insertOrMerge(newReport);
        markForDeletion(newReport);

        assertEquals(mergedReport, allSymptomReports.get(existingReport.getId()));
    }

    @Test
    public void shouldReturnListOfSymptomsGivenDateRange(){
        LocalDate to = DateUtil.today();
        LocalDate from = to.minusWeeks(1);

        SymptomReport symptomReport1 = new SymptomReport(patientDocId, "11");
        symptomReport1.setReportedAt(from.minusDays(1).toDateTimeAtCurrentTime());
        
        SymptomReport symptomReport2 = new SymptomReport(patientDocId, "2");
        symptomReport2.setReportedAt(to.minusDays(1).toDateTimeAtCurrentTime());
        symptomReport1 = allSymptomReports.insertOrMerge(symptomReport1);
        symptomReport2 = allSymptomReports.insertOrMerge(symptomReport2);

        List<SymptomReport> symptomReports = allSymptomReports.getSymptomReports(patientDocId, from, to);
        markForDeletion(symptomReport1);
        markForDeletion(symptomReport2);

        assertTrue(symptomReports.contains(symptomReport2));
    }
}
