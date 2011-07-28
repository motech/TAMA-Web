package org.motechproject.tama.ivr.builder;

import com.ozonetel.kookoo.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.IVRMessage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class IVRResponseBuilderTest {

    private IVRResponseBuilder builder;
    @Mock
    private IVRMessage messages;

    @Before
    public void setUp() {
        initMocks(this);
        builder = new IVRResponseBuilder("sid");

        when(messages.getWav(anyString())).thenReturn("");
    }

    @Test
    public void shouldAddPlayTextOnlyIfItsNotEmpty() {
        when(messages.get(anyString())).thenReturn("nova");

        Response response = builder.withPlayTexts("nova").create(messages);
        assertTrue(response.getXML().contains("nova"));

        builder = new IVRResponseBuilder("sid");
        response = builder.create(messages);
        assertFalse(response.getXML().contains("<playtext>"));
    }

    @Test
    public void shouldAddPlayAudioOnlyIfItsNotEmpty() {
        when(messages.getWav(anyString())).thenReturn("nova");
        Response response = builder.withPlayAudios("nova").create(messages);
        assertTrue(response.getXML().contains("nova"));

        builder = new IVRResponseBuilder("sid");
        response = builder.create(messages);
        assertFalse(response.getXML().contains("<playaudio>"));
    }

    @Test
    public void shouldAddCollectDTMFOnlyIfItsNotNull() {
        Response response = builder.collectDtmf().create(messages);
        assertTrue(response.getXML().contains("<collectdtmf/>"));

        response = new IVRResponseBuilder("sid").create(messages);
        assertFalse(response.getXML().contains("<collectdtmf/>"));
    }

    @Test
    public void shouldHangupOnlyOnlyWhenAskedFor() {
        Response response = builder.withHangUp().create(messages);
        assertTrue(response.getXML().contains("<hangup/>"));

        response = new IVRResponseBuilder("sid").create(messages);
        assertFalse(response.getXML().contains("<hangup/>"));
    }

    @Test
    public void shouldAddMultiplePlayAudios() {
        when(messages.getWav("wav1")).thenReturn("wav1");
        when(messages.getWav("wav2")).thenReturn("wav2");
        Response response = builder.withPlayAudios("wav1").withPlayAudios("wav2").create(messages);
        assertTrue(response.getXML().contains("<playaudio>wav1</playaudio>"));
        assertTrue(response.getXML().contains("<playaudio>wav2</playaudio>"));
    }

    @Test
    public void shouldAddMultiplePlayTexts() {
        when(messages.get("txt1")).thenReturn("txt1");
        when(messages.get("txt2")).thenReturn("txt2");
        Response response = builder.withPlayTexts("txt1").withPlayTexts("txt2").create(messages);
        assertTrue(response.getXML().contains("<playtext>txt1</playtext>"));
        assertTrue(response.getXML().contains("<playtext>txt2</playtext>"));
    }
}
