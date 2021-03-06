package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.TamaUrl;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;


public class ListPatientsPage extends Page {
    public static final String PATIENT_REGISTRATION_LINK_XPATH = "//li[@id='i_patient_new']/a";
    public static final String LIST_PATIENT_PANE_ID = "_title_pl_org_motechproject_tama_domain_patient_id";

    @FindBy(how = How.XPATH, using = PATIENT_REGISTRATION_LINK_XPATH)
    private WebElement patientRegistrationLink;

    @FindBy(how = How.XPATH, using = "//li[@id='i_patient_list']/a")
    private WebElement listPatientsLink;



    @FindBy(how = How.XPATH, using = "//li[@id='i_alert_list']/a")
    private WebElement showAlerts;

    @FindBy(how = How.XPATH, using = "//h3")
    private WebElement welcomeMessage;

    public ListPatientsPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected void waitForPageToLoad() {
        waitForElementWithXPATHToLoad(PATIENT_REGISTRATION_LINK_XPATH);
    }



    public PatientRegistrationPage goToPatientRegistrationPage() {
        patientRegistrationLink.click();
        return MyPageFactory.initElements(webDriver, PatientRegistrationPage.class);
    }

    public ListPatientsPage goToListPatientsPage() {
        listPatientsLink.click();
        return MyPageFactory.initElements(webDriver, ListPatientsPage.class);
    }

    public ChangePasswordPage goToChangePasswordPage() {
        WebElement changePasswordLink = getNavigationLinks().findElement(By.id("changePasswordLink"));
        changePasswordLink.click();
        return MyPageFactory.initElements(webDriver, ChangePasswordPage.class);
    }

    public ShowPatientPage gotoShowPatientPage(TestPatient patient) {
        webDriver.get(TamaUrl.viewPageUrlFor(patient));
        return MyPageFactory.initElements(webDriver, ShowPatientPage.class);
    }

    public AlertsPage goToAlertsPage() {
        showAlerts.click();
        return MyPageFactory.initElements(webDriver, AlertsPage.class);
    }
}
