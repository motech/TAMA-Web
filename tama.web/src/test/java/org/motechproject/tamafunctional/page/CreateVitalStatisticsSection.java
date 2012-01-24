package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.motechproject.tamafunctional.testdata.TestVitalStatistics;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class CreateVitalStatisticsSection {

    public static final String PAGE_LOAD_MARKER = "page_load_marker";

    @FindBy(how = How.ID, using = "_vitalStatistics.weightInKg_id")
    private WebElement weightElement;

    @FindBy(how = How.ID, using = "_vitalStatistics.heightInCm_id")
    private WebElement heightElement;

    @FindBy(how = How.ID, using = "_vitalStatistics.systolicBp_id")
    private WebElement systolicBpElement;

    @FindBy(how = How.ID, using = "_vitalStatistics.diastolicBp_id")
    private WebElement diastolicElement;

    @FindBy(how = How.ID, using = "_vitalStatistics.temperatureInFahrenheit_id")
    private WebElement temperatureElement;

    @FindBy(how = How.ID, using = "_vitalStatistics.pulse_id")
    private WebElement pulseElement;

    public void postInitialize() {
        weightElement = WebDriverFactory.createWebElement(weightElement);
        heightElement = WebDriverFactory.createWebElement(heightElement);
        temperatureElement = WebDriverFactory.createWebElement(temperatureElement);
        systolicBpElement = WebDriverFactory.createWebElement(systolicBpElement);
        diastolicElement = WebDriverFactory.createWebElement(diastolicElement);
        pulseElement = WebDriverFactory.createWebElement(pulseElement);
    }

    public void fillVitalStatistics(TestVitalStatistics vitalStatistics) {
        weightElement.clear();
        heightElement.clear();
        systolicBpElement.clear();
        diastolicElement.clear();
        temperatureElement.clear();
        pulseElement.clear();
        weightElement.sendKeys(vitalStatistics.weightInKg().toString());
        heightElement.sendKeys(vitalStatistics.heightInCm().toString());
        systolicBpElement.sendKeys(vitalStatistics.systolicBp().toString());
        diastolicElement.sendKeys(vitalStatistics.diastolicBp().toString());
        temperatureElement.sendKeys(vitalStatistics.temperatureInFahrenheit().toString());
        pulseElement.sendKeys(vitalStatistics.pulse().toString());
    }
}
