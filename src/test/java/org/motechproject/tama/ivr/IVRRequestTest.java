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

    @Test
    public void callDirectionShouldBeInbound_WhenThereIsNoTAMAData() {
        IVRRequest ivrRequest = new IVRRequest("sid", "cid", "someEvent", "4%23");
        ivrRequest.setTamaData(null);
        assertEquals(IVRRequest.CallDirection.Inbound, ivrRequest.getCallDirection());
    }

    @Test
    public void callDirectionShouldBeInbound_WhenDirectionNotSpecifiedInTAMAData() {
        IVRRequest ivrRequest = new IVRRequest("sid", "cid", "someEvent", "4%23");
        ivrRequest.setTamaData("{\"hero\":\"batman\",\"villain\":\"joker\"}");
        assertEquals(IVRRequest.CallDirection.Inbound, ivrRequest.getCallDirection());
    }

    @Test
    public void callDirectionShouldBeOutbound_WhenDirectionSpecifiedInTAMAData() {
        IVRRequest ivrRequest = new IVRRequest("sid", "cid", "someEvent", "4%23");
        ivrRequest.setTamaData("{\"is_outbound\":\"true\"}");
        assertEquals(IVRRequest.CallDirection.Outbound, ivrRequest.getCallDirection());
    }
}
