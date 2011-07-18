package org.motechproject.tama.functional.test.ivr;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.functional.framework.MyPageFactory;
import org.motechproject.tama.functional.framework.MyWebClient;
import org.motechproject.tama.functional.page.LoginPage;
import org.motechproject.tama.functional.page.ShowPatientPage;
import org.motechproject.tama.functional.preset.ClinicianPreset;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testApplicationContext.xml")
public class PatientAuthenticationTest extends BaseIVRTest {
    private MyWebClient webClient;

    @Before
    public void setUp() {
        webClient = new MyWebClient();
        super.setUp();
    }

    @Test
    public void shouldTestConversationForPatientWhoseNumberIsNotActivated() throws MalformedURLException {
        Page page = webClient.getPage(urlWith("123", "9876543210", "NewCall"));
        WebResponse webResponse = page.getWebResponse();
        String response = print(webResponse.getContentAsString());
        assertTrue(StringUtils.contains(response, "<response sid=\"123\"><collectdtmf><playaudio>"));
        assertTrue(StringUtils.contains(response, "</playaudio></collectdtmf></response>"));

        page = webClient.getPage(urlWith("123", "9876543210", "GotDTMF"));

        webResponse = page.getWebResponse();
        response = print(webResponse.getContentAsString());
        assertEquals("<response sid=\"123\"><playtext>Your mobile number is not registered.</playtext><hangup/></response>", response);
    }


    @Test
    public void shouldTestConversationForActivatedAndWrongPasscode() {
        Clinician clinician = new ClinicianPreset(webDriver).create();
        Patient patient = PatientBuilder.startRecording().withDefaults().withMobileNumber("9876543210").withPasscode("5678").build();
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.getUsername(), clinician.getPassword()).
                goToPatientRegistrationPage().
                registerNewPatient(patient);
        showPatientPage.activatePatient().logout();

        Page page = webClient.getPage(urlWith("123", "9876543210", "NewCall", ""));
        WebResponse webResponse = page.getWebResponse();
        String response = print(webResponse.getContentAsString());
        assertTrue(StringUtils.contains(response, "<response sid=\"123\"><collectdtmf><playaudio>"));

        page = webClient.getPage(urlWith("123", "9876543210", "GotDTMF","1234#"));
        webResponse = page.getWebResponse();
        response = print(webResponse.getContentAsString());
        assertTrue(StringUtils.contains(response,"<response sid=\"123\"><collectdtmf><playtext>Your passcode is incorrect. Please enter the correct passcode and press # sign.</playtext>"));
    }

    @After
    public void tearDown() throws IOException {
        webClient.shutDown();
        super.tearDown();
    }
}

