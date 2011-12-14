package org.motechproject.tamafunctional.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ShowAlertPage extends Page {

    private static final String ALERT_DIV_ID = "_s_org_motechproject_tama_alert_id_id";

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_alert_id_patientId_id")
    private WebElement patientId;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_alert_symptomsStatus_symptomsAlertStatus_id")
    private WebElement alertStatus;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_alert_connectedToDoctor_connectedToDoctor_id")
    private WebElement connectedToDoctor;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_alert_type_type_id")
    private WebElement alertType;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_alert_Description_description_id")
    private WebElement description;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_alert_CallPreference_patientCallPreference_id")
    private WebElement callPreference;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_alert_notes_notes_id")
    private WebElement notes;

    public ShowAlertPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void postInitialize() {
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(ALERT_DIV_ID);
    }

    public String patientId() {
        return patientId.getText();
    }

    public String alertStatus() {
        return alertStatus.getText();
    }

    public String connectedToDoctor() {
        return connectedToDoctor.getText();
    }

    public String alertType() {
        return alertType.getText();
    }

    public String description() {
        return description.getText();
    }

    public String callPreference() {
        return callPreference.getText();
    }

    public String notes() {
        return notes.getText();
    }
}
