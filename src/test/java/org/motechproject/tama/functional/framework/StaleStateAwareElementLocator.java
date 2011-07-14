package org.motechproject.tama.functional.framework;


import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.AjaxElementLocator;
import org.openqa.selenium.support.ui.Clock;

import java.lang.reflect.Field;

public class StaleStateAwareElementLocator extends AjaxElementLocator{

    public StaleStateAwareElementLocator(WebDriver driver, Field field, int timeOutInSeconds) {
        super(driver, field, timeOutInSeconds);
    }

    public StaleStateAwareElementLocator(Clock clock, WebDriver driver, Field field, int timeOutInSeconds) {
        super(clock, driver, field, timeOutInSeconds);
    }

    @Override
    protected boolean isElementUsable(WebElement element){
        try{
            element.sendKeys("");
        }catch (StaleElementReferenceException e){
            return false;
        }finally {
            return true;
        }
    }
}
