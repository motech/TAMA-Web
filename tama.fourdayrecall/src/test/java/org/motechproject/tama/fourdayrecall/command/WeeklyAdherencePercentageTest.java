package org.motechproject.tama.fourdayrecall.command;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallAdherenceService;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class WeeklyAdherencePercentageTest {

    public static final String PATIENT_ID = "patientId";

    @Mock
    private FourDayRecallAdherenceService fourDayRecallAdherenceService;
    private TAMAIVRContextForTest ivrContext;

    private WeeklyAdherencePercentage weeklyAdherencePercentage;

    @Before
    public void setUp() {
        initMocks(this);
        ivrContext = new TAMAIVRContextForTest().patientDocumentId(PATIENT_ID);
        weeklyAdherencePercentage = new WeeklyAdherencePercentage(fourDayRecallAdherenceService);
    }

    @Test
    public void shouldReturnCorrectMessage_WhenCurrentWeekAdherenceIsGreaterThan90() throws NoAdherenceRecordedException {
        when(fourDayRecallAdherenceService.adherencePercentageFor(anyInt())).thenReturn(100);
        ivrContext.dtmfInput("0");

        String[] audios = weeklyAdherencePercentage.executeCommand(ivrContext);
        assertEquals(3, audios.length);
        assertThat(audios[0], is(TamaIVRMessage.FDR_YOUR_WEEKLY_ADHERENCE_IS));
        assertThat(audios[1], is("Num_100"));
        assertThat(audios[2], is(TamaIVRMessage.FDR_PERCENT));
    }
}
