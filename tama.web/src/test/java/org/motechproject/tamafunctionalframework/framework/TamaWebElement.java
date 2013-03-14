package org.motechproject.tamafunctionalframework.framework;

import org.openqa.selenium.*;
import org.openqa.selenium.internal.Locatable;

import java.util.List;

//This class should be agnostic of any specific driver
public class TamaWebElement implements ExtendedWebElement {

    protected WebElement webElement;

    public TamaWebElement(WebElement webElement) {
        this.webElement = webElement;
    }

    @Override
    public void click() {
        try {
            ((Locatable)webElement).getLocationOnScreenOnceScrolledIntoView();
            webElement.click();
        } catch (Exception e) {
            webElement.click();
        }
    }

    @Override
    public void submit() {
        ((Locatable)webElement).getLocationOnScreenOnceScrolledIntoView();
        webElement.sendKeys(Keys.ENTER);
    }

    @Override
    public void sendKeys(CharSequence... charSequences) {
        ((Locatable)webElement).getLocationOnScreenOnceScrolledIntoView();
        click();
        clear();
        webElement.sendKeys(charSequences);
    }

    @Override
    public void clear() {
        ((Locatable)webElement).getLocationOnScreenOnceScrolledIntoView();
        webElement.clear();
    }

    @Override
    public String getTagName() {
        return webElement.getTagName();
    }

    @Override
    public String getAttribute(String s) {
        return webElement.getAttribute(s);
    }


    @Override
    public boolean isSelected() {
        return webElement.isSelected();
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
        try {
            return webElement.findElement(by);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public boolean isDisplayed() {
        return webElement.isDisplayed();
    }

    @Override
    public Point getLocation() {
        return webElement.getLocation();
    }

    @Override
    public Dimension getSize() {
        return webElement.getSize();
    }

    @Override
    public String getCssValue(String s) {
        return webElement.getCssValue(s);
    }

    @Override
    public void select(String value) {
        sendKeys(value);
        click(); // required to simulate onKeyPress that sets the dojo backing element
    }

    @Override
    public void sendKey(CharSequence charSequence) {
        ((Locatable)webElement).getLocationOnScreenOnceScrolledIntoView();
        webElement.sendKeys(charSequence);
    }
}
