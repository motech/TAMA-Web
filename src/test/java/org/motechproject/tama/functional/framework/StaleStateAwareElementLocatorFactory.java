package org.motechproject.tama.functional.framework;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocator;

public class StaleStateAwareElementLocatorFactory extends AjaxElementLocatorFactory{

    private final WebDriver driver;
    private final int timeOutInSeconds;

    public StaleStateAwareElementLocatorFactory(WebDriver driver, int timeOutInSeconds) {
        super(driver, timeOutInSeconds);
        this.driver = driver;
        this.timeOutInSeconds = timeOutInSeconds;
    }

    public ElementLocator createLocator(java.lang.reflect.Field field){
        return new StaleStateAwareElementLocator(driver,field,timeOutInSeconds);
    }
}
