package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.TamaUrl;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class ShowPatientSummaryPage extends Page {
    public static final String PATIENT_ID_ID = "_s_org_motechproject_tama_domain_patient_patientId_patientId_id";

    @FindBy(how = How.ID, using = PATIENT_ID_ID)
    private WebElement patientId;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_patient_mobilePhoneNumber_mobilePhoneNumber_id")
    private WebElement mobileNumber;

    @FindBy(how = How.ID, using = "_s_org_motechproject_tama_domain_patient_dateOfBirth_dateOfBirth_id")
    private WebElement dateOfBirth;

    @FindBy(how = How.XPATH, using = "//li[@id='i_patient_list']/a")
    private WebElement listPatientsLink;

    public ShowPatientSummaryPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithIdToLoad(PATIENT_ID_ID);
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
