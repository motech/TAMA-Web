package org.motechproject.tama.fourdayrecall.command;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CreateWeeklyAdherenceLogsTest extends BaseUnitTest {
    @Mock
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;
    private CreateWeeklyAdherenceLogs createWeeklyAdherenceLogs;

    private TAMAIVRContextForTest context;
    private LocalDate today;


    @Before
    public void setUp() {
        initMocks(this);
        context = new TAMAIVRContextForTest();
        today = new LocalDate(2011, 10, 7);
        mockCurrentDate(DateUtil.newDateTime(today, 9, 0, 0));

        createWeeklyAdherenceLogs = new CreateWeeklyAdherenceLogs(fourDayRecallAdherenceService);
    }

    @Test
    public void shouldCreateWeeklyAdherenceLog() {
        String patientId = "patient_id";
        context.patientDocumentId(patientId).dtmfInput("1");

        createWeeklyAdherenceLogs.executeCommand(context);
        verify(fourDayRecallAdherenceService).recordAdherence(patientId, 1);
    }
}