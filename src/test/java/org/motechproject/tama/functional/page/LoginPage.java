package org.motechproject.tama.functional.page;

import org.motechproject.tama.functional.framework.MyPageFactory;
import org.motechproject.tama.functional.framework.MyWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends Page {
    public static final String LOGIN_URL = "http://localhost:"+System.getProperty("jetty.port","8080")+"/tama/login";
    public static final String USERNAME_ID = "j_username";
    public static final String PASSWORD_ID = "j_password";
    public static final String ERROR_MESSAGE_XPATH = "//div[@class='errors']/p";
    public static final String INCORRECT_USERNAME = "Incorrect";
    public static final String INCORRECT_PASSWORD = "Incorrect";
    public static final String CORRECT_USERNAME = "admin";
    public static final String CORRECT_PASSWORD = "password";
    public static final String FAILURE_MESSAGE = "Your login attempt was not successful, try again. Reason: User not found .";

    @FindBy(how = How.ID, using = USERNAME_ID)
    private WebElement userName;
    @FindBy(how = How.ID, using = PASSWORD_ID)
    private WebElement password;
    @FindBy(how = How.XPATH, using = ERROR_MESSAGE_XPATH)
    private WebElement errorMessage;

    public LoginPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void postInitialize() {
        userName = new MyWebElement(userName);
        password = new MyWebElement(password);
    }

    public LoginPage loginWithIncorrectAdminUserNamePassword() {
        login(INCORRECT_USERNAME, INCORRECT_PASSWORD);
        this.waitForElementWithIdToLoad(USERNAME_ID);
        return this;
    }

    public HomePage loginWithCorrectAdminUserNamePassword() {
        return loginAndWait(CORRECT_USERNAME, CORRECT_PASSWORD);
    }

    private HomePage loginAndWait(String userName, String password) {
        login(userName, password);
        this.waitForElementWithXPATHToLoad(HomePage.PATIENT_REGISTRATION_LINK_XPATH);
        return MyPageFactory.initElements(webDriver, HomePage.class);
    }

    private void login(String userName, String password) {
        webDriver.get(LOGIN_URL);
        this.userName.sendKeys(userName);
        this.password.sendKeys(password);
        this.userName.submit();
    }

    public String errorMessage(){
      return errorMessage.getText();
    }

    public HomePage loginWithClinicianUserNamePassword(String clinicianUsername, String clinicianPassword) {
        return loginAndWait(clinicianUsername, clinicianPassword);
    }
}