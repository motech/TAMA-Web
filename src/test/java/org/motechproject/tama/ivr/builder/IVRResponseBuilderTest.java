package org.motechproject.tama.ivr.builder;

import com.ozonetel.kookoo.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooCollectDtmfFactory;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.KookooResponseFactory;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({KookooResponseFactory.class, KookooCollectDtmfFactory.class})
public class IVRResponseBuilderTest {

    private KookooIVRResponseBuilder builder;
    @Mock
    private IVRMessage messages;

    @Before
    public void setUp() {
        initMocks(this);
        builder = new KookooIVRResponseBuilder();

        when(messages.getWav(anyString(), anyString())).thenReturn("");
    }

    @Test
    public void shouldAddPlayTextOnlyIfItsNotEmpty() {
        when(messages.getText(anyString())).thenReturn("nova");

        String response = builder.withPlayTexts("nova").create(messages, "sid", "en");
        assertTrue(response.contains("nova"));

        builder = new KookooIVRResponseBuilder();
        response = builder.create(messages, "sid", "en");
        assertFalse(response.contains("<playtext>"));
    }

    @Test
    public void shouldAddPlayAudioOnlyIfItsNotEmpty() {
        when(messages.getWav(anyString(), anyString())).thenReturn("nova");
        String response = builder.withPlayAudios("nova").create(messages, "sid", "en");
        assertTrue(response.contains("nova"));

        builder = new KookooIVRResponseBuilder();
        response = builder.create(messages, "sid", "en");
        assertFalse(response.contains("<playaudio>"));
    }

    @Test
    public void shouldAddCollectDTMFOnlyIfItsNotNull() {
        String response = builder.collectDtmf().create(messages, "sid", "en");
        assertTrue(response.contains("<collectdtmf/>"));

        response = new KookooIVRResponseBuilder().create(messages, "sid", "en");
        assertFalse(response.contains("<collectdtmf/>"));
    }

    @Test
    public void shouldAddCollectDTMFWithCharacterLimit() {
        mockStatic(KookooResponseFactory.class);
        mockStatic(KookooCollectDtmfFactory.class);
        when(KookooResponseFactory.create()).thenReturn(new TestableKookooResponse());
        when(KookooCollectDtmfFactory.create()).thenReturn(new TestableCollectDtmf());


        String response = builder.collectDtmf(4).create(messages, "sid", "en");
        assertTrue(response.contains("l=\"4\""));

        when(KookooResponseFactory.create()).thenReturn(new TestableKookooResponse());
        when(KookooCollectDtmfFactory.create()).thenReturn(new TestableCollectDtmf());

        String zeroDigitResponse =  new KookooIVRResponseBuilder().collectDtmf(0).createWithDefaultLanguage(messages,"sid");
        assertTrue(zeroDigitResponse.contains("<collectdtmf/>"));
    }

    @Test
    public void shouldHangupOnlyOnlyWhenAskedFor() {
        String response = builder.withHangUp().create(messages, "sid", "en");
        assertTrue(response.contains("<hangup/>"));

        response = new KookooIVRResponseBuilder().create(messages, "12", "en");
        assertFalse(response.contains("<hangup/>"));
    }

    @Test
    public void shouldAddMultiplePlayAudios() {
        when(messages.getWav("wav1","en")).thenReturn("wav1");
        when(messages.getWav("wav2","en")).thenReturn("wav2");
        String response = builder.withPlayAudios("wav1").withPlayAudios("wav2").create(messages, "12", "en");
        assertTrue(response.contains("<playaudio>wav1</playaudio>"));
        assertTrue(response.contains("<playaudio>wav2</playaudio>"));
    }

    @Test
    public void shouldAddMultiplePlayTexts() {
        when(messages.getText("txt1")).thenReturn("txt1");
        when(messages.getText("txt2")).thenReturn("txt2");
        String response = builder.withPlayTexts("txt1").withPlayTexts("txt2").create(messages, "12", "en");
        assertTrue(response.contains("<playtext>txt1</playtext>"));
        assertTrue(response.contains("<playtext>txt2</playtext>"));
    }
}
