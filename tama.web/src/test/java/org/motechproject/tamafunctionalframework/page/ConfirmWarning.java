package org.motechproject.tamafunctionalframework.page;


import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ConfirmWarning {

    @FindBy(how = How.ID, using = "unique_mobile_warning_confirm")
    private WebElement confirm;

    @FindBy(how = How.ID, using = "unique_mobile_warning_cancel")
    private WebElement cancel;


    public void postInitialize() {
        confirm = WebDriverFactory.createWebElement(confirm);
    }

    public void confirm() {
        confirm.click();
    }
}
