package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.fourdayrecall.service.ResumeFourDayRecallService;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.objectcache.AllGendersCache;
import org.motechproject.tama.refdata.objectcache.AllHIVTestReasonsCache;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.refdata.objectcache.AllModesOfTransmissionCache;
import org.motechproject.tama.web.model.DoseStatus;
import org.motechproject.tama.web.model.IncompletePatientDataWarning;
import org.motechproject.tama.web.model.ListPatientViewModel;
import org.motechproject.tama.web.model.PatientSummary;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/patients")
@Controller
public class PatientController extends BaseController {
    public static final String CREATE_VIEW = "patients/create";
    public static final String SHOW_VIEW = "patients/show";
    public static final String SUMMARY_VIEW = "patients/summary";
    public static final String LIST_VIEW = "patients/list";
    public static final String UPDATE_VIEW = "patients/update";
    private static final String REVIVE_VIEW = "patients/revive";
    public static final String REDIRECT_TO_LIST_VIEW = "redirect:/patients";
    public static final String REDIRECT_TO_SHOW_VIEW = "redirect:/patients/";
    public static final String REDIRECT_TO_SUMMARY_VIEW = "redirect:/patients/summary/";

    public static String DEACTIVATION_STATUSES = "deactivation_statuses";
    public static final String PATIENT = "patient";
    public static final String PATIENTS = "patients";
    public static final String WARNING = "warning";
    public static final String ITEM_ID = "itemId";
    public static final String PATIENT_HAS_STARTED_TREATMENT = "patient_has_started_treatment";
    public static final String PATIENT_ID = "patientIdNotFound";
    public static final String DATE_OF_BIRTH_FORMAT = "patient_dateofbirth_date_format";
    public static final String CLINIC_AND_PATIENT_ID_ALREADY_IN_USE = "Sorry, the entered patient-id already in use.";
    private static final String PHONE_NUMBER_AND_PASSCODE_ALREADY_IN_USE = "Sorry, the entered combination of phone number and TAMA-PIN is already in use.";
    public static final String PATIENT_INSERT_ERROR_KEY = "patientInsertError";
    private static final String PATIENT_INSERT_ERROR = "Sorry, there was an error while creating/updating the patient. Please try again.";

    private AllPatients allPatients;
    private AllGendersCache allGenders;
    private AllIVRLanguagesCache allIVRLanguages;
    private AllHIVTestReasonsCache allTestReasons;
    private AllModesOfTransmissionCache allModesOfTransmission;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllVitalStatistics allVitalStatistics;
    private AllLabResults allLabResults;
    private PatientService patientService;
    private DailyPillReminderAdherenceService dailyPillReminderAdherenceService;
    private AllClinicVisits allClinicVisits;
    private ResumeFourDayRecallService resumeFourDayRecallService;
    private Integer minNumberOfDaysOnDailyBeforeTransitioningToWeekly;

    @Autowired
    public PatientController(AllPatients allPatients, AllGendersCache allGenders, AllIVRLanguagesCache allIVRLanguages, AllHIVTestReasonsCache allTestReasons, AllModesOfTransmissionCache allModesOfTransmission, AllTreatmentAdvices allTreatmentAdvices,
                             AllVitalStatistics allVitalStatistics, AllLabResults allLabResults, PatientService patientService, DailyPillReminderAdherenceService dailyPillReminderAdherenceService, ResumeFourDayRecallService resumeFourDayRecallService,
                             @Value("#{dailyPillReminderProperties['" + TAMAConstants.MIN_NUMBER_OF_DAYS_ON_DAILY_BEFORE_TRANSITIONING_TO_WEEKLY + "']}") Integer minNumberOfDaysOnDailyBeforeTransitioningToWeekly, AllClinicVisits allClinicVisits) {
        this.allPatients = allPatients;
        this.allGenders = allGenders;
        this.allIVRLanguages = allIVRLanguages;
        this.allTestReasons = allTestReasons;
        this.allModesOfTransmission = allModesOfTransmission;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allVitalStatistics = allVitalStatistics;
        this.allLabResults = allLabResults;
        this.patientService = patientService;
        this.dailyPillReminderAdherenceService = dailyPillReminderAdherenceService;
        this.resumeFourDayRecallService = resumeFourDayRecallService;
        this.minNumberOfDaysOnDailyBeforeTransitioningToWeekly = minNumberOfDaysOnDailyBeforeTransitioningToWeekly;
        this.allClinicVisits = allClinicVisits;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/activate")
    public String activate(@RequestParam String id, HttpServletRequest request) {
        return activatePatient(id, REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(id, request), request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/activate/{id}")
    public String activateAndRedirectToListPatient(@PathVariable String id, HttpServletRequest request) {
        return activatePatient(id, REDIRECT_TO_LIST_VIEW, request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/deactivate")
    public String deactivate(@RequestParam String id, @RequestParam Status status, HttpServletRequest request) {
        try {
            patientService.deactivate(id, status, loggedInUserId(request));
        } catch (RuntimeException e) {
            request.setAttribute("flash.flashError", "Error occurred while deactivating patient: " + e.getMessage());
        }
        return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(id, request);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
        Patient patient = new Patient();
        final PatientPreferences patientPreferences = new PatientPreferences();
        patientPreferences.setCallPreference(CallPreference.DailyPillReminder);
        patient.setPatientPreferences(patientPreferences);
        initUIModel(uiModel, patient);
        return CREATE_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/revive/{id}")
    public String revive(@PathVariable String id, Model model) {
        List<DoseStatus> doseStatuses = Arrays.asList(DoseStatus.values());
        model.addAttribute("patientId", id);
        model.addAttribute("pastDosageStatus", doseStatuses);
        return REVIVE_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reactivatePatient")
    public String reactivatePatient(@RequestParam String id, @RequestParam DoseStatus doseStatus, HttpServletRequest request) {
        Patient patient = allPatients.get(id);
        final TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(id);
        if (treatmentAdvice == null){
            return activate(id, request);
        }
        final DateTime startDate = patient.getStatus().isTemporarilyDeactivated() ? patient.getLastDeactivationDate() : patient.getLastSuspendedDate();
        try {
            if (patient.isOnDailyPillReminder()) {
                dailyPillReminderAdherenceService.backFillAdherence(id, startDate, DateUtil.now(), doseStatus.isTaken());
            } else {
                resumeFourDayRecallService.backFillAdherence(patient, startDate, DateUtil.now(), doseStatus.isTaken());
            }
            patientService.activate(id, loggedInUserId(request));
        } catch (RuntimeException e) {
            request.setAttribute("flash.flashError", "Error occurred while reactivating patient: " + e.getMessage());
        }

        return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(id, request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        addDateTimeFormat(uiModel);
        Patient patient = allPatients.findByIdAndClinicId(id, loggedInClinic(request));
        if (patient == null) return "authorizationFailure";
        String warning = new IncompletePatientDataWarning(patient, allVitalStatistics, allTreatmentAdvices, allLabResults, allClinicVisits).toString();
        uiModel.addAttribute(PATIENT, patient);
        uiModel.addAttribute(ITEM_ID, id);  // TODO: is this even used?
        uiModel.addAttribute(DEACTIVATION_STATUSES, Status.deactivationStatuses());
        uiModel.addAttribute(WARNING, warning);
        //TODO: PATIENT_HAS_STARTED_TREATMENT logic to a service layer
        uiModel.addAttribute(PATIENT_HAS_STARTED_TREATMENT, allTreatmentAdvices.currentTreatmentAdvice(patient.getId()) != null);
        return SHOW_VIEW;
    }

    @RequestMapping(value = "/summary/{id}", method = RequestMethod.GET)
    public ModelAndView showSummary(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        addDateTimeFormat(uiModel);
        Patient patient = allPatients.findByIdAndClinicId(id, loggedInClinic(request));
        if (patient == null) return new ModelAndView("authorizationFailure", "", null);
        TreatmentAdvice earliestTreatmentAdvice = allTreatmentAdvices.earliestTreatmentAdvice(id);
        TreatmentAdvice currentTreatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(id);
        Regimen currentRegimen = patientService.currentRegimen(patient);
        ClinicVisits clinicVisits = allClinicVisits.clinicVisits(patient.getId());
        String warning = new IncompletePatientDataWarning(patient, allVitalStatistics, allTreatmentAdvices, allLabResults, allClinicVisits).toString();
        PatientSummary patientSummary = new PatientSummary(patient, earliestTreatmentAdvice, currentTreatmentAdvice, currentRegimen, clinicVisits, warning);
        //Do not change name of form bean - currently used by graphs to get data.
        //TODO : Change <graph>.jspx partials to accept patient form bean name as parameter/variable.
        return new ModelAndView(SUMMARY_VIEW, "patient", patientSummary);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(Model uiModel, HttpServletRequest request) {
        String clinicId = loggedInClinic(request);
        List<ListPatientViewModel> listPatientViewModels = new ArrayList<ListPatientViewModel>();
        for (Patient patient : allPatients.findByClinic(clinicId)) {
            ListPatientViewModel listPatientViewModel = new ListPatientViewModel(patient);
            listPatientViewModels.add(listPatientViewModel);
        }
        uiModel.addAttribute(PATIENTS, listPatientViewModels);
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
            patientService.create(patient, loggedInClinic(request), loggedInUserId(request));
            uiModel.asMap().clear();
        } catch (RuntimeException e) {
            decorateViewWithUniqueConstraintError(patient, bindingResult, uiModel, e);
            return CREATE_VIEW;
        }
        return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(patient.getId(), request);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        Patient patient = allPatients.findByIdAndClinicId(id, loggedInClinic(request));
        if (patient == null) return "authorizationFailure";
        initUIModel(uiModel, patient);
        uiModel.addAttribute("systemCategories", patient.getMedicalHistory().getNonHivMedicalHistory().getSystemCategories());
        uiModel.addAttribute("canTransitionToWeekly", patient.canTransitionToWeekly(minNumberOfDaysOnDailyBeforeTransitioningToWeekly));
        return UPDATE_VIEW;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Patient patient, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            initUIModel(uiModel, patient);
            return UPDATE_VIEW;
        }
        try {
            patientService.update(patient, loggedInUserId(request));
            uiModel.asMap().clear();
        } catch (RuntimeException e) {
            decorateViewWithUniqueConstraintError(patient, bindingResult, uiModel, e);
            return UPDATE_VIEW;
        }
        return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(patient.getId(), request);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findByPatientId")
    public String findByPatientId(@RequestParam String patientId, Model uiModel, HttpServletRequest request) {
        Patient patient = allPatients.findByPatientIdAndClinicId(patientId, loggedInClinic(request));
        if (patient == null) {
            uiModel.addAttribute(PATIENT_ID, patientId);
            return redirectToListPatientsPage(request);
        }
        return REDIRECT_TO_SUMMARY_VIEW + encodeUrlPathSegment(patient.getId(), request);
    }

    private String activatePatient(String patientDocId, String redirectPage, HttpServletRequest request) {
        try {
            Patient patient = allPatients.get(patientDocId);
            boolean firstActivation = patient.getActivationDate() == null;
            if (firstActivation) {
                return activatePatientForFirstTime(patientDocId, request);
            }
            patientService.activate(patientDocId, loggedInUserId(request));
        } catch (RuntimeException e) {
            request.setAttribute("flash.flashError", "Error occurred while activating patient: " + e.getMessage());
        }
        return redirectPage;
    }

    private String activatePatientForFirstTime(String patientDocId, HttpServletRequest request) {
        allClinicVisits.addAppointmentCalendar(patientDocId);
        ClinicVisit clinicVisit = allClinicVisits.getBaselineVisit(patientDocId);
        patientService.activate(patientDocId, loggedInUserId(request));
        return "redirect:/clinicvisits?form&patientId=" + patientDocId + "&clinicVisitId=" + encodeUrlPathSegment(clinicVisit.getId(), request);
    }

    private void decorateViewWithUniqueConstraintError(Patient patient, BindingResult bindingResult, Model uiModel, RuntimeException e) {
        String message = e.getMessage();
        if (message.contains(Patient.CLINIC_AND_PATIENT_ID_UNIQUE_CONSTRAINT)) {
            bindingResult.addError(new FieldError("Patient", "patientId", patient.getPatientId(), false,
                    new String[]{"clinic_and_patient_id_not_unique"}, new Object[]{}, CLINIC_AND_PATIENT_ID_ALREADY_IN_USE));
        } else if (message.contains(Patient.PHONE_NUMBER_AND_PASSCODE_UNIQUE_CONSTRAINT)) {
            bindingResult.addError(new FieldError("Patient", "mobilePhoneNumber", patient.getMobilePhoneNumber(), false,
                    new String[]{"phone_number_and_passcode_not_unique"}, new Object[]{}, PHONE_NUMBER_AND_PASSCODE_ALREADY_IN_USE));
        } else {
            bindingResult.addError(new ObjectError("Patient", ""));
            uiModel.addAttribute(PATIENT_INSERT_ERROR_KEY, PATIENT_INSERT_ERROR);
        }
        initUIModel(uiModel, patient);
    }

    private void initUIModel(Model uiModel, Patient patient) {
        uiModel.addAttribute(PATIENT, patient);
        populateModel(uiModel);
        addDateTimeFormat(uiModel);
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
        uiModel.addAttribute("ivrlanguages", allIVRLanguages.getAll());
        uiModel.addAttribute("daysInAMonth", TAMAConstants.Time.MAX_DAYS_IN_A_MONTH.list());
        uiModel.addAttribute("hoursInADay", TAMAConstants.Time.MAX_HOURS_IN_A_DAY.list());
        uiModel.addAttribute("minutesInAnHour", TAMAConstants.Time.MAX_MINUTES_IN_AN_HOUR.list());
        uiModel.addAttribute("genders", allGenders.getAll());
        uiModel.addAttribute("testReasons", allTestReasons.getAll());
        uiModel.addAttribute("modesOfTransmission", allModesOfTransmission.getAll());
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