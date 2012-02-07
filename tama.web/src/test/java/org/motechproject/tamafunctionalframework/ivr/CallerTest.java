package org.motechproject.tamafunctionalframework.ivr;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.deliverytools.kookoo.QueryParams;
import org.motechproject.tamafunctionalframework.framework.KooKooResponseParser;
import org.motechproject.tamafunctionalframework.framework.MyWebClient;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class CallerTest {
    @Mock
    private MyWebClient webClient;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void keepInvokingTillTheResponseIsEmpty() {
        Caller caller = new Caller("123", "3432534", webClient);
        when(webClient.getResponse(any(String.class), any(QueryParams.class))).thenReturn(KooKooResponseParser.fromObject(new IVRResponse()));
        caller.enter("1");
        verify(webClient, times(2)).getResponse(any(String.class), any(QueryParams.class));
    }
}
