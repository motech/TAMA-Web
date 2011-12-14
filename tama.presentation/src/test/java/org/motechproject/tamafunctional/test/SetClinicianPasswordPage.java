package org.motechproject.tamafunctional.test;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.Page;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class SetClinicianPasswordPage extends Page {

    static public final String NEW_PASSWORD_TEXTBOX_ID = "j_newPassword";
    static public final String CONFIRM_NEW_PASSWORD_TEXTBOX_ID = "j_newPasswordConfirm";

    @FindBy(how = How.ID, using = NEW_PASSWORD_TEXTBOX_ID)
    private WebElement newPasswordTextBox;

    @FindBy(how = How.ID, using = CONFIRM_NEW_PASSWORD_TEXTBOX_ID)
    private WebElement confirmNewPasswordTextBox;

    public SetClinicianPasswordPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(NEW_PASSWORD_TEXTBOX_ID);
    }

    public SetPasswordSuccessPage submitWithValidInput(String newPassword, String confirmNewPassword) {
        newPasswordTextBox.clear();
        newPasswordTextBox.sendKeys(newPassword);
        confirmNewPasswordTextBox.sendKeys(newPassword);
        confirmNewPasswordTextBox.submit();
        return MyPageFactory.initElements(webDriver, SetPasswordSuccessPage.class);
    }
}