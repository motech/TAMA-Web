package org.motechproject.tama.fourdayrecall.command;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallDateService;
import org.motechproject.tama.fourdayrecall.service.WeeklyAdherenceLogService;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TimeMeridiem;
import org.motechproject.tama.patient.domain.TimeOfDay;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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