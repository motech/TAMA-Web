package org.motechproject.tama.ivr;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class IVRMessageTest {
    private IVRMessage messages;
    private Properties properties;

    @Before
    public void setUp() {
        properties = new Properties();
        messages = new IVRMessage(properties);
    }

    @Test
    public void shouldReturnWavFileLink() {
        properties.put(IVRMessage.CONTENT_LOCATION_URL, "http://localhost/");
        properties.put("mayo", "clinic.welcome.mayo");

        assertEquals("http://localhost/en/clinic.welcome.mayo.wav", messages.getWav("mayo","en"));
        assertEquals("http://localhost/en/apollo.wav", messages.getWav("apollo","en"));
    }
}
