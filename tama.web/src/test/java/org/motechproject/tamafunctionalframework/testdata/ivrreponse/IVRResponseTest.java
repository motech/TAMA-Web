package org.motechproject.tamafunctionalframework.testdata.ivrreponse;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class IVRResponseTest {
    @Test
    public void isEmpty() {
        IVRResponse ivrResponse = new IVRResponse();
        assertEquals(true, ivrResponse.isEmpty());
    }
}
