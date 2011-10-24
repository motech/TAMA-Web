package org.motechproject.tamafunctional.framework;

import org.openqa.selenium.Keys;
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

    @Override
    public void select(String value) {
        sendKeys(value);
        sendKey(Keys.ENTER);
    }
}
