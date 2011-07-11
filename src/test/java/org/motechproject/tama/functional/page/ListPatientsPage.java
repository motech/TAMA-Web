package org.motechproject.tama.functional.page;

import org.openqa.selenium.WebDriver;

public class ListPatientsPage extends Page {

    public static final String LIST_PATIENT_PANE_ID = "_title_pl_org_motechproject_tama_domain_patient_id";

    public ListPatientsPage(WebDriver webDriver) {
        super(webDriver);
    }
}
