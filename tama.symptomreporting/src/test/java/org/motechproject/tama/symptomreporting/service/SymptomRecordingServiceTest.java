package org.motechproject.tama.symptomreporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.symptomreporting.domain.SymptomReport;
import org.motechproject.tama.symptomreporting.repository.AllSymptomReports;
import org.motechproject.util.DateUtil;

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
        SymptomReport expectedReport = new SymptomReport("patientDocumentId", "callId", DateUtil.now());
        expectedReport.addSymptomId(FEVER_ID);
        when(allSymptomReports.findByCallId("callId")).thenReturn(null);

        symptomRecordingService.save(FEVER_ID, "patientDocumentId", "callId", DateUtil.now());

        ArgumentCaptor<SymptomReport> reportCapture = ArgumentCaptor.forClass(SymptomReport.class);
        verify(allSymptomReports).addOrReplace(reportCapture.capture());
        assertEquals(expectedReport, reportCapture.getValue());
    }


    @Test
    public void shouldInsertIfOnlySummarySymptomIdReported() {
        SymptomReport expectedReport = new SymptomReport("patientDocumentId", "callId", DateUtil.now());
        String summarySymptomId = "depression";
        expectedReport.addSymptomId(summarySymptomId);
        when(allSymptomReports.findByCallId("callId")).thenReturn(null);

        symptomRecordingService.save(summarySymptomId, "patientDocumentId", "callId", DateUtil.now());

        ArgumentCaptor<SymptomReport> reportCapture = ArgumentCaptor.forClass(SymptomReport.class);
        verify(allSymptomReports).addOrReplace(reportCapture.capture());
        assertEquals(expectedReport, reportCapture.getValue());
    }

    @Test
    public void shouldSetAdviceGivenOnSymptomsReport() {
        SymptomReport expectedReport = new SymptomReport("patientDocumentId", "callId", DateUtil.now());
        when(allSymptomReports.findByCallId("callId")).thenReturn(expectedReport);
        symptomRecordingService.saveAdviceGiven("patientDocumentId", "callId", "some advice");
        verify(allSymptomReports).addOrReplace(expectedReport);
        assertEquals("some advice", expectedReport.getAdviceGiven());
    }

    @Test
    public void shouldCreateNewSymptomsReportLog_WhenNotPresent_WhileSettingAdvice(){
        when(allSymptomReports.findByCallId("callId")).thenReturn(null);
        symptomRecordingService.saveAdviceGiven("patientDocumentId", "callId", "some advice");

        ArgumentCaptor<SymptomReport> newSymptomReportCaptor = ArgumentCaptor.forClass(SymptomReport.class);
        verify(allSymptomReports).addOrReplace(newSymptomReportCaptor.capture());

        assertEquals("callId", newSymptomReportCaptor.getValue().getCallId());
        assertEquals("patientDocumentId", newSymptomReportCaptor.getValue().getPatientDocId());
        assertEquals("some advice", newSymptomReportCaptor.getValue().getAdviceGiven());
    }
}
