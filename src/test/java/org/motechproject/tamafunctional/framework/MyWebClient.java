package org.motechproject.tamafunctional.framework;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;

import java.io.IOException;

public class MyWebClient {

    private WebClient webClient = new WebClient();

    public MyWebClient() {
        webClient.getCookieManager().setCookiesEnabled(true);
    }

    public Page getPage(String url) {
        try {
            return webClient.getPage(url);
        } catch (IOException e) {
            return null;
        }
    }

    public void shutDown() {
        webClient.getCookieManager().clearCookies();
        webClient.closeAllWindows();
    }
}
