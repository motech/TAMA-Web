package org.motechproject.tama.functional.setup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.util.Hashtable;

public class WebDriverFactory {

    public static final String TEST_DRIVER = "test.driver";
    public static final String HTMLUNIT = "htmlunit";
    public static final String FIREFOX = "firefox";
    public static final String IE = "ie";

    public static WebDriver getInstance() {
        String name = System.getProperty(TEST_DRIVER, FIREFOX);
        return createDrivers().get(name);
    }

    private static Hashtable<String, WebDriver> createDrivers() {
        Hashtable<String, WebDriver> drivers = new Hashtable<String, WebDriver>();
        drivers.put(FIREFOX, new FirefoxDriver());
        drivers.put(IE, new InternetExplorerDriver());
        drivers.put(HTMLUNIT, new HtmlUnitDriver(true));
        return drivers;
    }
}
