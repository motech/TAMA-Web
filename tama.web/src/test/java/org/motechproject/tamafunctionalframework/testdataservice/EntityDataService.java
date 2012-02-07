package org.motechproject.tamafunctionalframework.testdataservice;

import org.motechproject.tamafunctionalframework.framework.FunctionalTestObject;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.openqa.selenium.WebDriver;

//LayerSuperType for DataService layer.
// All operations in data service should leave the AUT to the login page.
public abstract class EntityDataService extends FunctionalTestObject {
    protected WebDriver webDriver;

    protected EntityDataService(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    protected LoginPage page(Class<LoginPage> pageClass) {
        return MyPageFactory.initElements(webDriver, pageClass);
    }
}
