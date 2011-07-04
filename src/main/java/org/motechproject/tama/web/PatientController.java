package org.motechproject.tama.web;

import java.util.ArrayList;

import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.Patient;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RooWebScaffold(path = "patients", formBackingObject = Patient.class)
@RequestMapping("/patients")
@Controller
public class PatientController {

    @RequestMapping(method = RequestMethod.PUT, value = "/updateStatus")
    public String updateStatus(@Valid Patient patient, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        Patient.findPatient(patient.getId()).activate().merge();
        return "redirect:/patients/" + encodeUrlPathSegment(patient.getId().toString(), httpServletRequest);
    }


    @ModelAttribute("daysInAMonth")
    public ArrayList<Integer> getDaysInAMonth() {
        ArrayList<Integer> daysInAMonth = new ArrayList<Integer>();
        for (int i = 0; i <= TAMAConstants.MAX_DAYS_IN_A_MONTH; i++) {
            daysInAMonth.add(i);
        }
        return daysInAMonth;
    }

    @ModelAttribute("hoursInADay")
    public ArrayList<Integer> getHoursInADay() {
        ArrayList<Integer> hoursInADay = new ArrayList<Integer>();
        for (int i = 0; i <= TAMAConstants.MAX_HOURS_IN_A_DAY; i++) {
            hoursInADay.add(i);
        }
        return hoursInADay;
    }

    @ModelAttribute("minutesInAnHour")
    public ArrayList<Integer> getMinutesInAnHour() {
        ArrayList<Integer> minutesInAnHour = new ArrayList<Integer>();
        for (int i = 0; i <= TAMAConstants.MAX_MINUTES_IN_AN_HOUR; i++) {
            minutesInAnHour.add(i);
        }
        return minutesInAnHour;
    }
}
