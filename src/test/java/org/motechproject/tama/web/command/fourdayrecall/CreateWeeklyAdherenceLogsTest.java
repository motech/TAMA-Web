package org.motechproject.tama.web.command.fourdayrecall;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.domain.WeeklyAdherenceLog;
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
    private IVRSession ivrSession;
    @Mock
    private IVRRequest ivrRequest;
    
    private IVRContext ivrContext;
    private final String PATIENT_ID = "patient_id";
    private final String TREATMENT_ADVICE_ID = "treatmentAdviceId";


    @Before
    public void setUp() {
        initMocks(this);
        mockStatic(DateUtil.class);
    }

    @Test
    public void shouldCreateWeeklyAdherenceLog() {
        LocalDate today = new LocalDate(2011, 10, 7);
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice() {{
            setId(TREATMENT_ADVICE_ID);
        }};

        when(allTreatmentAdvices.findByPatientId(PATIENT_ID)).thenReturn(treatmentAdvice);
        when(DateUtil.today()).thenReturn(today);
        when(ivrSession.getExternalId()).thenReturn(PATIENT_ID);
        when(ivrRequest.getData()).thenReturn("1");

        ivrContext = new IVRContext(ivrRequest, ivrSession);
        CreateWeeklyAdherenceLogs createWeeklyAdherenceLogs = new CreateWeeklyAdherenceLogs(allWeeklyAdherenceLogs, allTreatmentAdvices);
        createWeeklyAdherenceLogs.execute(ivrContext);

        ArgumentCaptor<WeeklyAdherenceLog> weeklyAdherenceLogArgumentCaptor = ArgumentCaptor.forClass(WeeklyAdherenceLog.class);
        verify(allWeeklyAdherenceLogs).add(weeklyAdherenceLogArgumentCaptor.capture());
        assertEquals(today, weeklyAdherenceLogArgumentCaptor.getValue().getLogDate());
        assertEquals(1, weeklyAdherenceLogArgumentCaptor.getValue().getNumberOfDaysMissed());
        assertEquals(PATIENT_ID, weeklyAdherenceLogArgumentCaptor.getValue().getPatientId());
        assertEquals(TREATMENT_ADVICE_ID, weeklyAdherenceLogArgumentCaptor.getValue().getTreatmentAdviceId());
    }
}