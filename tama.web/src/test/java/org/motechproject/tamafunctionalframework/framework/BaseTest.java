package org.motechproject.tamafunctionalframework.framework;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.util.DateUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class BaseTest extends FunctionalTestObject {

    @Rule
    public TestName testName = new TestName();

    protected WebDriver webDriver;

    @Before
    public void setUp() {
        createWebDriver();
        logInfo("Using login URL as %s", LoginPage.LOGIN_URL);
        webDriver.get(LoginPage.LOGIN_URL);
    }

    private void createWebDriver() {
        try {
            webDriver = WebDriverFactory.getInstance();
        } catch (WebDriverException e) {
            if (e.getMessage().contains("Unable to bind to locking port")) {
                createWebDriver();
            }
        }
    }

    protected String unique(String name) {
        return name + DateUtil.now().toInstant().getMillis() + Math.random();
    }

    @After
    public void tearDown() throws IOException {
        String testMethodName = testName.getMethodName();
        testMethodName = StringUtils.isEmpty(testMethodName) ? DateUtil.now().toString("yyyy-MM-dd HH-mm") : testMethodName;
        if (webDriver == null) return;
        String pageSource = webDriver.getPageSource();

        File file = new File(System.getProperty("base.dir"), String.format("target/%s.html", testMethodName));
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(file));
            output.write(pageSource);
            logInfo("HTML Output logged to %s", file.getName());
        } catch (IOException ignore) {
            System.err.println("Unable to write html result to file " + ignore.getMessage());
        } finally {
            webDriver.manage().deleteAllCookies();
            webDriver.quit();
            if (output != null)
                output.close();
        }
    }
}
