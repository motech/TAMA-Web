package org.motechproject.tama.functional.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {


    public static final String LOGIN_URL = "http://localhost:"+System.getProperty("jetty.port","8080")+"/tama/login";

    public static final String USERNAME_ID = "j_username";
    public static final String PASSWORD_ID = "j_password";
    public static final String ERROR_MESSAGE_XPATH = "//div[@class='errors']/p";

    @FindBy(how = How.ID, using = USERNAME_ID)
    private WebElement userName;
    @FindBy(how = How.ID, using = PASSWORD_ID)
    private WebElement password;
    @FindBy(how = How.XPATH, using = ERROR_MESSAGE_XPATH)
    private WebElement errorMessage;

    public static final String INCORRECT_USERNAME = "Incorrect";

    public static final String INCORRECT_PASSWORD = "Incorrect";
    public static final String CORRECT_USERNAME = "admin";

    public static final String CORRECT_PASSWORD = "admin";

    public static final String FAILURE_MESSAGE = "Your login attempt was not successful, try again. Reason: Bad credentials .";


    private WebDriver webDriver;

    public LoginPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public LoginPage loginWithIncorrectUserNamePassword() {
        webDriver.get(LOGIN_URL);
        userName.sendKeys(INCORRECT_USERNAME);
        password.sendKeys(INCORRECT_PASSWORD);
        userName.submit();
        return this;
    }

    public HomePage loginWithCorrectUserNamePassword() {
        webDriver.get(LOGIN_URL);
        userName.sendKeys(CORRECT_USERNAME);
        password.sendKeys(CORRECT_PASSWORD);
        userName.submit();
        return PageFactory.initElements(webDriver, HomePage.class);
    }

    public String errorMessage(){
      return errorMessage.getText();
    }

}