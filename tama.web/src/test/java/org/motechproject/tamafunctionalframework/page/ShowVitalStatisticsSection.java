package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.testdata.TestVitalStatistics;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ShowVitalStatisticsSection {

    @FindBy(how = How.ID, using = "_weight_weightInKg_id")
    private WebElement weightElement;

    @FindBy(how = How.ID, using = "_height_heightInCm_id")
    private WebElement heightElement;

    @FindBy(how = How.ID, using = "_systolic_bp_systolicBp_id")
    private WebElement systolicBpElement;

    @FindBy(how = How.ID, using = "_diastolic_bp_diastolicBp_id")
    private WebElement diastolicBpElement;

    @FindBy(how = How.ID, using = "_temperature_temperatureInFahrenheit_id")
    private WebElement temperatureElement;

    @FindBy(how = How.ID, using = "_pulse_pulse_id")
    private WebElement pulseElement;

    @FindBy(how = How.ID, using = "edit_link")
    private WebElement editLink;

    public Double getWeight() {
        return Double.parseDouble(weightElement.getText());
    }

    public Double getHeight() {
        return Double.parseDouble(heightElement.getText());
    }

    public Integer getSystolicBp() {
        return Integer.parseInt(systolicBpElement.getText());
    }

    public Integer getDiastolicBp() {
        return Integer.parseInt(diastolicBpElement.getText());
    }

    public Double getTemperature() {
        return Double.parseDouble(temperatureElement.getText());
    }

    public Integer getPulse() {
        return Integer.parseInt(pulseElement.getText());
    }

    public void clickEdit() {
        this.editLink.click();
    }

    public TestVitalStatistics getVitalStatistics() {
        return new TestVitalStatistics()
                .weightInKg(getWeight())
                .heightInCm(getHeight())
                .systolicBp(getSystolicBp())
                .diastolicBp(getDiastolicBp())
                .temperatureInFahrenheit(getTemperature())
                .pulse(getPulse());
    }
}
