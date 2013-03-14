package org.motechproject.tamafunctionalframework.framework;

import org.motechproject.tamafunctionalframework.page.Page;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.TimeoutException;

import java.lang.reflect.InvocationTargetException;

public class MyPageFactory {
    public static <T extends Page> T initElements(WebDriver driver, Class<T> pageClassToProxy) {
        try {
            T page = PageFactory.initElements(driver, pageClassToProxy);
            page.postInitialize();
            return page;
        } catch (RuntimeException e) {
            if (e.getCause() instanceof InvocationTargetException && ((InvocationTargetException) e.getCause()).getTargetException() instanceof TimeoutException) {
                String message = String.format("Most likely is caused by page transition didn't happen as a result of previous action. URL for current page: %s. Expected Page: %s", driver.getCurrentUrl(), pageClassToProxy.getName());
                throw new RuntimeException(message, e.getCause());
            }
            throw e;
        }
    }
}
