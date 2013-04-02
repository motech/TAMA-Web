package org.motechproject.tama.messages.message;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.messages.service.AdherenceTrendService;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdherenceTrendMessageTest {

    @Mock
    private AdherenceTrendService adherenceTrendService;
    private TreatmentAdvice advice;
    private LocalDate today;
    private AdherenceTrendMessage adherenceTrendMessage;

    @Before
    public void setup() {
        initMocks(this);
        today = DateUtil.today();
        advice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(today).build();
        adherenceTrendMessage = new AdherenceTrendMessage(adherenceTrendService);
    }

    @Test
    public void shouldBeValidIfCurrentDateIsFiveWeeksFromTreatmentStartDate() {
        assertTrue(adherenceTrendMessage.isValid(advice, today.plusDays(TreatmentAdvice.DAYS_IN_FIVE_WEEKS)));
    }

    @Test
    public void shouldBeValidIfCurrentDateIsGreaterThanFiveWeeksFromTreatmentStartDate() {
        assertTrue(adherenceTrendMessage.isValid(advice, today.plusDays(TreatmentAdvice.DAYS_IN_FIVE_WEEKS + 1)));
    }

    @Test
    public void shouldReturnTreatmentIdAsUniqueID() {
        assertEquals(advice.getId(), adherenceTrendMessage.getId(advice));
    }

    @Test
    public void shouldNotBeValidIfCurrentDateIsLessThanFiveWeeksFromTreatmentStartDate() {
        assertFalse(adherenceTrendMessage.isValid(advice, today.plusDays(TreatmentAdvice.DAYS_IN_FIVE_WEEKS - 1)));
    }

    @Test
    public void shouldNotBeValidIfCurrentDateIsLessThanTreatmentStartDate() {
        assertFalse(adherenceTrendMessage.isValid(advice, today.minusDays(1)));
    }
}
