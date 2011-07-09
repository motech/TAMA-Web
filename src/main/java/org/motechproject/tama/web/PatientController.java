package org.motechproject.tama.web;

import org.joda.time.format.DateTimeFormat;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.Gender;
import org.motechproject.tama.domain.IVRLanguage;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RooWebScaffold(path = "patients", formBackingObject = Patient.class)
@RequestMapping("/patients")
@Controller
public class PatientController {
    public static final String PATIENT_ID_NOT_FOUND = "patientIdNotFound";
    public static final String REDIRECT_SHOW_PATIENT = "redirect:/patients/";
    public static final String REDIRECT_LIST_PATIENTS = "redirect:/patients";

    @Autowired
    Patients patients;
    @Autowired
    Clinics clinics;

    public PatientController() {
    }

    public PatientController(Patients patients) {
        this.patients = patients;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/activate")
    public String activate(@RequestParam String id, HttpServletRequest httpServletRequest) {
        Patient.findPatient(id).activate().merge();
        return REDIRECT_SHOW_PATIENT + encodeUrlPathSegment(id, httpServletRequest);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findByPatientId")
    public String findByPatientId(@RequestParam String patientId, Model uiModel, HttpServletRequest httpServletRequest) {
        List<Patient> patientList = patients.findByPatientId(patientId);
        if (patientList == null || patientList.isEmpty()) {
            uiModel.addAttribute(PATIENT_ID_NOT_FOUND, patientId);
            String referrer = httpServletRequest.getHeader("Referer");
            referrer = referrer.replaceFirst("(\\?|&)" + PATIENT_ID_NOT_FOUND + "=[[0-9][^0-9]]*$", "");
            return "redirect:" + referrer;
        }
        return REDIRECT_SHOW_PATIENT + encodeUrlPathSegment(patientList.get(0).getId(), httpServletRequest);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/activate/{id}")
    public String activate(@PathVariable String id) {
        Patient.findPatient(id).activate().merge();
        return REDIRECT_LIST_PATIENTS;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Patient patient, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("patient", patient);
            populateModel(uiModel);
            addDateTimeFormatPatterns(uiModel);
            return "patients/create";
        }
        uiModel.asMap().clear();
        patient.persist();
        return REDIRECT_SHOW_PATIENT + encodeUrlPathSegment(patient.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
        uiModel.addAttribute("patient", new Patient());
        populateModel(uiModel);
        addDateTimeFormatPatterns(uiModel);
        return "patients/create";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("patient", Patient.findPatient(id));
        uiModel.addAttribute("itemId", id);
        return "patients/show";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
//        if (page != null || size != null) {
//            int sizeNo = size == null ? 10 : size.intValue();
//            uiModel.addAttribute("patients", Patient.findPatientEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
//            float nrOfPages = (float) Patient.countPatients() / sizeNo;
//            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
//        } else {
//            uiModel.addAttribute("patients", patients.getPatientsForClinician());
//        }
        uiModel.addAttribute("patients", patients.getPatientsForClinic());
        addDateTimeFormatPatterns(uiModel);
        return "patients/list";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Patient patient, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("patient", patient);
            populateModel(uiModel);
            addDateTimeFormatPatterns(uiModel);
            return "patients/update";
        }
        uiModel.asMap().clear();
        if (Patient.findPatient(patient.getId()).isActive()) patient.activate();
        patient.merge();
        return REDIRECT_SHOW_PATIENT + encodeUrlPathSegment(patient.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("patient", Patient.findPatient(id));
        populateModel(uiModel);
        addDateTimeFormatPatterns(uiModel);
        return "patients/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") String id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Patient.findPatient(id).remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        uiModel.addAttribute("patients", patients.getPatientsForClinic());
        return REDIRECT_LIST_PATIENTS;
    }

    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("patient_dateofbirth_date_format", DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
    }


    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
        }
        return pathSegment;
    }

    private void populateModel(Model uiModel) {
        uiModel.addAttribute("clinics", clinics.getAll());
        uiModel.addAttribute("ivrlanguages", IVRLanguage.findAllIVRLanguages());
        uiModel.addAttribute("daysInAMonth", TAMAConstants.Time.MAX_DAYS_IN_A_MONTH.list());
        uiModel.addAttribute("hoursInADay", TAMAConstants.Time.MAX_HOURS_IN_A_DAY.list());
        uiModel.addAttribute("minutesInAnHour", TAMAConstants.Time.MAX_MINUTES_IN_AN_HOUR.list());
        uiModel.addAttribute("genders", Gender.findAllGenders());
    }

}
