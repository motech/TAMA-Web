package org.motechproject.tamafunctional.framework;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamafunctional.context.Context;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.setup.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public abstract class BaseTest {
    @Rule
    public TestName testName = new TestName();

    protected WebDriver webDriver;

    @Autowired
    protected PillReminderService pillReminderService;

    @Before
    public void setUp() {
        webDriver = WebDriverFactory.getInstance();
        webDriver.get(LoginPage.LOGIN_URL);
    }

    protected void buildContexts(Context... contexts) {
        for (Context context : Arrays.asList(contexts)) {
            context.build(webDriver);
        }
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
            webDriver.close();
            if (output != null)
                output.close();
        }
    }

}
