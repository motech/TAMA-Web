package org.motechproject.tama.functional.framework;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.motechproject.tama.functional.page.LoginPage;
import org.motechproject.tama.functional.setup.WebDriverFactory;
import org.openqa.selenium.WebDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class BaseTest {
    @Rule
    public TestName testName = new TestName();

    protected WebDriver webDriver;

    @Before
    public void setUp() {
        webDriver = WebDriverFactory.getInstance();
        webDriver.get(LoginPage.LOGIN_URL);
    }

    @After
    public void tearDown() throws IOException {
        String testMethodName = testName.getMethodName();
        String pageSource = webDriver.getPageSource();

        File file = new File(System.getProperty("base.dir"), String.format("target/%s.html", testMethodName));
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(file));
            output.write(pageSource);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            webDriver.manage().deleteAllCookies();
            webDriver.quit();
            if (output != null)
                output.close();
        }
    }

}
