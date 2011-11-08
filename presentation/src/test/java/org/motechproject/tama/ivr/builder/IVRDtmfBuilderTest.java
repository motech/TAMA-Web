package org.motechproject.tama.ivr.builder;

import com.ozonetel.kookoo.CollectDtmf;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IVRDtmfBuilderTest {

    private IVRDtmfBuilder builder;

    @Before
    public void setUp() {
        builder = new IVRDtmfBuilder();
    }

    @Test
    public void shouldAddPlayTextOnlyIfItIsNotEmpty() {
        CollectDtmf collectDtmf = builder.withPlayText("nova").create();
        assertTrue(collectDtmf.getXML().contains("nova"));

        collectDtmf = builder.withPlayText("").create();
        assertFalse(collectDtmf.getXML().contains("nova"));
    }

    @Test
    public void shouldAddPlayAudioOnlyIfItIsNotEmpty() {
        CollectDtmf collectDtmf = builder.withPlayAudio("nova").create();
        assertTrue(collectDtmf.getXML().contains("nova"));

        collectDtmf = builder.withPlayAudio("").create();
        assertFalse(collectDtmf.getXML().contains("nova"));
    }
}
