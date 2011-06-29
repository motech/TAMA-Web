package org.motechproject.tama.functional.setup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class WebDriverFactory {

    public static final String WEB_DRIVER = "web.driver";
    public static final String HTMLUNIT = "htmlunit";

    private enum Driver {

        FIREFOX("firefox") {
            @Override
            WebDriver give() {
                return new FirefoxDriver();
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
                return new HtmlUnitDriver(true);
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
        String name = System.getProperty(WEB_DRIVER, HTMLUNIT);
        WebDriver give = Driver.enumFor(name).give();
        return give;
    }
}
