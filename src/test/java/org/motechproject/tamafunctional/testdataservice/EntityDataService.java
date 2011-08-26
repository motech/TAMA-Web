package org.motechproject.tamafunctional.testdataservice;

import org.apache.log4j.Logger;
import org.motechproject.tamafunctional.framework.FunctionalTestObject;
import org.openqa.selenium.WebDriver;

//LayerSuperType for DataService layer.
// All operations in data service should leave the AUT to the login page.
public abstract class EntityDataService extends FunctionalTestObject {
    protected WebDriver webDriver;

    protected EntityDataService(WebDriver webDriver) {
        this.webDriver = webDriver;
    }
}
