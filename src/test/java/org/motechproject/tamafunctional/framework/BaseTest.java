package org.motechproject.tamafunctional.framework;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.motechproject.tamafunctional.context.Context;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.util.DateUtil;
import org.openqa.selenium.WebDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public abstract class BaseTest extends FunctionalTestObject {
    @Rule
    public TestName testName = new TestName();

    protected WebDriver webDriver;

    @Before
    public void setUp() {
        webDriver = WebDriverFactory.getInstance();
        logInfo("Using login URL as %s", LoginPage.LOGIN_URL);
        webDriver.get(LoginPage.LOGIN_URL);
    }

    protected void buildContexts(Context... contexts) {
        for (Context context : Arrays.asList(contexts)) {
            context.build(webDriver);
        }
    }

    protected String unique(String name) {
        return name + DateUtil.now().toInstant().getMillis();
    }

    @After
    public void tearDown() throws IOException {
        String testMethodName = testName.getMethodName();
        testMethodName = StringUtils.isEmpty(testMethodName) ? DateUtil.today().toString() : testMethodName;
        String pageSource = webDriver.getPageSource();

        File file = new File(System.getProperty("base.dir"), String.format("target/%s.html", testMethodName));
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(file));
            output.write(pageSource);
            logInfo("HTML Output logged to %s", file.getName());
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
