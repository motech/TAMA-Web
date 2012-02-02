package org.motechproject.tama.symptomreporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.symptomreporting.domain.SymptomReport;
import org.motechproject.tama.symptomreporting.repository.AllSymptomReports;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomRecordingServiceTest {
    private static final String FEVER_ID = "fever";
    @Mock
    private AllSymptomReports allSymptomReports;
    private SymptomRecordingService symptomRecordingService;

    @Before
    public void setUp() {
        initMocks(this);
        symptomRecordingService = new SymptomRecordingService(allSymptomReports);
    }

    @Test
    public void shouldRecordSymptomReported() {
        SymptomReport expectedReport = new SymptomReport("patientDocumentId", "callId");
        expectedReport.setSymptomIds(Arrays.asList(FEVER_ID));
        when(allSymptomReports.findByCallId("callId")).thenReturn(null);

        symptomRecordingService.save(FEVER_ID, "patientDocumentId", "callId", DateUtil.now());

        ArgumentCaptor<SymptomReport> reportCapture = ArgumentCaptor.forClass(SymptomReport.class);
        verify(allSymptomReports).insertOrMerge(reportCapture.capture());
        assertEquals(expectedReport, reportCapture.getValue());
    }


    @Test
    public void shouldInsertIfOnlySummarySymptomIdReported() {
        SymptomReport expectedReport = new SymptomReport("patientDocumentId", "callId");
        String summarySymptomId = "depression";
        expectedReport.setSymptomIds(Arrays.asList(summarySymptomId));
        when(allSymptomReports.findByCallId("callId")).thenReturn(null);

        symptomRecordingService.save(summarySymptomId, "patientDocumentId", "callId", DateUtil.now());

        ArgumentCaptor<SymptomReport> reportCapture = ArgumentCaptor.forClass(SymptomReport.class);
        verify(allSymptomReports).insertOrMerge(reportCapture.capture());
        assertEquals(expectedReport, reportCapture.getValue());
    }

    @Test
    public void shouldSetAdviceGivenOnSymptomsReport() {
        SymptomReport expectedReport = new SymptomReport("patientDocumentId", "callId");
        when(allSymptomReports.findByCallId("callId")).thenReturn(expectedReport);
        symptomRecordingService.saveAdviceGiven("callId", "some advice");
        verify(allSymptomReports).update(expectedReport);
        assertEquals("some advice", expectedReport.getAdviceGiven());
    }
}
