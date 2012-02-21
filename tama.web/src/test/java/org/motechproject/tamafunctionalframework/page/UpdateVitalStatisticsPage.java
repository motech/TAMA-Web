package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.motechproject.tamafunctionalframework.testdata.TestVitalStatistics;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class UpdateVitalStatisticsPage extends Page {

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

    public UpdateVitalStatisticsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForDojoElementToLoad("_vitalStatistics.weightInKg_id", "dijitInputInner");
    }

    public void postInitialize() {
        weightElement = WebDriverFactory.createWebElement(weightElement);
        heightElement = WebDriverFactory.createWebElement(heightElement);
        temperatureElement = WebDriverFactory.createWebElement(temperatureElement);
        systolicBpElement = WebDriverFactory.createWebElement(systolicBpElement);
        diastolicElement = WebDriverFactory.createWebElement(diastolicElement);
        pulseElement = WebDriverFactory.createWebElement(pulseElement);
    }

    public ShowClinicVisitPage enterVitalStatistics(TestVitalStatistics testVitalStatistics) {
        weightElement.clear();
        heightElement.clear();
        systolicBpElement.clear();
        diastolicElement.clear();
        temperatureElement.clear();
        pulseElement.clear();
        weightElement.sendKeys(testVitalStatistics.weightInKg().toString());
        heightElement.sendKeys(testVitalStatistics.heightInCm().toString());
        systolicBpElement.sendKeys(testVitalStatistics.systolicBp().toString());
        diastolicElement.sendKeys(testVitalStatistics.diastolicBp().toString());
        temperatureElement.sendKeys(testVitalStatistics.temperatureInFahrenheit().toString());
        pulseElement.sendKeys(testVitalStatistics.pulse().toString());
        weightElement.submit();
        return MyPageFactory.initElements(webDriver, ShowClinicVisitPage.class);
    }
}
