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

    private TAMAIVRContextForTest context;
    private final String TREATMENT_ADVICE_ID = "treatmentAdviceId";


    @Before
    public void setUp() {
        initMocks(this);
        mockStatic(DateUtil.class);
        context = new TAMAIVRContextForTest();
    }

    @Test
    public void shouldCreateWeeklyAdherenceLog() {
        LocalDate today = new LocalDate(2011, 10, 7);
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice() {{
            setId(TREATMENT_ADVICE_ID);
        }};

        String patientId = "patient_id";
        when(allTreatmentAdvices.findByPatientId(patientId)).thenReturn(treatmentAdvice);
        when(DateUtil.today()).thenReturn(today);
        context.patientId(patientId).dtmfInput("1");
        CreateWeeklyAdherenceLogs createWeeklyAdherenceLogs = new CreateWeeklyAdherenceLogs(allWeeklyAdherenceLogs, allTreatmentAdvices);
        createWeeklyAdherenceLogs.executeCommand(context);

        ArgumentCaptor<WeeklyAdherenceLog> weeklyAdherenceLogArgumentCaptor = ArgumentCaptor.forClass(WeeklyAdherenceLog.class);
        verify(allWeeklyAdherenceLogs).add(weeklyAdherenceLogArgumentCaptor.capture());
        assertEquals(today, weeklyAdherenceLogArgumentCaptor.getValue().getLogDate());
        assertEquals(1, weeklyAdherenceLogArgumentCaptor.getValue().getNumberOfDaysMissed());
        assertEquals(patientId, weeklyAdherenceLogArgumentCaptor.getValue().getPatientId());
        assertEquals(TREATMENT_ADVICE_ID, weeklyAdherenceLogArgumentCaptor.getValue().getTreatmentAdviceId());
    }
}