package org.motechproject.tamacallflow.ivr.command.fourdayrecall;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.platform.service.FourDayRecallService;

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
    private FourDayRecallService fourDayRecallService;
    @Mock
    private TamaIVRMessage ivrMessage;

    private WeeklyAdherencePercentage weeklyAdherencePercentage;
    private TAMAIVRContextForTest ivrContext;

    @Before
    public void setUp() {
        initMocks(this);
        ivrContext = new TAMAIVRContextForTest().patientId(PATIENT_ID);
        weeklyAdherencePercentage = new WeeklyAdherencePercentage(ivrMessage, fourDayRecallService);
    }

    @Test
    public void shouldReturnCorrectMessage_WhenCurrentWeekAdherenceIsGreaterThan90() {
        when(fourDayRecallService.adherencePercentageFor(anyInt())).thenReturn(100);
        when(fourDayRecallService.getAdherencePercentageForPreviousWeek(PATIENT_ID)).thenReturn(50);
        ivrContext.dtmfInput("0");
        when(ivrMessage.getNumberFilename(100)).thenReturn("Num_100");

        String[] audios = weeklyAdherencePercentage.executeCommand(ivrContext);
        assertEquals(4, audios.length);
        assertThat(audios[0], is(TamaIVRMessage.FDR_YOUR_WEEKLY_ADHERENCE_IS));
        assertThat(audios[1], is("Num_100"));
        assertThat(audios[2], is(TamaIVRMessage.FDR_PERCENT));
        assertThat(audios[3], is(TamaIVRMessage.M02_04_ADHERENCE_COMMENT_GT95_FALLING));
    }

    @Test
    public void shouldReturnCorrectMessage_WhenCurrentWeekAdherenceIsGreaterThan70_AndFalling() {
        when(fourDayRecallService.adherencePercentageFor(anyInt())).thenReturn(80);
        when(fourDayRecallService.isAdherenceFalling(anyInt(),anyString())).thenReturn(true);
        ivrContext.dtmfInput("1");
        when(ivrMessage.getNumberFilename(80)).thenReturn("Num_80");

        String[] audios = weeklyAdherencePercentage.executeCommand(ivrContext);
        assertEquals(4, audios.length);
        assertThat(audios[0], is(TamaIVRMessage.FDR_YOUR_WEEKLY_ADHERENCE_IS));
        assertThat(audios[1], is("Num_80"));
        assertThat(audios[2], is(TamaIVRMessage.FDR_PERCENT));
        assertThat(audios[3], is(TamaIVRMessage.M02_05_ADHERENCE_COMMENT_70TO90_FALLING));
    }

    @Test
    public void shouldReturnCorrectMessage_WhenCurrentWeekAdherenceIsGreaterThan70_AndRising() {
        when(fourDayRecallService.adherencePercentageFor(anyInt())).thenReturn(80);
        when(fourDayRecallService.isAdherenceFalling(anyInt(),anyString())).thenReturn(false);
        ivrContext.dtmfInput("1");
        when(ivrMessage.getNumberFilename(80)).thenReturn("Num_80");

        String[] audios = weeklyAdherencePercentage.executeCommand(ivrContext);
        assertEquals(4, audios.length);
        assertThat(audios[0], is(TamaIVRMessage.FDR_YOUR_WEEKLY_ADHERENCE_IS));
        assertThat(audios[1], is("Num_80"));
        assertThat(audios[2], is(TamaIVRMessage.FDR_PERCENT));
        assertThat(audios[3], is(TamaIVRMessage.M02_06_ADHERENCE_COMMENT_70TO90_RISING));
    }


    @Test
    public void shouldReturnCorrectMessage_WhenCurrentWeekAdherenceIsLessThan70_AndFalling() {
        when(fourDayRecallService.adherencePercentageFor(anyInt())).thenReturn(60);
        when(fourDayRecallService.isAdherenceFalling(anyInt(),anyString())).thenReturn(true);
        ivrContext.dtmfInput("2");
        when(ivrMessage.getNumberFilename(60)).thenReturn("Num_60");

        String[] audios = weeklyAdherencePercentage.executeCommand(ivrContext);
        assertEquals(4, audios.length);
        assertThat(audios[0], is(TamaIVRMessage.FDR_YOUR_WEEKLY_ADHERENCE_IS));
        assertThat(audios[1], is("Num_60"));
        assertThat(audios[2], is(TamaIVRMessage.FDR_PERCENT));
        assertThat(audios[3], is(TamaIVRMessage.M02_07_ADHERENCE_COMMENT_LT70_FALLING));
    }


    @Test
    public void shouldReturnCorrectMessage_WhenCurrentWeekAdherenceIsLessThan70_AndRising() {
        when(fourDayRecallService.adherencePercentageFor(anyInt())).thenReturn(60);
        when(fourDayRecallService.isAdherenceFalling(anyInt(),anyString())).thenReturn(false);
        ivrContext.dtmfInput("2");
        when(ivrMessage.getNumberFilename(60)).thenReturn("Num_60");

        String[] audios = weeklyAdherencePercentage.executeCommand(ivrContext);
        assertEquals(4, audios.length);
        assertThat(audios[0], is(TamaIVRMessage.FDR_YOUR_WEEKLY_ADHERENCE_IS));
        assertThat(audios[1], is("Num_60"));
        assertThat(audios[2], is(TamaIVRMessage.FDR_PERCENT));
        assertThat(audios[3], is(TamaIVRMessage.M02_08_ADHERENCE_COMMENT_LT70_RISING));
    }
}
