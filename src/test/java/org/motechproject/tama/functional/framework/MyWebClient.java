package org.motechproject.tama.functional.framework;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;

import java.io.IOException;

public class MyWebClient {

    private WebClient webClient = new WebClient();

    public Page getPage(String url) {
        try {
            return webClient.getPage(url);
        } catch (IOException e) {
            return null;
        }
    }

}
