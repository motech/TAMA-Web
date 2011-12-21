package org.motechproject.tamafunctional.test.ivr;

import junit.framework.Assert;
import org.motechproject.tama.common.util.FileUtil;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;

public class IVRAssert {
    public IVRAssert() {
    }

    public static void asksForCollectDtmfWith(IVRResponse ivrResponse, String... names) {
        Assert.assertTrue(ivrResponse.collectDtmf());
        assertAudioFilesPresent(ivrResponse, names);
    }

    public static  void assertAudioFilesPresent(IVRResponse ivrResponse, String... names) {
        for (String name : names) {
            name = FileUtil.sanitizeFilename(name);
            Assert.assertTrue(String.format("%s not found. %s", name, ivrResponse.audiosPlayed()), ivrResponse.wasAudioPlayed(name));
        }
    }
}