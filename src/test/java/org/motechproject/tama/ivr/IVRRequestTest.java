package org.motechproject.tama.ivr;

import org.junit.Test;
import java.util.Map;
import static org.junit.Assert.assertEquals;

public class IVRRequestTest {

    @Test
    public void shouldFetchParamFromTamaData() {
        IVRRequest request = new IVRRequest();
        request.setTamaData("{\"hero\":\"batman\",\"villain\":\"joker\"}");

        Map tamaParams = request.getTamaParams();
        assertEquals("batman", tamaParams.get("hero"));
        assertEquals("joker", tamaParams.get("villain"));
    }

    @Test
    public void shouldGetInputWithoutPoundSymbol() {
        IVRRequest ivrRequest = new IVRRequest("sid", "cid", "someEvent", "4%23");

        assertEquals("4", ivrRequest.getInput());
    }
}
