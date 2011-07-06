package org.motechproject.tama.web;

import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.Patient;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RooWebScaffold(path = "patients", formBackingObject = Patient.class)
@RequestMapping("/patients")
@Controller
public class PatientController {

    @RequestMapping(method = RequestMethod.POST, value = "/activate")
    public String activate(@RequestParam String id, HttpServletRequest httpServletRequest) {
        Patient.findPatient(id).activate().merge();
        return "redirect:/patients/" + encodeUrlPathSegment(id, httpServletRequest);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findByPatientId")
    public String findByPatientId(@RequestParam String patientId, Model uiModel, HttpServletRequest httpServletRequest) {
        List<Patient> patients = Patient.findByPatientId(patientId);
        if (patients == null || patients.isEmpty()) {
            uiModel.addAttribute("patientIdNotFound", patientId);
            String referer = httpServletRequest.getHeader("Referer");
            return "redirect:"+referer;
        }

        return "redirect:/patients/" + encodeUrlPathSegment(patients.get(0).getId(), httpServletRequest);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/activate/{id}")
    public String activate(@PathVariable String id) {
        Patient.findPatient(id).activate().merge();
        return "redirect:/patients";
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
