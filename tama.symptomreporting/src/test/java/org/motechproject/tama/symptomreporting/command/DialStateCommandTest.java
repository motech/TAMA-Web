package org.motechproject.tama.symptomreporting.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.symptomreporting.SymptomReportingContextForTest;
import org.motechproject.tama.symptomreporting.factory.SymptomReportingContextFactory;
import org.motechproject.tama.symptomreporting.service.SymptomReportingService;
import org.motechproject.util.Cookies;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DialStateCommandTest {

    public static final String CALL_LOG_ID = "callLogId";
    public static final String PATIENT_DOC_ID = "patientDocId";
    @Mock
    private Cookies cookies;
    @Mock
    private KooKooIVRContext kooKooIVRContext;
    @Mock
    private SymptomReportingService symptomsReportingService;
    @Mock
    private SymptomReportingContextFactory symptomReportingContextFactory;
    private SymptomReportingContextForTest symptomReportingContext;

    @Before
    public void setUp() {
        initMocks(this);
        symptomReportingContext = new SymptomReportingContextForTest().patientDocumentId(PATIENT_DOC_ID).callDetailRecordId(CALL_LOG_ID);
        when(symptomReportingContextFactory.create(kooKooIVRContext)).thenReturn(symptomReportingContext);
        when(kooKooIVRContext.cookies()).thenReturn(cookies);
    }

    @Test
    public void shouldStartCall() {
        DialStateCommand dialStateCommand = new DialStateCommand(symptomsReportingService, symptomReportingContextFactory);
        dialStateCommand.execute(kooKooIVRContext);

        assertTrue(symptomReportingContext.getStartCall());
        verify(symptomsReportingService).smsOTCAdviceToAllClinicianWhenDialToClinicianFails(PATIENT_DOC_ID, CALL_LOG_ID);
    }
}