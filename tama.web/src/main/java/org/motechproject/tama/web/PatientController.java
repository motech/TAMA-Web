package org.motechproject.tama.web;

import org.apache.commons.collections.CollectionUtils;
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
import org.motechproject.tama.ivr.service.AdherenceService;
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
import org.motechproject.tama.web.model.*;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@RequestMapping("/patients")
@Controller
public class PatientController extends BaseController {
    public static final String CREATE_VIEW = "patients/expressCreate";
    public static final String SHOW_VIEW = "patients/show";
    public static final String SUMMARY_VIEW = "patients/summary";
    public static final String LIST_VIEW = "patients/list";
    public static final String UPDATE_VIEW = "patients/update";
    private static final String REVIVE_VIEW = "patients/revive";
    private static final String DEACTIVATE_VIEW = "patients/deactivate";
    public static final String REDIRECT_TO_LIST_VIEW = "redirect:/patients";
    public static final String REDIRECT_TO_SHOW_VIEW = "redirect:/patients/";
    public static final String EXPRESS_SHOW_VIEW = "patients/expressShow";
    public static final String REDIRECT_TO_SUMMARY_VIEW = "redirect:/patients/summary/";

    public static String DEACTIVATION_STATUSES = "deactivation_statuses";
    public static final String PATIENT = "patient";
    public static final String PATIENTS = "patients";
    public static final String WARNING = "warning";
    public static final String ITEM_ID = "itemId";
    public static final String EXPRESS_REGISTRATION = "express_registration";
    public static final String PATIENT_HAS_STARTED_TREATMENT = "patient_has_started_treatment";
    public static final String PATIENT_ID = "patientIdNotFound";
    public static final String DATE_OF_BIRTH_FORMAT = "patient_dateofbirth_date_format";
    public static final String CLINIC_AND_PATIENT_ID_ALREADY_IN_USE = "Sorry, the entered patient-id already in use.";
    private static final String PHONE_NUMBER_AND_PASSCODE_ALREADY_IN_USE = "Sorry, the entered combination of phone number and TAMA-PIN is already in use.";
    public static final String PATIENT_INSERT_ERROR_KEY = "patientInsertError";
    private static final String PATIENT_INSERT_ERROR = "Sorry, there was an error while creating/updating the patient. Please try again.";
    public static final String PATIENT_WARNING_WARNING_RESOLVE_HELP = "Please add missing data by accessing CLINIC VISIT/APPOINTMENTS tab and then clicking on link ACTIVATED IN TAMA ";
    public static final String PATIENT_HAS_NOT_BEEN_ACTIVATED = "Patient has not been Activated";
    public static final String WARNING_DUPLICATE_PHONE_NUMBERS = "The below patients are registered with the same mobile number in TAMA";
    public static final String WARNING_DUPLICATE_PHONE_NUMBERS_SUGGESTION = "Every patient should have unique mobile number to avoid confusion to all.";

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
    private AdherenceService adherenceService;

    @Autowired
    public PatientController(AllPatients allPatients, AllGendersCache allGenders, AllIVRLanguagesCache allIVRLanguages, AllHIVTestReasonsCache allTestReasons, AllModesOfTransmissionCache allModesOfTransmission, AllTreatmentAdvices allTreatmentAdvices,
                             AllVitalStatistics allVitalStatistics, AllLabResults allLabResults, PatientService patientService, DailyPillReminderAdherenceService dailyPillReminderAdherenceService, ResumeFourDayRecallService resumeFourDayRecallService,
                             AdherenceService adherenceService, @Value("#{dailyPillReminderProperties['" + TAMAConstants.MIN_NUMBER_OF_DAYS_ON_DAILY_BEFORE_TRANSITIONING_TO_WEEKLY + "']}") Integer minNumberOfDaysOnDailyBeforeTransitioningToWeekly,
                             AllClinicVisits allClinicVisits) {
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
        this.adherenceService = adherenceService;
        this.minNumberOfDaysOnDailyBeforeTransitioningToWeekly = minNumberOfDaysOnDailyBeforeTransitioningToWeekly;
        this.allClinicVisits = allClinicVisits;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/activate")
    public String activate(@RequestParam String id, HttpServletRequest request) {
        return activatePatient(id, REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(id, request), request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/expressActivate/{id}")
    public String expressActivate(@PathVariable String id, Model uiModel, HttpServletRequest request) {
        activatePatient(id, null, request);
        Patient activatedPatient = allPatients.findByIdAndClinicId(id, loggedInClinic(request));
        List<String> warning = new IncompletePatientDataWarning(activatedPatient, null, null, null, null).value();
        if (!CollectionUtils.isEmpty(warning)) {
            warning.add(PATIENT_WARNING_WARNING_RESOLVE_HELP);
        }
        uiModel.addAttribute("warning", warning);
        uiModel.addAttribute(EXPRESS_REGISTRATION, "true");
        initUIModel(uiModel, activatedPatient);
        return EXPRESS_SHOW_VIEW;
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

    @RequestMapping(method = RequestMethod.POST, value = "/deactivate/{id}")
    public String redirectToDeactivatePage(@PathVariable String id, Model model, HttpServletRequest request) {
        model.addAttribute("patientId", id);
        model.addAttribute("patient", allPatients.findByIdAndClinicId(id, loggedInClinic(request)));
        model.addAttribute(DEACTIVATION_STATUSES, Status.deactivationStatuses());
        model.addAttribute("prefix", "/patients");
        return DEACTIVATE_VIEW;
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
        Patient patient = new Patient();
        final PatientPreferences patientPreferences = new PatientPreferences();
        patientPreferences.setCallPreference(CallPreference.DailyPillReminder);
        patient.setPatientPreferences(patientPreferences);
        uiModel.addAttribute(EXPRESS_REGISTRATION, "true");
        initUIModel(uiModel, patient);
        uiModel.addAttribute("selectedMenuItem", "NEW_PATIENT");
        return CREATE_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/revive/{id}")
    public String revive(@PathVariable String id, Model model, HttpServletRequest request) {
        List<DoseStatus> doseStatuses = Arrays.asList(DoseStatus.values());
        model.addAttribute("patientId", id);
        model.addAttribute("patient", allPatients.findByIdAndClinicId(id, loggedInClinic(request)));
        model.addAttribute("pastDosageStatus", doseStatuses);
        return REVIVE_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reactivatePatient")
    public String reactivatePatient(@RequestParam String id, @RequestParam DoseStatus doseStatus, HttpServletRequest request) {
        Patient patient = allPatients.get(id);
        final TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(id);
        if (treatmentAdvice == null) {
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
        List<String> warningMessage = null;
        List<String> adviceMessage = null;
        Patient patient = allPatients.findByIdAndClinicId(id, loggedInClinic(request));
        if (patient == null) return "authorizationFailure";
        List<String> warning = new IncompletePatientDataWarning(patient, allVitalStatistics, allTreatmentAdvices, allLabResults, allClinicVisits).value();
        if (!CollectionUtils.isEmpty(warning)) {
            if (!warning.get(0).equals(PATIENT_HAS_NOT_BEEN_ACTIVATED))
            {
                warning.add(PATIENT_WARNING_WARNING_RESOLVE_HELP);
            }

        }
        List<String> patientsWithSameMobileNumber = new UniquePatientMobileNumberWarning(allPatients).findAllMobileNumbersWhichMatchTheGivenNumber(patient.getMobilePhoneNumber(), patient.getPatientId(), patient.getClinic().getName());
        if(!CollectionUtils.isEmpty(patientsWithSameMobileNumber))
        {
            warningMessage = new ArrayList<>();
            warningMessage.add(PatientController.WARNING_DUPLICATE_PHONE_NUMBERS);
            adviceMessage = new ArrayList<>();
            adviceMessage.add(PatientController.WARNING_DUPLICATE_PHONE_NUMBERS_SUGGESTION);
        }
        uiModel.addAttribute("patientsWithSameMobileNumber", patientsWithSameMobileNumber);
        uiModel.addAttribute("warningMessage", warningMessage);
        uiModel.addAttribute("adviceMessage", adviceMessage);
        uiModel.addAttribute(PATIENT, new PatientViewModel(patient));
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
        List<String> warningMessage = null;
        List<String> adviceMessage = null;
        Patient patient = allPatients.findByIdAndClinicId(id, loggedInClinic(request));
        if (patient == null) return new ModelAndView("authorizationFailure", "", null);
        TreatmentAdvice earliestTreatmentAdvice = allTreatmentAdvices.earliestTreatmentAdvice(id);
        TreatmentAdvice currentTreatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(id);
        Regimen currentRegimen = patientService.currentRegimen(patient);
        List<PatientEventLog> patientStatusChangeHistory = patientService.getStatusHistory(patient.getId());
        ClinicVisits clinicVisits = allClinicVisits.clinicVisits(patient.getId());
        Double runningAdherencePercentage = getRunningAdherencePercentage(patient);
        List<String> warning = new IncompletePatientDataWarning(patient, allVitalStatistics, allTreatmentAdvices, allLabResults, allClinicVisits).value();
        if (!CollectionUtils.isEmpty(warning)) {
            if (!warning.get(0).equals(PATIENT_HAS_NOT_BEEN_ACTIVATED))
            {
                warning.add(PATIENT_WARNING_WARNING_RESOLVE_HELP);
            }
        }
        List<String> patientsWithSameMobileNumber = new UniquePatientMobileNumberWarning(allPatients).findAllMobileNumbersWhichMatchTheGivenNumber(patient.getMobilePhoneNumber(), patient.getPatientId(), patient.getClinic().getName());
        if(!CollectionUtils.isEmpty(patientsWithSameMobileNumber))
        {
            warningMessage = new ArrayList<>();
            warningMessage.add(PatientController.WARNING_DUPLICATE_PHONE_NUMBERS);
            adviceMessage = new ArrayList<>();
            adviceMessage.add(PatientController.WARNING_DUPLICATE_PHONE_NUMBERS_SUGGESTION);
        }
        uiModel.addAttribute("patientsWithSameMobileNumber", patientsWithSameMobileNumber);
        uiModel.addAttribute("warningMessage", warningMessage);
        uiModel.addAttribute("adviceMessage", adviceMessage);

        PatientSummary patientSummary = new PatientSummary(new PatientViewModel(patient), earliestTreatmentAdvice, currentTreatmentAdvice, currentRegimen,
                clinicVisits, patientStatusChangeHistory, runningAdherencePercentage, warning);
        //Do not change name of form bean - currently used by graphs to get data.
        //TODO : Change <graph>.jspx partials to accept patient form bean name as parameter/variable.
        return new ModelAndView(SUMMARY_VIEW, "patient", patientSummary);
    }

    private Double getRunningAdherencePercentage(Patient patient) {
        PatientReport patientReport = patientService.getPatientReport(patient.getId());
        if (null != patientReport && null != patientReport.getCurrentRegimenStartDate()) {
            return adherenceService.getRunningAdherencePercentage(patient);
        } else {
            return 0d;
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(Model uiModel, HttpServletRequest request) {
        String clinicId = loggedInClinic(request);

        String applicationVersion = getApplicationVersion(request);
        String contextPath = request.getSession().getServletContext().getContextPath();
        String incompleteImageUrl = String.format("%s/resources-%s/images/warning.png", contextPath, applicationVersion);
        String duplicateImageUrl = String.format("%s/resources-%s/images/duplicate_phone_number_warning.png", contextPath, applicationVersion);
        List<PatientViewModel> listPatientViewModels = new ArrayList<>();
        for (Patient patient : allPatients.findByClinic(clinicId)) {
            PatientViewModel listPatientViewModel = new PatientViewModel(patient);
            listPatientViewModel.setIncompleteImageUrl(incompleteImageUrl);
            boolean checkIfGivenMobileNumberIsUnique = new UniquePatientMobileNumberWarning(allPatients).checkIfGivenMobileNumberIsUnique(patient.getMobilePhoneNumber(),patient.getPatientId(),clinicId);
            listPatientViewModel.setDuplicateImageUrl(duplicateImageUrl);
            listPatientViewModel.setHasUniqueMobileNumber(checkIfGivenMobileNumberIsUnique);
            listPatientViewModels.add(listPatientViewModel);
        }
        uiModel.addAttribute(PATIENTS, listPatientViewModels);
        uiModel.addAttribute("selectedMenuItem", "ALL_PATIENTS");
        addDateTimeFormat(uiModel);
        return LIST_VIEW;
    }

    private String getApplicationVersion(HttpServletRequest request) {
        ApplicationContext appCtx = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
        Properties tamaProperties = appCtx.getBean("tamaProperties", Properties.class);
        return tamaProperties.getProperty("application.version");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/saveAndActivate")
    public String saveAndActivate(@Valid Patient patient, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {
        return create(patient, bindingResult, uiModel, request, true);
    }


    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Patient patient, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {
        return create(patient, bindingResult, uiModel, request, false);
    }

    private String create(Patient patient, BindingResult bindingResult, Model uiModel, HttpServletRequest request, Boolean shouldActivate) {
        uiModel.addAttribute(EXPRESS_REGISTRATION, "true");

        if (bindingResult.hasErrors()) {
            initUIModel(uiModel, patient);
            return CREATE_VIEW;
        }
        List<String> patientsWithSameMobileNumber = new UniquePatientMobileNumberWarning(allPatients).findAllMobileNumbersWhichMatchTheGivenNumber(patient.getMobilePhoneNumber()
                ,patient.getPatientId(),loggedInClinic(request));
        uiModel.addAttribute("patientsWithSameMobileNumber", patientsWithSameMobileNumber);
        try {
            patientService.create(patient, loggedInClinic(request), loggedInUserId(request));
            String redirectUrl = REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(patient.getId(), request);
            if (shouldActivate) {
                redirectUrl = activatePatient(patient.getId(), REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(patient.getId(), request), request);
            }
            Patient savedPatient = allPatients.findByPatientIdAndClinicId(patient.getPatientId(), loggedInClinic(request));
            List<String> warning = new IncompletePatientDataWarning(savedPatient, null, null, null, null).value();
            if (!CollectionUtils.isEmpty(warning)) {
                if (!warning.get(0).equals(PATIENT_HAS_NOT_BEEN_ACTIVATED))
                {
                    warning.add(PATIENT_WARNING_WARNING_RESOLVE_HELP);
                }
            }

            uiModel.addAttribute("warning", warning);

            initUIModel(uiModel, savedPatient);
            return redirectUrl;
        } catch (RuntimeException e) {
            decorateViewWithUniqueConstraintError(patient, bindingResult, uiModel, e);
            return CREATE_VIEW;
        }
    }


    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
        Patient patient = allPatients.findByIdAndClinicId(id, loggedInClinic(request));
        if (patient == null) return "authorizationFailure";

        List<SystemCategory> patientSystemCategories = patient.getMedicalHistory().getNonHivMedicalHistory().getSystemCategories();

        initUIModel(uiModel, patient);

        uiModel.addAttribute("systemCategories", patientSystemCategories);
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
            List<String> warningMessage = null;
            List<String> adviceMessage = null;
            List<String> warning = new IncompletePatientDataWarning(patient, allVitalStatistics, allTreatmentAdvices, allLabResults, allClinicVisits).value();
            if (!CollectionUtils.isEmpty(warning)) {
                warning.add(PATIENT_WARNING_WARNING_RESOLVE_HELP);
            }
            patient.setComplete(CollectionUtils.isEmpty(warning));
            List<String> patientsWithSameMobileNumber = new UniquePatientMobileNumberWarning(allPatients).findAllMobileNumbersWhichMatchTheGivenNumberOnUpdate(patient.getMobilePhoneNumber(), patient.getPatientId(),
                    loggedInClinic(request).toString());
            if(!CollectionUtils.isEmpty(patientsWithSameMobileNumber))
            {
                warningMessage = new ArrayList<>();
                warningMessage.add(PatientController.WARNING_DUPLICATE_PHONE_NUMBERS);
                adviceMessage = new ArrayList<>();
                adviceMessage.add(PatientController.WARNING_DUPLICATE_PHONE_NUMBERS_SUGGESTION);
            }
            uiModel.addAttribute("patientsWithSameMobileNumber", patientsWithSameMobileNumber);
            uiModel.addAttribute("warningMessage", warningMessage);
            uiModel.addAttribute("adviceMessage", adviceMessage);
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
        if (message != null && message.contains(Patient.CLINIC_AND_PATIENT_ID_UNIQUE_CONSTRAINT)) {
            bindingResult.addError(new FieldError("Patient", "patientId", patient.getPatientId(), false,
                    new String[]{"clinic_and_patient_id_not_unique"}, new Object[]{}, CLINIC_AND_PATIENT_ID_ALREADY_IN_USE));
        } else if (message != null && message.contains(Patient.PHONE_NUMBER_AND_PASSCODE_UNIQUE_CONSTRAINT)) {
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
        uiModel.addAttribute("options", AilmentState.values());
        uiModel.addAttribute("questions", MedicalHistoryQuestions.all());
        uiModel.addAttribute("daysOfWeek", Arrays.asList(DayOfWeek.values()));
        uiModel.addAttribute("timeMeridiems", Arrays.asList(TimeMeridiem.values()));

        if (uiModel.asMap().containsKey(EXPRESS_REGISTRATION))
            uiModel.addAttribute("systemCategories", SystemCategoryDefinition.allExpressRegistration());
        else
            uiModel.addAttribute("systemCategories", SystemCategoryDefinition.all());
    }

    private void addDateTimeFormat(Model uiModel) {
        uiModel.addAttribute(DATE_OF_BIRTH_FORMAT, DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
    }


    @RequestMapping(value = "/validateMobileNumberUniqueness.json", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse validateMobileNumberUniqueness(@RequestParam(value = "mobileNumber", required = true) String mobileNumber,HttpServletRequest request) {
        JsonResponse res = new JsonResponse();

        boolean isMobileNumberUnique = new UniquePatientMobileNumberWarning(allPatients).checkIfMobileNumberIsDuplicateOrNot(mobileNumber);
        if(!isMobileNumberUnique)
        {
            res.setStatus("FAIL");
        }
        else
        {
            res.setStatus("SUCCESS");
        }
        return res;
    }
    @RequestMapping(value = "/validateMobileNumberUniquenessOnUpdate.json", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse validateMobileNumberUniquenessOnUpdate(@RequestParam(value = "mobileNumber", required = true) String mobileNumber,HttpServletRequest request) {
        JsonResponse res = new JsonResponse();

        boolean isMobileNumberUnique = new UniquePatientMobileNumberWarning(allPatients).checkIfMobileNumberIsDuplicateOrNotOnUpdate(mobileNumber);
        if(!isMobileNumberUnique)
        {
            res.setStatus("FAIL");
        }
        else
        {
            res.setStatus("SUCCESS");
        }
        return res;
    }
}