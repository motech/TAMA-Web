package org.motechproject.tama.fourdayrecall.command;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallDateService;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class WeeklyAdherencePercentageTest {

    public static final String PATIENT_ID = "patientId";
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;
    @Mock
    private FourDayRecallDateService fourDayRecallDateService;

    private WeeklyAdherencePercentage weeklyAdherencePercentage;
    private TAMAIVRContextForTest ivrContext;

    @Before
    public void setUp() {
        initMocks(this);
        ivrContext = new TAMAIVRContextForTest().patientDocumentId(PATIENT_ID);
        weeklyAdherencePercentage = new WeeklyAdherencePercentage(allPatients, allTreatmentAdvices, fourDayRecallAdherenceService, fourDayRecallDateService);
    }

    @Test
    public void shouldReturnCorrectMessage_WhenCurrentWeekAdherenceIsGreaterThan90() throws NoAdherenceRecordedException {
        when(fourDayRecallAdherenceService.adherencePercentageFor(anyInt())).thenReturn(100);
        when(fourDayRecallAdherenceService.getAdherencePercentageForPreviousWeek(PATIENT_ID)).thenReturn(50);
        ivrContext.dtmfInput("0");

        String[] audios = weeklyAdherencePercentage.executeCommand(ivrContext);
        assertEquals(5, audios.length);
        assertThat(audios[0], is(TamaIVRMessage.FDR_YOUR_WEEKLY_ADHERENCE_IS));
        assertThat(audios[1], is("Num_100"));
        assertThat(audios[2], is(TamaIVRMessage.FDR_PERCENT));
        assertThat(audios[3], is(TamaIVRMessage.M02_04_ADHERENCE_COMMENT_GT95_FALLING));
        assertThat(audios[4], is(TamaIVRMessage.FDR_TAKE_DOSAGES_REGULARLY));
    }

    @Test
    public void shouldReturnCorrectMessage_WhenCurrentWeekAdherenceIsGreaterThan70_AndFalling() {
        when(fourDayRecallAdherenceService.adherencePercentageFor(anyInt())).thenReturn(80);
        when(fourDayRecallAdherenceService.isAdherenceFalling(anyInt(), anyString())).thenReturn(true);
        ivrContext.dtmfInput("1");

        String[] audios = weeklyAdherencePercentage.executeCommand(ivrContext);
        assertEquals(5, audios.length);
        assertThat(audios[0], is(TamaIVRMessage.FDR_YOUR_WEEKLY_ADHERENCE_IS));
        assertThat(audios[1], is("Num_080"));
        assertThat(audios[2], is(TamaIVRMessage.FDR_PERCENT));
        assertThat(audios[3], is(TamaIVRMessage.M02_05_ADHERENCE_COMMENT_70TO90_FALLING));
        assertThat(audios[4], is(TamaIVRMessage.FDR_TAKE_DOSAGES_REGULARLY));
    }

    @Test
    public void shouldReturnCorrectMessage_WhenCurrentWeekAdherenceIsGreaterThan70_AndRising() {
        when(fourDayRecallAdherenceService.adherencePercentageFor(anyInt())).thenReturn(80);
        when(fourDayRecallAdherenceService.isAdherenceFalling(anyInt(), anyString())).thenReturn(false);
        ivrContext.dtmfInput("1");

        String[] audios = weeklyAdherencePercentage.executeCommand(ivrContext);
        assertEquals(5, audios.length);
        assertThat(audios[0], is(TamaIVRMessage.FDR_YOUR_WEEKLY_ADHERENCE_IS));
        assertThat(audios[1], is("Num_080"));
        assertThat(audios[2], is(TamaIVRMessage.FDR_PERCENT));
        assertThat(audios[3], is(TamaIVRMessage.M02_06_ADHERENCE_COMMENT_70TO90_RISING));
        assertThat(audios[4], is(TamaIVRMessage.FDR_TAKE_DOSAGES_REGULARLY));
    }

    @Test
    public void shouldReturnCorrectMessage_WhenCurrentWeekAdherenceIsLessThan70_AndFalling() {
        when(fourDayRecallAdherenceService.adherencePercentageFor(anyInt())).thenReturn(60);
        when(fourDayRecallAdherenceService.isAdherenceFalling(anyInt(), anyString())).thenReturn(true);
        ivrContext.dtmfInput("2");

        String[] audios = weeklyAdherencePercentage.executeCommand(ivrContext);
        assertEquals(5, audios.length);
        assertThat(audios[0], is(TamaIVRMessage.FDR_YOUR_WEEKLY_ADHERENCE_IS));
        assertThat(audios[1], is("Num_060"));
        assertThat(audios[2], is(TamaIVRMessage.FDR_PERCENT));
        assertThat(audios[3], is(TamaIVRMessage.M02_07_ADHERENCE_COMMENT_LT70_FALLING));
        assertThat(audios[4], is(TamaIVRMessage.FDR_TAKE_DOSAGES_REGULARLY));
    }

    @Test
    public void shouldReturnCorrectMessage_WhenCurrentWeekAdherenceIsLessThan70_AndRising() {
        when(fourDayRecallAdherenceService.adherencePercentageFor(anyInt())).thenReturn(60);
        when(fourDayRecallAdherenceService.isAdherenceFalling(anyInt(), anyString())).thenReturn(false);
        ivrContext.dtmfInput("2");

        String[] audios = weeklyAdherencePercentage.executeCommand(ivrContext);
        assertEquals(5, audios.length);
        assertThat(audios[0], is(TamaIVRMessage.FDR_YOUR_WEEKLY_ADHERENCE_IS));
        assertThat(audios[1], is("Num_060"));
        assertThat(audios[2], is(TamaIVRMessage.FDR_PERCENT));
        assertThat(audios[3], is(TamaIVRMessage.M02_08_ADHERENCE_COMMENT_LT70_RISING));
        assertThat(audios[4], is(TamaIVRMessage.FDR_TAKE_DOSAGES_REGULARLY));
    }
}
