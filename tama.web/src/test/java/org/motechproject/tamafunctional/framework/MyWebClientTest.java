package org.motechproject.tamafunctional.framework;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.motechproject.deliverytools.kookoo.QueryParams;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

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
