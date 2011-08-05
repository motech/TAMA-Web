package org.motechproject.tama.functional.framework;

import org.motechproject.tama.functional.page.Page;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class MyPageFactory {
    public static <T extends Page> T initElements(WebDriver driver, Class<T> pageClassToProxy) {
        T page = PageFactory.initElements(driver, pageClassToProxy);
        page.postInitialize();
        return page;
    }
}
