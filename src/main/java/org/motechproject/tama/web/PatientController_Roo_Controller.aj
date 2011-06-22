// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama.web;

import java.io.UnsupportedEncodingException;
import java.lang.Integer;
import java.lang.String;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.tama.Doctor;
import org.motechproject.tama.Gender;
import org.motechproject.tama.Patient;
import org.motechproject.tama.Title;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect PatientController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST)
    public String PatientController.create(@Valid Patient patient, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("patient", patient);
            addDateTimeFormatPatterns(uiModel);
            return "patients/create";
        }
        uiModel.asMap().clear();
        patient.persist();
        return "redirect:/patients/" + encodeUrlPathSegment(patient.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String PatientController.createForm(Model uiModel) {
        uiModel.addAttribute("patient", new Patient());
        addDateTimeFormatPatterns(uiModel);
        return "patients/create";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String PatientController.show(@PathVariable("id") String id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("patient", Patient.findPatient(id));
        uiModel.addAttribute("itemId", id);
        return "patients/show";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String PatientController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("patients", Patient.findPatientEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) Patient.countPatients() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("patients", Patient.findAllPatients());
        }
        addDateTimeFormatPatterns(uiModel);
        return "patients/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String PatientController.update(@Valid Patient patient, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("patient", patient);
            addDateTimeFormatPatterns(uiModel);
            return "patients/update";
        }
        uiModel.asMap().clear();
        patient.merge();
        return "redirect:/patients/" + encodeUrlPathSegment(patient.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String PatientController.updateForm(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("patient", Patient.findPatient(id));
        addDateTimeFormatPatterns(uiModel);
        return "patients/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String PatientController.delete(@PathVariable("id") String id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Patient.findPatient(id).remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/patients";
    }
    
    @ModelAttribute("doctors")
    public Collection<Doctor> PatientController.populateDoctors() {
        return Doctor.findAllDoctors();
    }
    
    @ModelAttribute("genders")
    public Collection<Gender> PatientController.populateGenders() {
        return Gender.findAllGenders();
    }
    
    @ModelAttribute("patients")
    public Collection<Patient> PatientController.populatePatients() {
        return Patient.findAllPatients();
    }
    
    @ModelAttribute("titles")
    public Collection<Title> PatientController.populateTitles() {
        return Title.findAllTitles();
    }
    
    void PatientController.addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("patient_dateofbirth_date_format", DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
    }
    
    String PatientController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        }
        catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}
