package org.motechproject.tamafunctionalframework.framework;

import org.openqa.selenium.WebElement;

public interface ExtendedWebElement extends WebElement {
    void select(String value);

    void sendKey(CharSequence charSequence);
}
