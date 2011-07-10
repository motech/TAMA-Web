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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
    public static final String REDIRECT_SHOW_VIEW = "redirect:/patients/";
    public static final String REDIRECT_LIST_VIEW = "redirect:/patients";
    public static final String CREATE_VIEW = "patients/create";
    public static final String SHOW_VIEW = "patients/show";
    public static final String LIST_VIEW = "patients/list";
    public static final String UPDATE_VIEW = "patients/update";

    @Autowired
    private Patients patients;
    @Autowired
    private Clinics clinics;

    public PatientController() {
    }

    public PatientController(Patients patients) {
        this.patients = patients;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/activate")
    public String activate(@RequestParam String id, HttpServletRequest request) {
        patients.activate(id);
        return REDIRECT_SHOW_VIEW + encodeUrlPathSegment(id, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/activate/{id}")
    public String activate(@PathVariable String id) {
        patients.activate(id);
        return REDIRECT_LIST_VIEW;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findByPatientId")
    public String findByPatientId(@RequestParam String patientId, Model uiModel, HttpServletRequest request) {
        List<Patient> patients = this.patients.findById(patientId);
        if (patients == null || patients.isEmpty()) {
            uiModel.addAttribute(PATIENT_ID_NOT_FOUND, patientId);
            return "redirect:" + getReferrer(request);
        }
        return REDIRECT_SHOW_VIEW + encodeUrlPathSegment(patients.get(0).getId(), request);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Patient patient, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("patient", patient);
            populateModel(uiModel);
            addDateTimeFormat(uiModel);
            return CREATE_VIEW;
        }
        uiModel.asMap().clear();
        patients.addToClinic(patient);
        return REDIRECT_SHOW_VIEW + encodeUrlPathSegment(patient.getId(), request);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
        uiModel.addAttribute("patient", new Patient());
        populateModel(uiModel);
        addDateTimeFormat(uiModel);
        return CREATE_VIEW;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel) {
        addDateTimeFormat(uiModel);
        uiModel.addAttribute("patient", patients.get(id));
        uiModel.addAttribute("itemId", id);
        return SHOW_VIEW;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page,
                       @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        uiModel.addAttribute("patients", patients.byClinic());
        addDateTimeFormat(uiModel);
        return LIST_VIEW;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Patient patient, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("patient", patient);
            populateModel(uiModel);
            addDateTimeFormat(uiModel);
            return UPDATE_VIEW;
        }
        uiModel.asMap().clear();
        synchronise(patient);
        patients.merge(patient);
        return REDIRECT_SHOW_VIEW + encodeUrlPathSegment(patient.getId(), request);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("patient", patients.get(id));
        populateModel(uiModel);
        addDateTimeFormat(uiModel);
        return UPDATE_VIEW;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") String id,
                         @RequestParam(value = "page", required = false) Integer page,
                         @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        patients.remove(id);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        uiModel.addAttribute("patients", patients.byClinic());
        return REDIRECT_LIST_VIEW;
    }

    private String getReferrer(HttpServletRequest request) {
        String referrer = request.getHeader("Referer");
        referrer = referrer.replaceFirst("(\\?|&)" + PATIENT_ID_NOT_FOUND + "=[[0-9][^0-9]]*$", "");
        return referrer;
    }

    private void synchronise(Patient patient) {
        if (patients.checkIfActive(patient)) patient.activate();
        patient.setClinic_id(patients.findClinicFor(patient));
    }

    private void addDateTimeFormat(Model uiModel) {
        uiModel.addAttribute("patient_dateofbirth_date_format", DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
    }

    private void populateModel(Model uiModel) {
        uiModel.addAttribute("clinics", clinics.getAll());
        uiModel.addAttribute("ivrlanguages", IVRLanguage.findAllIVRLanguages());
        uiModel.addAttribute("daysInAMonth", TAMAConstants.Time.MAX_DAYS_IN_A_MONTH.list());
        uiModel.addAttribute("hoursInADay", TAMAConstants.Time.MAX_HOURS_IN_A_DAY.list());
        uiModel.addAttribute("minutesInAnHour", TAMAConstants.Time.MAX_MINUTES_IN_AN_HOUR.list());
        uiModel.addAttribute("genders", Gender.findAllGenders());
    }

    private String encodeUrlPathSegment(String pathSegment, HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
        return pathSegment;
    }

}
