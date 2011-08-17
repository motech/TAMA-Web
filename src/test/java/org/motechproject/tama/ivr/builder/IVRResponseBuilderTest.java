package org.motechproject.tama.ivr.builder;

import com.ozonetel.kookoo.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.decisiontree.KookooCollectDtmfFactory;
import org.motechproject.tama.ivr.decisiontree.KookooResponseFactory;
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

    private IVRResponseBuilder builder;
    @Mock
    private IVRMessage messages;

    @Before
    public void setUp() {
        initMocks(this);
        builder = new IVRResponseBuilder("sid", "en");

        when(messages.getWav(anyString(), anyString())).thenReturn("");
    }

    @Test
    public void shouldAddPlayTextOnlyIfItsNotEmpty() {
        when(messages.get(anyString())).thenReturn("nova");

        Response response = builder.withPlayTexts("nova").create(messages);
        assertTrue(response.getXML().contains("nova"));

        builder = new IVRResponseBuilder("sid","en");
        response = builder.create(messages);
        assertFalse(response.getXML().contains("<playtext>"));
    }

    @Test
    public void shouldAddPlayAudioOnlyIfItsNotEmpty() {
        when(messages.getWav(anyString(), anyString())).thenReturn("nova");
        Response response = builder.withPlayAudios("nova").create(messages);
        assertTrue(response.getXML().contains("nova"));

        builder = new IVRResponseBuilder("sid","en");
        response = builder.create(messages);
        assertFalse(response.getXML().contains("<playaudio>"));
    }

    @Test
    public void shouldAddCollectDTMFOnlyIfItsNotNull() {
        Response response = builder.collectDtmf().create(messages);
        assertTrue(response.getXML().contains("<collectdtmf/>"));

        response = new IVRResponseBuilder("sid","en").create(messages);
        assertFalse(response.getXML().contains("<collectdtmf/>"));
    }

    @Test
    public void shouldAddCollectDTMFWithCharacterLimit() {
        mockStatic(KookooResponseFactory.class);
        mockStatic(KookooCollectDtmfFactory.class);
        when(KookooResponseFactory.create()).thenReturn(new TestableKookooResponse());
        when(KookooCollectDtmfFactory.create()).thenReturn(new TestableCollectDtmf());


        TestableKookooResponse response = (TestableKookooResponse) builder.collectDtmf(4).create(messages);
        assertTrue(response.getDtmf().getMaxDigits() == 4);

        when(KookooResponseFactory.create()).thenReturn(new TestableKookooResponse());
        when(KookooCollectDtmfFactory.create()).thenReturn(new TestableCollectDtmf());

        TestableKookooResponse zeoDigitResponse = (TestableKookooResponse) new IVRResponseBuilder("sid").collectDtmf(0).create(messages);
        assertTrue(zeoDigitResponse.getDtmf().getMaxDigits() == 0);
    }

    @Test
    public void shouldHangupOnlyOnlyWhenAskedFor() {
        Response response = builder.withHangUp().create(messages);
        assertTrue(response.getXML().contains("<hangup/>"));

        response = new IVRResponseBuilder("sid","en").create(messages);
        assertFalse(response.getXML().contains("<hangup/>"));
    }

    @Test
    public void shouldAddMultiplePlayAudios() {
        when(messages.getWav("wav1","en")).thenReturn("wav1");
        when(messages.getWav("wav2","en")).thenReturn("wav2");
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
