package org.motechproject.tamafunctional.page;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.WebDriverFactory;
import org.motechproject.tamafunctional.testdata.TestVitalStatistics;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class FormVitalStatisticsPage extends Page {

    public static final String PAGE_LOAD_MARKER = "page_load_marker";

    @FindBy(how = How.ID, using = "_weightInKg_id")
    private WebElement weightElement;

    @FindBy(how = How.ID, using = "_heightInCm_id")
    private WebElement heightElement;

    @FindBy(how = How.ID, using = "_systolicBp_id")
    private WebElement systolicBpElement;

    @FindBy(how = How.ID, using = "_diastolicBp_id")
    private WebElement diastolicElement;

    @FindBy(how = How.ID, using = "_temperatureInFahrenheit_id")
    private WebElement temperatureElement;

    @FindBy(how = How.ID, using = "_pulse_id")
    private WebElement pulseElement;

    public FormVitalStatisticsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {
        weightElement = WebDriverFactory.createWebElement(weightElement);
        heightElement = WebDriverFactory.createWebElement(heightElement);
        temperatureElement = WebDriverFactory.createWebElement(temperatureElement);
        systolicBpElement = WebDriverFactory.createWebElement(systolicBpElement);
        diastolicElement = WebDriverFactory.createWebElement(diastolicElement);
        pulseElement = WebDriverFactory.createWebElement(pulseElement);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForDojoElementToLoad("_weightInKg_id", "dijitInputInner");
    }

    public ShowVitalStatisticsPage enterVitalStatistics(TestVitalStatistics testVitalStatistics) {
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
        return MyPageFactory.initElements(webDriver, ShowVitalStatisticsPage.class);
    }
}
