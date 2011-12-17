package org.motechproject.tama.web;

import org.joda.time.format.DateTimeFormat;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.web.view.ClinicsView;
import org.motechproject.tama.web.view.HIVTestReasonsView;
import org.motechproject.tama.web.view.IvrLanguagesView;
import org.motechproject.tama.web.view.ModesOfTransmissionView;
import org.motechproject.tamacallflow.platform.service.TamaSchedulerService;
import org.motechproject.tamacallflow.service.PatientService;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamacommon.TamaException;
import org.motechproject.tamadomain.domain.*;
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
import java.util.Arrays;
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
    private static final String REVIVE_VIEW = "patients/revive";

    public static String DEACTIVATION_STATUSES =  "deactivation_statuses";
    public static final String PATIENT = "patient";
    public static final String PATIENTS = "patients";
    public static final String ITEM_ID = "itemId";
    public static final String PAGE = "page";
    public static final String SIZE = "size";
    public static final String PATIENT_ID = "patientIdNotFound";
    public static final String DATE_OF_BIRTH_FORMAT = "patient_dateofbirth_date_format";
    public static final String CLINIC_AND_PATIENT_ID_ALREADY_IN_USE = "Sorry, the entered patient-id already in use.";
    private static final String PHONE_NUMBER_AND_PASSCODE_ALREADY_IN_USE = "Sorry, the entered combination of phone number and TAMA-PIN is already in use.";
    private AllPatients allPatients;
    private AllClinics allClinics;
    private AllGenders allGenders;
    private AllIVRLanguages allIVRLanguages;
    private AllHIVTestReasons allTestReasons;
    private AllModesOfTransmission allModesOfTransmission;
    private PatientService patientService;
    private TamaSchedulerService schedulerService;

    @Autowired
    public PatientController(AllPatients allPatients, AllClinics allClinics, AllGenders allGenders, AllIVRLanguages allIVRLanguages, AllHIVTestReasons allTestReasons, AllModesOfTransmission allModesOfTransmission, TamaSchedulerService schedulerService, PatientService patientService) {
        this.allPatients = allPatients;
        this.allClinics = allClinics;
        this.allGenders = allGenders;
        this.allIVRLanguages = allIVRLanguages;
        this.allTestReasons = allTestReasons;
        this.allModesOfTransmission = allModesOfTransmission;
        this.patientService = patientService;
        this.schedulerService = schedulerService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/activate")
    public String activate(@RequestParam String id, HttpServletRequest request) {
        allPatients.activate(id);
        return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(id, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/activate/{id}")
    public String activate(@PathVariable String id) {
        allPatients.activate(id);
        return REDIRECT_TO_LIST_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/deactivate")
    public String deactivate(@RequestParam String id, @RequestParam Status status, HttpServletRequest request) {
        Patient patient = allPatients.get(id);
        patient.setStatus(status);
        patientService.update(patient);
        return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(id, request);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
        Patient patient = new Patient();
        initUIModel(uiModel, patient);
        return CREATE_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/revive/{id}")
    public String revive(@PathVariable String id, Model model, HttpServletRequest request){
        List<SuspendedAdherenceData.DosageStatusWhenSuspended> pastDosageStatus = Arrays.asList(SuspendedAdherenceData.DosageStatusWhenSuspended.values());
        model.addAttribute("suspendedAdherenceData", new SuspendedAdherenceData());
        model.addAttribute("patientId", id);
        model.addAttribute("pastDosageStatus", pastDosageStatus);
        return REVIVE_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/revive")
    public String reactivatePatient(@RequestParam String id, SuspendedAdherenceData suspendedAdherenceData, Model uiModel, HttpServletRequest request){
        suspendedAdherenceData.patientId(id);
        patientService.reActivate(id, suspendedAdherenceData);
        return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(id, request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        addDateTimeFormat(uiModel);
        uiModel.addAttribute(PATIENT, allPatients.findByIdAndClinicId(id, loggedInClinic(request)));
        uiModel.addAttribute(ITEM_ID, id);  // TODO: is this even used?
        uiModel.addAttribute(DEACTIVATION_STATUSES, Status.deactivationStatuses());
        return SHOW_VIEW;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(Model uiModel, HttpServletRequest request) {
        uiModel.addAttribute(PATIENTS, allPatients.findByClinic(loggedInClinic(request)));
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
            //TODO: This code should be moved to PatientService
            allPatients.addToClinic(patient, loggedInClinic(request));
            //TODO: Instead of calling patient to get data and checking on that, patient should have method like hasAgreedToBeCalledAtBestCallTime
            //TODO: scheduling rescheduling codes have duplication in it
            if (patient.getPatientPreferences().getCallPreference().equals(CallPreference.DailyPillReminder) &&
                    patient.getPatientPreferences().hasAgreedToBeCalledAtBestCallTime()) {
                schedulerService.scheduleJobForOutboxCall(patient);
            }
            uiModel.asMap().clear();
        } catch (TamaException e) {
            String message = e.getMessage();
            if(message.contains(Patient.CLINIC_AND_PATIENT_ID_UNIQUE_CONSTRAINT)){
                bindingResult.addError(new FieldError("Patient", "patientId", patient.getPatientId(), false,
                        new String[]{"clinic_and_patient_id_not_unique"}, new Object[]{}, CLINIC_AND_PATIENT_ID_ALREADY_IN_USE));
            }
            else if (message.contains(Patient.PHONE_NUMBER_AND_PASSCODE_UNIQUE_CONSTRAINT)){
                bindingResult.addError(new FieldError("Patient", "mobilePhoneNumber", patient.getMobilePhoneNumber(), false,
                        new String[]{"phone_number_and_passcode_not_unique"}, new Object[]{}, PHONE_NUMBER_AND_PASSCODE_ALREADY_IN_USE));
            }
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
        final Patient patient = allPatients.findByIdAndClinicId(id, loggedInClinic(request));
        initUIModel(uiModel, patient);
        uiModel.addAttribute("systemCategories", patient.getMedicalHistory().getNonHivMedicalHistory().getSystemCategories());
        return UPDATE_VIEW;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Patient patient, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            initUIModel(uiModel, patient);
            return UPDATE_VIEW;
        }
        try {
            patientService.update(patient);
            uiModel.asMap().clear();
        } catch (TamaException e) {
            String message = e.getMessage();
            if (message.contains(Patient.PHONE_NUMBER_AND_PASSCODE_UNIQUE_CONSTRAINT)){
                bindingResult.addError(new FieldError("Patient", "mobilePhoneNumber", patient.getMobilePhoneNumber(), false,
                        new String[]{"phone_number_and_passcode_not_unique"}, new Object[]{}, PHONE_NUMBER_AND_PASSCODE_ALREADY_IN_USE));
            }
            initUIModel(uiModel, patient);
            return UPDATE_VIEW;
        }

        return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(patient.getId(), request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findByPatientId")
    public String findByPatientId(@RequestParam String patientId, Model uiModel, HttpServletRequest request) {
        List<Patient> patientsByClinic = allPatients.findByPatientIdAndClinicId(patientId, loggedInClinic(request));

        if (patientsByClinic == null || patientsByClinic.isEmpty()) {
            uiModel.addAttribute(PATIENT_ID, patientId);
            return redirectToListPatientsPage(request);
        }
        return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(patientsByClinic.get(0).getId(), request);
    }

	private String redirectToListPatientsPage(HttpServletRequest request) {
		return "redirect:" + getReferrer(request);
	}

    private String getReferrer(HttpServletRequest request) {
        String referrer = request.getHeader("Referer");
        referrer = referrer.replaceFirst("(\\?|&)" + PATIENT_ID + "=[[0-9][^0-9]]*$", "");
        return referrer;
    }

    private void populateModel(Model uiModel) {
        uiModel.addAttribute("clinics", new ClinicsView(allClinics).getAll());
        uiModel.addAttribute("ivrlanguages", new IvrLanguagesView(allIVRLanguages).getAll());
        uiModel.addAttribute("daysInAMonth", TAMAConstants.Time.MAX_DAYS_IN_A_MONTH.list());
        uiModel.addAttribute("hoursInADay", TAMAConstants.Time.MAX_HOURS_IN_A_DAY.list());
        uiModel.addAttribute("minutesInAnHour", TAMAConstants.Time.MAX_MINUTES_IN_AN_HOUR.list());
        uiModel.addAttribute("genders", allGenders.getAll());
        uiModel.addAttribute("testReasons", new HIVTestReasonsView(allTestReasons).getAll());
        uiModel.addAttribute("modesOfTransmission", new ModesOfTransmissionView(allModesOfTransmission).getAll());
        uiModel.addAttribute("drugAllergies", TAMAConstants.DrugAllergy.values());
        uiModel.addAttribute("nnrtiRashes", TAMAConstants.NNRTIRash.values());
        uiModel.addAttribute("systemCategories", SystemCategoryDefinition.all());
        uiModel.addAttribute("options", AilmentState.values());
        uiModel.addAttribute("questions", MedicalHistoryQuestions.all());
        uiModel.addAttribute("daysOfWeek", Arrays.asList(DayOfWeek.values()));
        uiModel.addAttribute("timeMeridiems", Arrays.asList(TimeMeridiem.values()));
    }

    private void addDateTimeFormat(Model uiModel) {
        uiModel.addAttribute(DATE_OF_BIRTH_FORMAT, DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
    }
}