package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.ExtendedWebElement;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.TamaUrl;
import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.motechproject.tamafunctionalframework.testdata.TestEntity;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.TestPatientPreferences;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import java.net.MalformedURLException;
import java.net.URL;

public class ShowPatientPage extends Page {
    public static final String PATIENT_ID_ID = "_s_org_motechproject_tama_domain_patient_patientId_patientId_id";
    public static final String ACTIVATE_PATIENT_ID = "activatePatient";
    public static final String DEACTIVATE_PATIENT_ID = "deactivatePatientButton";

    @FindBy(how = How.ID, using = PATIENT_ID_ID)
    private WebElement patientId;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_patient_mobilePhoneNumber_mobilePhoneNumber_id")
    private WebElement mobileNumber;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_patient_dateOfBirth_dateOfBirthAsDate_id")
    private WebElement dateOfBirth;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_patient_test_reason_name_id")
    private WebElement hivTestReason;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_patient_mode_of_transmission_type_id")
    private WebElement modeOfTransmission;

    @FindBy(how = How.CLASS_NAME, using = "drug_allergy_text")
    private WebElement allergy;

    @FindBy(how = How.CLASS_NAME, using = "drug_rash_text")
    private WebElement rash;

    @FindBy(how = How.ID, using = "_c_org_motechproject_tama_domain_patient_passcode_passcode_id")
    private WebElement passcode;

    @FindBy(how = How.ID, using = "_patientPreferencesDayOfWeeklyCall_dayOfWeeklyCall_id")
    private WebElement dayOfWeeklyCall;

    @FindBy(how = How.ID, using = "_bestCallTime__id")
    private WebElement bestCallTime;

    @FindBy(how = How.ID, using = ACTIVATE_PATIENT_ID)
    private WebElement activationLink;

    @FindBy(how = How.ID, using = DEACTIVATE_PATIENT_ID)
    private WebElement deactivationLink;

    @FindBy(how = How.ID, using = "_patient.status_id")
    private WebElement deactivationReasonDropdown;

    @FindBy(how = How.ID, using = "clinic_visits")
    private WebElement clinicVisitsLink;

    @FindBy(how = How.ID, using = "_c_org_motechproject_tama_domain_Patient_status_displayName_id")
    private WebElement status;

    @FindBy(how = How.ID, using = "_c_org_motechproject_tama_domain_Patient_reminderCall_displayCallPreference_id")
    private WebElement callPreference;

    @FindBy(how = How.ID, using = "_title_fc_org_motechproject_tama_domain_patient_Preferences_update")
    private WebElement editTAMAPreferencesLink;

    @FindBy(how = How.XPATH, using = "//li[@id='i_patient_list']/a")
    private WebElement listPatientsLink;

    @FindBy(how = How.ID, using = "lab_results")
    private WebElement labResultsLink;

    @FindBy(how = How.ID, using = "vital_statistics")
    private WebElement showOrCreateVitalStatisticsLink;

    public ShowPatientPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(PATIENT_ID_ID);
    }

    @Override
    public void postInitialize() {
        this.deactivationReasonDropdown = WebDriverFactory.createWebElement(this.deactivationReasonDropdown);
    }

    public String getPatientId() {
        return patientId.getText();
    }

    public String getMobileNumber() {
        return mobileNumber.getText();
    }

    public String getDateOfBirth() {
        return dateOfBirth.getText();
    }

    public String getHIVTestReason() {
        return hivTestReason.getText();
    }

    public String getModeOfTransmission() {
        return modeOfTransmission.getText();
    }

    public String getAllergyText() {
        return allergy.getText();
    }

    public String getRashText() {
        return rash.getText();
    }

    public String getPasscode() {
        return passcode.getText();
    }

    public String getStatus() {
        return status.getText();
    }

    public CreateClinicVisitPage activatePatient() {
        this.activationLink.click();
        return MyPageFactory.initElements(webDriver, CreateClinicVisitPage.class);
    }

    public ShowPatientPage deactivatePatient(String reason) {
        ((ExtendedWebElement) this.deactivationReasonDropdown).select(reason);
        this.deactivationLink.click();
        waitForElementWithIdToLoad(ACTIVATE_PATIENT_ID);
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }

    public CreateClinicVisitPage goToCreateClinicVisitPage() {
        return goToClinicVisitListPage().
                gotoFirstCreateClinicVisitPage();
    }

    private ShowClinicVisitListPage goToClinicVisitListPage() {
        this.clinicVisitsLink.click();
        return MyPageFactory.initElements(webDriver, ShowClinicVisitListPage.class);
    }

    public ShowClinicVisitPage goToShowClinicVisitPage() {
        return goToClinicVisitListPage().
                gotoFirstShowClinicVisitPage();
    }

    public ListPatientsPage goToListPatientsPage() {
        listPatientsLink.click();
        return MyPageFactory.initElements(webDriver, ListPatientsPage.class);
    }

    public String patientDocId() {
        try {
            return new URL(webDriver.getCurrentUrl()).getFile().replace("/tama/patients/", "");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public TestPatientPreferences.CallPreference getCallPreference() {
        return callPreference.getText().equals("Daily") ? TestPatientPreferences.CallPreference.DAILY_CALL : TestPatientPreferences.CallPreference.WEEKLY_CALL;
    }

    public UpdatePatientPage clickOnEditTAMAPreferences() {
        this.editTAMAPreferencesLink.click();
        return MyPageFactory.initElements(webDriver, UpdatePatientPage.class);
    }

    public static ShowPatientPage get(WebDriver driver, TestPatient patient) {
        driver.get(TamaUrl.viewPageUrlFor(patient));
        return MyPageFactory.initElements(driver, ShowPatientPage.class);
    }
}
