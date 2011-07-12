package org.motechproject.tama.web;

import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Properties;

@RequestMapping("/ivr")
@Controller
public class IVRController {
    @Autowired
    private Patients patients;

    @Qualifier("tamaProperties")
    @Autowired
    Properties properties;

    public static final String NUM_OF_RETRIES = "num_of_retries";
    public static final String PATIENT_DOCUMENT_ID = "patient_document_id";
    public static final String MAX_NUM_OF_IVR_RETRIES_KEY = "MAX_NUM_OF_IVR_RETRIES";

    public IVRController() {
    }

    public IVRController(Patients patients, Properties properties) {
        this.patients = patients;
        this.properties = properties;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/auth")
    public String authenticate(@RequestParam String phoneNumber, @RequestParam String passcode, HttpServletRequest request) {
        HttpSession session = request.getSession();

        Patient patient = patients.findByMobileNumber(phoneNumber);
        if (passcode.equals(patient.getPasscode())) {
            session.invalidate();
            session = request.getSession();
            session.setAttribute(PATIENT_DOCUMENT_ID, patient.getId());

            //TODO: Replace the following with the correct URL while integrating
            return TAMAConstants.AUTH_STATUS.AUTHENTICATED.getValue();
        }

        int numOfRetries = getNumberOfRetries(session);
        if(thisIsTheLastTry(numOfRetries)) {
            session.invalidate();
        } else {
            session.setAttribute(NUM_OF_RETRIES, numOfRetries + 1);
        }

        //TODO: Replace the following with the correct URL while integrating
        return TAMAConstants.AUTH_STATUS.UNAUTHENTICATED.getValue();
    }

    private int getNumberOfRetries(HttpSession session) {
        Object numOfRetriesAttrValue = session.getAttribute(NUM_OF_RETRIES);
        return numOfRetriesAttrValue == null ? 0 : (Integer) numOfRetriesAttrValue;
    }

    private boolean thisIsTheLastTry(int numOfRetries) {
        return properties.getProperty(MAX_NUM_OF_IVR_RETRIES_KEY).equals(String.valueOf(numOfRetries + 1));
    }
}
