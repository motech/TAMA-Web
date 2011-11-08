package org.motechproject.tama.ivr;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class TamaIVRMessageTest {
    private TamaIVRMessage messages;
    private Properties properties;

    @Before
    public void setUp() {
        properties = new Properties();
        messages = new TamaIVRMessage(properties);
    }

    @Test
    public void shouldReturnWavFileLink() {
        properties.put(TamaIVRMessage.CONTENT_LOCATION_URL, "http://localhost/stream/");
        properties.put("mayo", "clinic.welcome.mayo");

        assertEquals("http://localhost/stream/en/clinic.welcome.mayo.wav", messages.getWav("mayo","en"));
        assertEquals("http://localhost/stream/en/apollo.wav", messages.getWav("apollo","en"));
    }

    @Test
    public void shouldReturnWavFileForNumbers() {
        assertEquals("Num_000", messages.getNumberFilename(0));
        assertEquals("Num_003", messages.getNumberFilename(3));
        assertEquals("Num_046", messages.getNumberFilename(46));
    }
}
