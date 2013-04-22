package org.motechproject.tama.messages.message;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.messages.domain.AdherenceTrendMessageCriteria;
import org.motechproject.tama.messages.domain.MessageHistory;
import org.motechproject.tama.messages.domain.Method;
import org.motechproject.tama.messages.service.AdherenceTrendService;
import org.motechproject.tama.messages.service.MessageTrackingService;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdherenceTrendMessageTest {

    @Mock
    private AdherenceTrendService adherenceTrendService;
    @Mock
    private MessageTrackingService messageTrackingService;
    @Mock
    private AdherenceTrendMessageCriteria criteria;
    @Mock
    private MessageHistory history;

    private Method method = Method.PULL;

    private Patient patient;
    private TreatmentAdvice advice;
    private DateTime now;
    private AdherenceTrendMessage adherenceTrendMessage;


    @Before
    public void setup() {
        initMocks(this);
        now = DateUtil.now();
        setupHistory();
        adherenceTrendMessage = new AdherenceTrendMessage(adherenceTrendService, messageTrackingService, criteria);
        advice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(now.toLocalDate()).build();
    }

    private void setupHistory() {
        when(messageTrackingService.get(eq(method), eq(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO), anyString())).thenReturn(history);
    }

    @Test
    public void shouldBeValidIfAdherenceTrendIsAvailable() {
        when(adherenceTrendService.hasAdherenceTrend(patient, advice, now)).thenReturn(true);
        when(criteria.shouldPlay(anyDouble(), eq(history), eq(now))).thenReturn(true);
        assertTrue(adherenceTrendMessage.isValid(method, patient, advice, now));
    }

    @Test
    public void shouldBeFalseWhenAdherenceTrendMessageShouldNotBePlayed() {
        when(criteria.shouldPlay(anyDouble(), eq(history), eq(now))).thenReturn(true);
        assertFalse(adherenceTrendMessage.isValid(method, patient, advice, now));
    }

    @Test
    public void shouldReturnTreatmentIdAsUniqueID() {
        assertEquals(advice.getId(), adherenceTrendMessage.getId(advice));
    }

    @Test
    public void shouldNotBeValidIfCurrentDateIsLessThanFiveWeeksFromTreatmentStartDate() {
        assertFalse(adherenceTrendMessage.isValid(method, patient, advice, now.plusDays(TreatmentAdvice.DAYS_IN_FIVE_WEEKS - 1)));
    }

    @Test
    public void shouldNotBeValidIfCurrentDateIsLessThanTreatmentStartDate() {
        assertFalse(adherenceTrendMessage.isValid(method, patient, advice, now.minusDays(1)));
    }
}
