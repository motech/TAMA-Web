package org.motechproject.tama.ivr;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TamaIVRMessageTest {
    private TamaIVRMessage ivrMessage;
    private Properties properties;

    @Before
    public void setUp() {
        properties = new Properties();
        ivrMessage = new TamaIVRMessage(properties);
    }

    @Test
    public void shouldReturnWavFileLink() {
        properties.put(TamaIVRMessage.CONTENT_LOCATION_URL, "http://localhost/stream/");
        properties.put("mayo", "clinic.welcome.mayo");

        assertEquals("http://localhost/stream/en/clinic.welcome.mayo.wav", ivrMessage.getWav("mayo", "en"));
        assertEquals("http://localhost/stream/en/apollo.wav", ivrMessage.getWav("apollo", "en"));
    }

    @Test
    public void shouldReturnWavFileForNumbers() {
        assertEquals("Num_000", TamaIVRMessage.getNumberFilename(0));
        assertEquals("Num_003", TamaIVRMessage.getNumberFilename(3));
        assertEquals("Num_046", TamaIVRMessage.getNumberFilename(46));
    }

    @Test
    public void shouldReturnAListOfWavFilesForGivenMultipleDigitNumber() {
        assertArrayEquals(new String[]{"Num_006", "Num_005", "Num_004", "Num_003", "Num_002", "Num_001"}, ivrMessage.getAllNumberFileNames("654321").toArray());
    }
}
