package org.motechproject.tamafunctional.page;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class PasswordSuccessPage extends Page{
    
    private static final String SUCCESS_MESSAGE_ELEMENT_ID = "successMessage";
    
    @FindBy(how = How.ID, using = SUCCESS_MESSAGE_ELEMENT_ID)
    private WebElement successMessage;
    
    public PasswordSuccessPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
       waitForElementWithIdToLoad(SUCCESS_MESSAGE_ELEMENT_ID);
    }

    public Object getSuccessMessageElement() {
        return successMessage;
    }
}
