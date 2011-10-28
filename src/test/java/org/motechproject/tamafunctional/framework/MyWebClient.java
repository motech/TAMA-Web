package org.motechproject.tamafunctional.framework;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.Cookie;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class MyWebClient extends FunctionalTestObject{
    private WebClient webClient = new WebClient();

    public MyWebClient() {
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
    }

    public Page getPage(String url) {
        try {
            logInfo("Invoking %s", url);
            return webClient.getPage(url);
        } catch (IOException e) {
            return null;
        }
    }

    public void shutDown() {
        webClient.getCookieManager().clearCookies();
        webClient.closeAllWindows();
    }

    public Set<Cookie> cookies() {
        CookieManager cookieManager = webClient.getCookieManager();
        return cookieManager.getCookies();
    }

    public void clearCookies() {
        webClient.getCookieManager().clearCookies();
    }

    public String getResponse(String url) {
        Page page = getPage(url);
        return page.getWebResponse().getContentAsString();
    }
}
