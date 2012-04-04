package org.motechproject.tama.ivr;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
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

    @Test
    public void shouldReturnDayOfWeekFileName() {
        assertEquals("weekday_Monday", TamaIVRMessage.getDayOfWeekFile("Monday"));
    }

    @Test
    public void shouldReturnMonthOfYearFileName() {
        DateTime testDate = new DateTime(2012, 3, 17, 23, 0, 0);
        assertEquals("month_March", TamaIVRMessage.getMonthOfYearFile(testDate.monthOfYear().getAsText()));
    }

    @Test
    public void shouldPrefixDateMessage() {
        assertTrue(new TamaIVRMessage.DateMessage(1).value().startsWith("dates_"));
    }

    @Test
    public void shouldAppendDayToDateMessage() {
        assertTrue(new TamaIVRMessage.DateMessage(2).value().startsWith("dates_2"));
    }

    @Test
    public void shouldNotAppendConstantDayToDateMessage() {
        String message = new TamaIVRMessage.DateMessage(2).value();
        String anotherMessage = new TamaIVRMessage.DateMessage(21).value();
        assertFalse(message.equals(anotherMessage));
    }

    @Test
    public void shouldSuffixDateMessageForDaysEndingWith1() {
        assertTrue(new TamaIVRMessage.DateMessage(1).value().endsWith("st"));
    }

    @Test
    public void shouldSuffixDateMessageForDaysEndingWith2() {
        assertTrue(new TamaIVRMessage.DateMessage(2).value().endsWith("nd"));
    }

    @Test
    public void shouldSuffixDateMessageForDaysEndingWith3() {
        assertTrue(new TamaIVRMessage.DateMessage(3).value().endsWith("rd"));
    }

    @Test
    public void shouldSuffixDateMessageForDays11Through19() {
        for (int i=11; i<=19; i++) {
            assertTrue(new TamaIVRMessage.DateMessage(i).value().endsWith("th"));
        }
    }

    @Test
    public void shouldSuffixDateMessageForDaysWithDefaultSuffix() {
        assertTrue(new TamaIVRMessage.DateMessage(4).value().endsWith("th"));
    }
}
