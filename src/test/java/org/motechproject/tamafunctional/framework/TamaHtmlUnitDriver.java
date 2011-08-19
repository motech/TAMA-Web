package org.motechproject.tamafunctional.framework;

import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class TamaHtmlUnitDriver extends HtmlUnitDriver {
    public TamaHtmlUnitDriver(boolean enableJavascript, boolean throwExceptionOnScriptError) {
        super(enableJavascript);
        getWebClient().setThrowExceptionOnScriptError(throwExceptionOnScriptError);
    }
}
