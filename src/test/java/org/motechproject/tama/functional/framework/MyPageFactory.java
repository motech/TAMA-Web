package org.motechproject.tama.functional.framework;

import org.motechproject.tama.functional.page.Page;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class MyPageFactory {
    public static <T> T initElements(WebDriver driver, Class<? extends Page> pageClassToProxy) {
        Page page = PageFactory.initElements(driver, pageClassToProxy);
        page.postInitialize();
        return (T) page;
    }
}
