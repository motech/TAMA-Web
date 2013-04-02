package org.motechproject.tama.messages.message;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.dailypillreminder.command.PlayAdherenceTrendFeedbackCommand;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdherenceTrendMessageTest {

    @Mock
    private PlayAdherenceTrendFeedbackCommand command;
    private TreatmentAdvice advice;
    private LocalDate today;
    private AdherenceTrendMessage adherenceTrendMessage;

    @Before
    public void setup() {
        initMocks(this);
        today = DateUtil.today();
        advice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(today).build();
        adherenceTrendMessage = new AdherenceTrendMessage(advice, command);
    }

    @Test
    public void shouldBeValidIfCurrentDateIsFiveWeeksFromTreatmentStartDate() {
        assertTrue(adherenceTrendMessage.isValid(today.plusDays(TreatmentAdvice.DAYS_IN_FIVE_WEEKS)));
    }

    @Test
    public void shouldBeValidIfCurrentDateIsGreaterThanFiveWeeksFromTreatmentStartDate() {
        assertTrue(adherenceTrendMessage.isValid(today.plusDays(TreatmentAdvice.DAYS_IN_FIVE_WEEKS + 1)));
    }

    @Test
    public void shouldReturnTreatmentIdAsUniqueID() {
        assertEquals(advice.getId(), adherenceTrendMessage.getId());
    }

    @Test
    public void shouldNotBeValidIfCurrentDateIsLessThanFiveWeeksFromTreatmentStartDate() {
        assertFalse(adherenceTrendMessage.isValid(today.plusDays(TreatmentAdvice.DAYS_IN_FIVE_WEEKS - 1)));
    }

    @Test
    public void shouldNotBeValidIfCurrentDateIsLessThanTreatmentStartDate() {
        assertFalse(adherenceTrendMessage.isValid(today.minusDays(1)));
    }

    @Test
    public void shouldBuildAdherenceTrendFeedback() {
        TAMAIVRContext context = mock(TAMAIVRContext.class);
        when(command.executeCommand(context)).thenReturn(new String[]{"Message"});

        assertTrue(adherenceTrendMessage.build(context).getPlayAudios().contains("Message"));
    }
}
