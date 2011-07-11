package org.motechproject.tama.web;

import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RequestMapping("/ivr")
@Controller
public class IVRController {
    @Autowired
    private Patients patients;
    public static final String NUM_OF_RETRIES = "num_of_retries";

    public IVRController(Patients patients) {
        this.patients = patients;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/auth")
    public String authenticate(@RequestParam String phoneNumber, @RequestParam String passcode, HttpServletRequest request) {
        HttpSession session = request.getSession(true);

        Patient patient = patients.findByMobileNumber(phoneNumber);
        if (passcode.equals(patient.getPasscode())) {
            //TODO: Replace the following with the correecct URL while integrating
            return TAMAConstants.AUTH_STATUS.AUTHENTICATED.getValue();
        }

        Object numOfRetriesAttrValue = session.getAttribute(NUM_OF_RETRIES);
        int numOfRetries = numOfRetriesAttrValue == null ? 0 : (Integer)numOfRetriesAttrValue;
        session.setAttribute(NUM_OF_RETRIES, numOfRetries+1);
        return TAMAConstants.AUTH_STATUS.UNAUTHENTICATED.getValue();
    }
}
