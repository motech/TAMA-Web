package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.ExtendedWebElement;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DrugDosageSection {

    protected WebDriver webDriver;
    protected WebElement drugDosageTypeElement;
    protected WebElement drugStartDateElement;

    protected WebElement drugMorningDosageTimeElement;
    protected WebElement drugEveningDosageTimeElement;
    protected WebElement drugOffsetDaysElement;
    protected WebElement drugMealAdviceTypeElement;

    public DrugDosageSection(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void postInitialize() {
        drugDosageTypeElement = WebDriverFactory.createWebElement(drugDosageTypeElement);
        drugStartDateElement = WebDriverFactory.createWebElement(drugStartDateElement);

        drugMorningDosageTimeElement = WebDriverFactory.createWebElement(drugMorningDosageTimeElement);
        drugEveningDosageTimeElement = WebDriverFactory.createWebElement(drugEveningDosageTimeElement);
        drugOffsetDaysElement = WebDriverFactory.createWebElement(drugOffsetDaysElement);
        drugMealAdviceTypeElement = WebDriverFactory.createWebElement(drugMealAdviceTypeElement);
    }

    public void createDosage(TestDrugDosage testDrugDosage, Page page) {
        logDosage(testDrugDosage, page);

        ((ExtendedWebElement) drugDosageTypeElement).select(testDrugDosage.dosageType());
        ((ExtendedWebElement) drugStartDateElement).select(testDrugDosage.startDate());

        drugMealAdviceTypeElement.sendKeys(testDrugDosage.mealAdvice());
        if (testDrugDosage.isMorningDosage()) {
            addDetailsForMorningDose(testDrugDosage, page);
        } else if (testDrugDosage.isVariableDosage()) {
            addDetailsForVariableDose(testDrugDosage, page);
        } else {
            addDetailsForEveningDose(testDrugDosage, page);
        }
    }

    private void addDetailsForMorningDose(TestDrugDosage testDrugDosage, Page page) {
        page.waitForElementWithIdToLoad(drugMorningDosageTimeElement.getAttribute("id"));
        drugMorningDosageTimeElement.sendKeys(testDrugDosage.dosageSchedule());
        drugEveningDosageTimeElement.sendKeys(Keys.TAB);
    }

    private void addDetailsForEveningDose(TestDrugDosage testDrugDosage, Page page) {
        page.waitForElementWithIdToLoad(drugEveningDosageTimeElement.getAttribute("id"));
        drugEveningDosageTimeElement.sendKeys(testDrugDosage.dosageSchedule());
        drugEveningDosageTimeElement.sendKeys(Keys.TAB);
    }

    private void addDetailsForVariableDose(TestDrugDosage testDrugDosage, Page page) {
        page.waitForElementWithIdToLoad(drugOffsetDaysElement.getAttribute("id"));
        drugMorningDosageTimeElement.sendKeys(testDrugDosage.dosageSchedule());
        drugEveningDosageTimeElement.sendKeys(testDrugDosage.dosageSchedule());
        drugOffsetDaysElement.sendKeys(testDrugDosage.startsFrom());
    }

    private void logDosage(TestDrugDosage drugDosage, Page page) {
        page.logInfo("%s Dosage at %s", drugDosage.dosageType(), drugDosage.dosageSchedule());
    }

    public void submit() {
        drugStartDateElement.submit();
    }
}
