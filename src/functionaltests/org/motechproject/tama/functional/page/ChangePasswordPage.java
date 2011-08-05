package org.motechproject.tama.functional.page;

import org.motechproject.tama.functional.framework.MyPageFactory;
import org.motechproject.tama.functional.framework.MyWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ChangePasswordPage extends Page {

    private static final String OLD_PASSWORD_ID = "j_oldPassword";

    private static final String NEW_PASSWORD_ID = "j_newPassword";

    private static final String CONFIRM_PASSWORD_ID = "j_newPasswordConfirm";

    public static final String CHANGE_PWD_SUCCESS_URL = "http://localhost:" + System.getProperty("jetty.port", "8080") + "/tama/passwordReset";

    public static final String CHANGE_PASSWORD_URL = "http://localhost:" + System.getProperty("jetty.port", "8080") + "/tama/changePassword";

    @FindBy(how = How.ID, using = OLD_PASSWORD_ID)
    private WebElement oldPassword;

    @FindBy(how = How.ID, using = NEW_PASSWORD_ID)
    private WebElement newPassword;

    @FindBy(how = How.ID, using = CONFIRM_PASSWORD_ID)
    private WebElement confirmPassword;

    public ChangePasswordPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {
        oldPassword = new MyWebElement(oldPassword);
        newPassword = new MyWebElement(newPassword);
        confirmPassword = new MyWebElement(confirmPassword);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(OLD_PASSWORD_ID);
    }

    public PasswordSuccessPage submitWithValidInput(String oldPassword, String newPassword, String confirmPassword) {
        this.oldPassword.sendKeys(oldPassword);
        this.newPassword.sendKeys(newPassword);
        this.confirmPassword.sendKeys(confirmPassword);
        this.newPassword.submit();
        return MyPageFactory.initElements(webDriver, PasswordSuccessPage.class);
    }

}
