package org.motechproject.tama.web;

import org.ektorp.UpdateConflictException;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.*;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RooWebScaffold(path = "patients", formBackingObject = Patient.class)
@RequestMapping("/patients")
@Controller
public class PatientController extends BaseController {
    public static final String CREATE_VIEW = "patients/create";
    public static final String SHOW_VIEW = "patients/show";
    public static final String LIST_VIEW = "patients/list";
    public static final String UPDATE_VIEW = "patients/update";
    public static final String REDIRECT_TO_LIST_VIEW = "redirect:/patients";
    public static final String REDIRECT_TO_SHOW_VIEW = "redirect:/patients/";

    public static final String PATIENT = "patient";
    public static final String PATIENTS = "patients";
    public static final String ITEM_ID = "itemId";
    public static final String PAGE = "page";
    public static final String SIZE = "size";
    public static final String PATIENT_ID = "patientIdNotFound";
    public static final String DATE_OF_BIRTH_FORMAT = "patient_dateofbirth_date_format";
    public static final String PATIENT_ID_ALREADY_IN_USE = "sorry, patient-id already in use";

    private Patients patients;
    private Clinics clinics;
    private Genders genders;
    private IVRLanguages ivrLanguages;
    private HIVTestReasons testReasons;
    private ModesOfTransmission modesOfTransmission;

    @Autowired
    public PatientController(Patients patients, Clinics clinics, Genders genders, IVRLanguages ivrLanguages, HIVTestReasons testReasons, ModesOfTransmission modesOfTransmission) {
        this.patients = patients;
        this.clinics = clinics;
        this.genders = genders;
        this.ivrLanguages = ivrLanguages;
        this.testReasons = testReasons;
        this.modesOfTransmission = modesOfTransmission;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/activate")
    public String activate(@RequestParam String id, HttpServletRequest request) {
        patients.activate(id);
        return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(id, request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/activate/{id}")
    public String activate(@PathVariable String id) {
        patients.activate(id);
        return REDIRECT_TO_LIST_VIEW;
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
        initUIModel(uiModel, new Patient());
        return CREATE_VIEW;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        addDateTimeFormat(uiModel);
        uiModel.addAttribute(PATIENT, patients.findByIdAndClinicId(id, loggedInClinic(request)));
        uiModel.addAttribute(ITEM_ID, id);
        return SHOW_VIEW;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(Model uiModel, HttpServletRequest request) {
        uiModel.addAttribute(PATIENTS, patients.findByClinic(loggedInClinic(request)));
        addDateTimeFormat(uiModel);
        return LIST_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Patient patient, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            initUIModel(uiModel, patient);
            return CREATE_VIEW;
        }
        try {
            patients.addToClinic(patient, loggedInClinic(request));
            uiModel.asMap().clear();
        } catch (UpdateConflictException e) {
            bindingResult.addError(new FieldError("Patient", "patientId", patient.getPatientId(), false,
                    new String[]{"patient_id_not_unique"}, new Object[]{}, PATIENT_ID_ALREADY_IN_USE));
            initUIModel(uiModel, patient);
            return CREATE_VIEW;
        }
        return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(patient.getId(), request);
    }

    private void initUIModel(Model uiModel, Patient patient) {
        uiModel.addAttribute(PATIENT, patient);
        populateModel(uiModel);
        addDateTimeFormat(uiModel);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        initUIModel(uiModel, patients.findByIdAndClinicId(id, loggedInClinic(request)));
        return UPDATE_VIEW;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Patient patient, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            initUIModel(uiModel, patient);
            return UPDATE_VIEW;
        }
        uiModel.asMap().clear();
        patients.merge(patient);

        return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(patient.getId(), request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") String id,
                         @RequestParam(value = "page", required = false) Integer page,
                         @RequestParam(value = "size", required = false) Integer size, Model uiModel,
                         HttpServletRequest request) {
        patients.remove(id);
        uiModel.asMap().clear();
        uiModel.addAttribute(PAGE, (page == null) ? "1" : page.toString());
        uiModel.addAttribute(SIZE, (size == null) ? "10" : size.toString());
        uiModel.addAttribute(PATIENTS, patients.findByClinic(loggedInClinic(request)));
        return REDIRECT_TO_LIST_VIEW;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findByPatientId")
    public String findByPatientId(@RequestParam String patientId, Model uiModel, HttpServletRequest request) {
        List<Patient> patientsByClinic = patients.findByPatientIdAndClinicId(patientId, loggedInClinic(request));

        if (patientsByClinic == null || patientsByClinic.isEmpty()) {
            uiModel.addAttribute(PATIENT_ID, patientId);
            return "redirect:" + getReferrer(request);
        }
        return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(patientsByClinic.get(0).getId(), request);
    }

    private String loggedInClinic(HttpServletRequest request) {
        AuthenticatedUser user = (AuthenticatedUser) request.getSession().getAttribute(LoginSuccessHandler.LOGGED_IN_USER);
        return user.getClinicId();
    }

    private String getReferrer(HttpServletRequest request) {
        String referrer = request.getHeader("Referer");
        referrer = referrer.replaceFirst("(\\?|&)" + PATIENT_ID + "=[[0-9][^0-9]]*$", "");
        return referrer;
    }

    private void populateModel(Model uiModel) {
        uiModel.addAttribute("clinics", clinics.getAll());
        uiModel.addAttribute("ivrlanguages", ivrLanguages.getAll());
        uiModel.addAttribute("daysInAMonth", TAMAConstants.Time.MAX_DAYS_IN_A_MONTH.list());
        uiModel.addAttribute("hoursInADay", TAMAConstants.Time.MAX_HOURS_IN_A_DAY.list());
        uiModel.addAttribute("minutesInAnHour", TAMAConstants.Time.MAX_MINUTES_IN_AN_HOUR.list());
        uiModel.addAttribute("genders", genders.getAll());
        uiModel.addAttribute("testReasons", testReasons.getAll());
        uiModel.addAttribute("modesOfTransmission", modesOfTransmission.getAll());
        uiModel.addAttribute("drugAllergies", TAMAConstants.DrugAllergy.values());
        uiModel.addAttribute("nnrtiRashes", TAMAConstants.NNRTIRash.values());
    }

    private void addDateTimeFormat(Model uiModel) {
        uiModel.addAttribute(DATE_OF_BIRTH_FORMAT, DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
    }
}
