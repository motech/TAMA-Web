package org.motechproject.tama.ivr.builder;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class IVRMessageBuilderTest {

    @Mock
    IVRMessage messages;

    IVRMessageBuilder ivrMessageBuilder;

    @Before
    public void setup() {
        initMocks(this);
        when(messages.getWav("10")).thenReturn("10.wav");
        when(messages.getWav("7")).thenReturn("7.wav");
        when(messages.getWav("20")).thenReturn("20.wav");
        when(messages.getWav(IVRMessage.IN_THE_MORNING)).thenReturn("in_the_morning.wav");
        when(messages.getWav(IVRMessage.IN_THE_EVENING)).thenReturn("in_the_evening.wav");
        when(messages.getWav(IVRMessage.TODAY)).thenReturn("today.wav");
        when(messages.getWav(IVRMessage.TOMORROW)).thenReturn("tomorrow.wav");
        ivrMessageBuilder = new IVRMessageBuilder(messages);
    }

    @Test
    public void shouldReturnWavFilesForTomorrowMorning() {
        DateTime time = DateUtil.now().plusDays(1).withHourOfDay(10).withMinuteOfHour(20);

        List<String> wavs = ivrMessageBuilder.getWavs(time);
        assertEquals("10.wav", wavs.get(0));
        assertEquals("20.wav", wavs.get(1));
        assertEquals("in_the_morning.wav", wavs.get(2));
        assertEquals("tomorrow.wav", wavs.get(3));
    }

    @Test
    public void shouldReturnWavFilesForTomorrowEvening() {
        DateTime time = DateUtil.now().plusDays(1).withHourOfDay(19).withMinuteOfHour(20);

        List<String> wavs = ivrMessageBuilder.getWavs(time);
        assertEquals("7.wav", wavs.get(0));
        assertEquals("20.wav", wavs.get(1));
        assertEquals("in_the_evening.wav", wavs.get(2));
        assertEquals("tomorrow.wav", wavs.get(3));
    }

    @Test
    public void shouldReturnWavFilesForTodayEvening() {
        DateTime time = DateUtil.now().withHourOfDay(19).withMinuteOfHour(20);

        List<String> wavs = ivrMessageBuilder.getWavs(time);
        assertEquals("7.wav", wavs.get(0));
        assertEquals("20.wav", wavs.get(1));
        assertEquals("in_the_evening.wav", wavs.get(2));
        assertEquals("today.wav", wavs.get(3));
    }
}
