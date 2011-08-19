package org.motechproject.tamafunctional.testdataservice;

import org.openqa.selenium.WebDriver;

//All operations in data service should leave the AUT to the login page
public abstract class EntityDataService {
    protected WebDriver webDriver;

    public EntityDataService(WebDriver webDriver) {
        this.webDriver = webDriver;
    }
}
