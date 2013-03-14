package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.TamaUrl;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class LoginPage extends Page {
    public static final String LOGIN_URL = TamaUrl.baseFor("login");
    public static final String USERNAME_ID = "j_username";
    public static final String PASSWORD_ID = "j_password";
    public static final String ERROR_MESSAGE_XPATH = "//div[@class='errors']/p";
    public static final String INCORRECT_USERNAME = "Incorrect";
    public static final String INCORRECT_PASSWORD = "Incorrect";
    public static final String CORRECT_USERNAME = "admin";
    public static final String CORRECT_PASSWORD = "password";
    public static final String FAILURE_MESSAGE = "The username or password you entered is incorrect.";

    @FindBy(how = How.ID, using = USERNAME_ID)
    private WebElement userName;
    @FindBy(how = How.ID, using = PASSWORD_ID)
    private WebElement password;
    @FindBy(how = How.XPATH, using = ERROR_MESSAGE_XPATH)
    private WebElement errorMessage;

    public LoginPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForDojoElementToLoad(USERNAME_ID, "dijitInputInner");
    }

    public void postInitialize() {
        userName = WebDriverFactory.createWebElement(userName);
        password = WebDriverFactory.createWebElement(password);
    }

    public LoginPage loginWithIncorrectAdminUserNamePassword() {
        login(INCORRECT_USERNAME, INCORRECT_PASSWORD);
        waitForElementWithIdToLoad("login-errors");
        return this;
    }

    public ListClinicsPage loginWithCorrectAdminUserNamePassword() {
        login(CORRECT_USERNAME, CORRECT_PASSWORD);
        return MyPageFactory.initElements(webDriver, ListClinicsPage.class);
    }

    private void login(String userName, String password) {
        this.userName.sendKeys(userName);
        this.password.sendKeys(password);
        this.userName.submit();
    }

    public String errorMessage() {
        return errorMessage.getText();
    }

    public ListPatientsPage loginWithClinicianUserNamePassword(String clinicianUsername, String clinicianPassword) {
        login(clinicianUsername, clinicianPassword);
        return MyPageFactory.initElements(webDriver, ListPatientsPage.class).goToListPatientsPage();
    }

    @Override
    public void logout() {
    }
}
