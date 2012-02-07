package org.motechproject.tamafunctionalframework.framework;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class TamaHtmlUnitDriver extends HtmlUnitDriver {
    public TamaHtmlUnitDriver(boolean enableJavascript, boolean throwExceptionOnScriptError) {
        super(enableJavascript);
        getWebClient().setThrowExceptionOnScriptError(throwExceptionOnScriptError);
        getWebClient().setCssErrorHandler(new SilentCssErrorHandler());
        getWebClient().setThrowExceptionOnFailingStatusCode(false);
    }
}
