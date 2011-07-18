package org.motechproject.tama.functional.context;


import org.openqa.selenium.WebDriver;

public interface Context {
    void build(WebDriver webDriver);
}
