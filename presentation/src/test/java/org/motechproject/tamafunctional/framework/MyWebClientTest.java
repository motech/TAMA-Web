package org.motechproject.tamafunctional.framework;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.motechproject.deliverytools.kookoo.QueryParams;
import org.motechproject.tama.ivr.logging.domain.CallLog;

import java.io.IOException;
import java.net.MalformedURLException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MyWebClientTest {
    @Test
    public void getPage() throws IOException {
        WebClient webClient = mock(WebClient.class);
        CookieManager cookieManager = mock(CookieManager.class);
        when(webClient.getCookieManager()).thenReturn(cookieManager);

        MyWebClient myWebClient = new MyWebClient(webClient);
        myWebClient.getPage("http://foo", new QueryParams().put("bar", "baz").put("quack", "quick"));
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(webClient).getPage(stringArgumentCaptor.capture());
        assertEquals(true, stringArgumentCaptor.getValue().contains("http://foo"));
        assertEquals(true, stringArgumentCaptor.getValue().contains("bar=baz&"));
        assertEquals(true, stringArgumentCaptor.getValue().contains("quack=quick&"));
    }
}
