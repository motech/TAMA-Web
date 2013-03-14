package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.TamaUrl;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ShowPatientSummaryPage extends Page {

    public static final String PATIENT_SUMMARY_ID = "fc_org_motechproject_tama_domain_patient_Patient_Summary";

    @FindBy(how = How.XPATH, using = "//table[@id='fc_org_motechproject_tama_domain_patient_Patient_Summary']/tbody//td[1]")
    private WebElement patientId;

    @FindBy(how = How.XPATH, using = "//table[@id='fc_org_motechproject_tama_domain_patient_Patient_Summary']/tbody//td[2]")
    private WebElement mobileNumber;

    @FindBy(how = How.XPATH, using = "//table[@id='fc_org_motechproject_tama_domain_patient_Patient_Summary']/tbody//td[3]")
    private WebElement dateOfBirth;

    @FindBy(how = How.XPATH, using = "//li[@id='i_patient_list']/a")
    private WebElement listPatientsLink;

    public ShowPatientSummaryPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(PATIENT_SUMMARY_ID);
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

    public ListPatientsPage goToListPatientsPage() {
        listPatientsLink.click();
        return MyPageFactory.initElements(webDriver, ListPatientsPage.class);
    }

    public static ShowPatientSummaryPage get(WebDriver driver, TestPatient patient) {
        driver.get(TamaUrl.viewPageUrlFor(patient));
        return MyPageFactory.initElements(driver, ShowPatientSummaryPage.class);
    }
}
