package org.motechproject.tamafunctional.framework;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class WebDriverFactory {

    public static final String TEST_DRIVER = "test.driver";
    public static final String HTMLUNIT = "firefox";

    public static WebElement createWebElement(WebElement webElement) {
        if (HTMLUNIT.equals(driverName()))
            return new HtmlUnitWebElement(webElement);
        else
            return new TamaWebElement(webElement);
    }

    private enum Driver {

        FIREFOX("firefox") {
            @Override
            WebDriver give() {
                return new FirefoxDriver();
            }
        },

        CHROME("chrome") {
            @Override
            WebDriver give() {
                return new ChromeDriver();
            }
        },

        IE("ie") {
            @Override
            WebDriver give() {
                return new InternetExplorerDriver();
            }
        },

        HTML_UNIT("htmlunit") {
            @Override
            WebDriver give() {
                return new TamaHtmlUnitDriver(true, false);
            }
        };


        private String name;

        Driver(String name) {
            this.name = name;
        }

        abstract WebDriver give();


        public static Driver enumFor(String name) {
            for (Driver driver : values()) {
                if (driver.is(name)) return driver;
            }
            return null;
        }

        private boolean is(String name) {
            return this.name.equalsIgnoreCase(name);
        }
    }

    public static WebDriver getInstance() {
        String name = driverName();
        return Driver.enumFor(name).give();
    }

    private static String driverName() {
        return System.getProperty(TEST_DRIVER, HTMLUNIT);
    }
}