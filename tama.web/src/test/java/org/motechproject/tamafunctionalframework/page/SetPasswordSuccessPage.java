package org.motechproject.tamafunctionalframework.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class SetPasswordSuccessPage extends Page {

    public final String successMessage = "successMessage";

    @FindBy(how = How.ID, using = successMessage)
    WebElement successMessageDiv;

    public SetPasswordSuccessPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(successMessage);
    }

    public WebElement getSuccessMessageElement() {
        return successMessageDiv;
    }
}