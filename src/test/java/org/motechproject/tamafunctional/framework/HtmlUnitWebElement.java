package org.motechproject.tamafunctional.framework;

import org.openqa.selenium.WebElement;

public class HtmlUnitWebElement extends TamaWebElement {
    public HtmlUnitWebElement(WebElement webElement) {
        super(webElement);
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        click();
        clear();
        click();
        webElement.sendKeys(keysToSend);
    }
}
