package org.motechproject.tama.functional.framework;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class MyWebElement implements WebElement {
    private WebElement webElement;

    public MyWebElement(WebElement webElement) {
        this.webElement = webElement;
    }

    @Override
    public void click() {
        webElement.click();
    }

    @Override
    public void submit() {
        webElement.submit();
    }

    @Override
    public String getValue() {
        return webElement.getValue();
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        click();
        clear();
        click();
        webElement.sendKeys(keysToSend);
    }

    @Override
    public void clear() {
        webElement.clear();
    }

    @Override
    public String getTagName() {
        return webElement.getTagName();
    }

    @Override
    public String getAttribute(String name) {
        return webElement.getAttribute(name);
    }

    @Override
    public boolean toggle() {
        return webElement.toggle();
    }

    @Override
    public boolean isSelected() {
        return webElement.isSelected();
    }

    @Override
    public void setSelected() {
        webElement.setSelected();
    }

    @Override
    public boolean isEnabled() {
        return webElement.isEnabled();
    }

    @Override
    public String getText() {
        return webElement.getText();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return webElement.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return webElement.findElement(by);
    }
}
