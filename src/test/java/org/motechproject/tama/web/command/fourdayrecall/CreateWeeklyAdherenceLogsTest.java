package org.motechproject.tama.web.command.fourdayrecall;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.domain.WeeklyAdherenceLog;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.platform.service.FourDayRecallService;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class CreateWeeklyAdherenceLogsTest {
    @Mock
    private AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private FourDayRecallService fourDayRecallService;

    private TAMAIVRContextForTest context;
    private String treatmentAdviceId = "treatmentAdviceId";


    @Before
    public void setUp() {
        initMocks(this);
        mockStatic(DateUtil.class);
        context = new TAMAIVRContextForTest();
    }

    @Test
    public void shouldCreateWeeklyAdherenceLog() {
        String patientId = "patient_id";
        LocalDate today = new LocalDate(2011, 10, 7);
        LocalDate startDateOfTreatmentAdvice = new LocalDate(2011, 10, 2);
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice() {{
            setId(treatmentAdviceId);
        }};

        when(DateUtil.today()).thenReturn(today);
        context.patientId(patientId).dtmfInput("1");
        when(allTreatmentAdvices.findByPatientId(patientId)).thenReturn(treatmentAdvice);
        when(fourDayRecallService.getStartDateForCurrentWeek(patientId)).thenReturn(startDateOfTreatmentAdvice);

        CreateWeeklyAdherenceLogs createWeeklyAdherenceLogs = new CreateWeeklyAdherenceLogs(allTreatmentAdvices, fourDayRecallService, allWeeklyAdherenceLogs);
        createWeeklyAdherenceLogs.executeCommand(context);

        ArgumentCaptor<WeeklyAdherenceLog> weeklyAdherenceLogArgumentCaptor = ArgumentCaptor.forClass(WeeklyAdherenceLog.class);
        verify(allWeeklyAdherenceLogs).add(weeklyAdherenceLogArgumentCaptor.capture());
        assertEquals(today, weeklyAdherenceLogArgumentCaptor.getValue().getLogDate());
        assertEquals(1, weeklyAdherenceLogArgumentCaptor.getValue().getNumberOfDaysMissed());
        assertEquals(patientId, weeklyAdherenceLogArgumentCaptor.getValue().getPatientId());
        assertEquals(treatmentAdviceId, weeklyAdherenceLogArgumentCaptor.getValue().getTreatmentAdviceId());
        assertEquals(startDateOfTreatmentAdvice, weeklyAdherenceLogArgumentCaptor.getValue().getWeekStartDate());
    }
}