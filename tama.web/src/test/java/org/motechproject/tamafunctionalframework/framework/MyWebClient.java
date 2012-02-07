package org.motechproject.tamafunctionalframework.framework;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.motechproject.deliverytools.kookoo.QueryParams;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class MyWebClient extends FunctionalTestObject {
    private WebClient webClient;

    public MyWebClient() {
        this(new WebClient());
    }

    MyWebClient(WebClient webClient) {
        this.webClient = webClient;
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
    }

    public Page getPage(String url, QueryParams params) {
        try {
            StringBuilder buffer = new StringBuilder(url);
            buffer.append("?");
            Set<Map.Entry<String, Object>> entries = params.params().entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                buffer.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            logInfo("Invoking %s", buffer.toString());
            return webClient.getPage(buffer.toString());
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

    public String getResponse(String url, QueryParams params) {
        WebResponse webResponse = getWebResponse(url, params);
        return webResponse.getContentAsString();
    }

    public WebResponse getWebResponse(String url, QueryParams params) {
        Page page = getPage(url, params);
        return page.getWebResponse();
    }
}
