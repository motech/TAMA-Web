package org.motechproject.tama.web;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.*;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.web.model.*;
import org.motechproject.tama.web.reportbuilder.AppointmentCalendarBuilder;
import org.motechproject.tama.web.reportbuilder.abstractbuilder.InMemoryReportBuilder;
import org.motechproject.tama.web.service.PatientDetailsService;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@RequestMapping("/clinicvisits")
@Controller
public class ClinicVisitsController extends BaseController {

    private TreatmentAdviceController treatmentAdviceController;
    private LabResultsController labResultsController;
    private VitalStatisticsController vitalStatisticsController;
    private OpportunisticInfectionsController opportunisticInfectionsController;
    private AllClinicVisits allClinicVisits;
    private AllVitalStatistics allVitalStatistics;
    private AllLabResults allLabResults;
    private AllTreatmentAdvices allTreatmentAdvices;
    private PatientService patientService;
    private PatientDetailsService patientDetailsService;
    private AllPatients allPatients;
    private AllRegimens allRegimens;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public ClinicVisitsController(TreatmentAdviceController treatmentAdviceController,
                                  AllTreatmentAdvices allTreatmentAdvices,
                                  AllVitalStatistics allVitalStatistics,
                                  AllLabResults allLabResults,
                                  LabResultsController labResultsController,
                                  VitalStatisticsController vitalStatisticsController,
                                  OpportunisticInfectionsController opportunisticInfectionsController,
                                  AllClinicVisits allClinicVisits,
                                  PatientService patientService,
                                  PatientDetailsService patientDetailsService,
                                  AllPatients allPatients, AllRegimens allRegimens) {

        this.treatmentAdviceController = treatmentAdviceController;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.labResultsController = labResultsController;
        this.allVitalStatistics = allVitalStatistics;
        this.allLabResults = allLabResults;
        this.vitalStatisticsController = vitalStatisticsController;
        this.opportunisticInfectionsController = opportunisticInfectionsController;
        this.allClinicVisits = allClinicVisits;
        this.patientService = patientService;
        this.patientDetailsService = patientDetailsService;
        this.allPatients = allPatients;
        this.allRegimens = allRegimens;
    }

    @RequestMapping(value = "/newVisit")
    public String newVisit(@RequestParam(value = "patientDocId") String patientDocId, Model uiModel, HttpServletRequest httpServletRequest) {
        List<ClinicVisit> clinicVisits = allClinicVisits.clinicVisits(patientDocId);
        for (ClinicVisit clinicVisit : clinicVisits) {
            if (clinicVisit.isBaseline() && clinicVisit.getTreatmentAdviceId() == null) {
                return list(patientDocId, uiModel);
            }
        }
        String clinicVisitId = allClinicVisits.createUnscheduledVisit(patientDocId, DateUtil.now(), TypeOfVisit.Unscheduled, loggedInUserId(httpServletRequest));
        return createForm(patientDocId, clinicVisitId, uiModel, httpServletRequest);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@RequestParam(value = "patientId", required = true) String patientDocId, @RequestParam(value = "clinicVisitId", required = true) String clinicVisitId, Model uiModel, HttpServletRequest httpServletRequest) {
        ClinicVisit clinicVisit = allClinicVisits.get(patientDocId, clinicVisitId);
        final String treatmentAdviceId = clinicVisit.getTreatmentAdviceId();
        List<String> warningMessage = null;
        List<String> adviceMessage = null;

        if (canAllowUpdateOfClinicVisits(clinicVisit, patientDocId)) {

            TreatmentAdvice adviceForPatient = null;
            if (treatmentAdviceId != null)
                adviceForPatient = allTreatmentAdvices.get(treatmentAdviceId);
            if (adviceForPatient == null)
                adviceForPatient = allTreatmentAdvices.currentTreatmentAdvice(patientDocId);
            if (adviceForPatient != null) {
                treatmentAdviceController.show(adviceForPatient.getId(), uiModel);
                final boolean wasVisitDetailsEdited = (clinicVisit.getVisitDate() != null);
                if (wasVisitDetailsEdited)
                    return redirectToShowClinicVisitUrl(clinicVisitId, patientDocId, httpServletRequest);
            } else {
                treatmentAdviceController.createForm(patientDocId, uiModel);
            }
            List<String> patientsWithSameMobileNumber = new UniquePatientMobileNumberWarning(allPatients).findAllMobileNumbersWhichMatchTheGivenNumber(clinicVisit.getPatient().getMobilePhoneNumber(), clinicVisit.getPatient().getPatientId(), clinicVisit.getPatient().getClinic().getName(), PatientController.PATIENT);
            List<String> patientsClinicWithSameMobileNumber = new UniquePatientMobileNumberWarning(allPatients).findAllMobileNumbersWhichMatchTheGivenNumber(clinicVisit.getPatient().getMobilePhoneNumber(), clinicVisit.getPatient().getPatientId(), clinicVisit.getPatient().getClinic().getName(), PatientController.CLINIC);
            if (CollectionUtils.isNotEmpty(patientsWithSameMobileNumber)) {
                warningMessage = new ArrayList<>();
                warningMessage.add(PatientController.WARNING_DUPLICATE_PHONE_NUMBERS);
                adviceMessage = new ArrayList<>();
                adviceMessage.add(PatientController.WARNING_DUPLICATE_PHONE_NUMBERS_SUGGESTION);
            }
            uiModel.addAttribute("patientsClinicWithSameMobileNumber", patientsClinicWithSameMobileNumber);
            uiModel.addAttribute("patientsWithSameMobileNumber", patientsWithSameMobileNumber);
            uiModel.addAttribute("warningMessage", warningMessage);
            uiModel.addAttribute("adviceMessage", adviceMessage);

            List<String> warning = new IncompletePatientDataWarning(clinicVisit.getPatient(), allVitalStatistics, allTreatmentAdvices, allLabResults, allClinicVisits).value();
            uiModel.addAttribute("patientId", patientDocId);
            uiModel.addAttribute("clinicVisit", new ClinicVisitUIModel(clinicVisit));
            uiModel.addAttribute("patient", clinicVisit.getPatient());
            uiModel.addAttribute(PatientController.WARNING, warning);
            labResultsController.createForm(patientDocId, uiModel);
            vitalStatisticsController.createForm(patientDocId, uiModel);
            opportunisticInfectionsController.createForm(clinicVisit, uiModel);
            return "clinicvisits/create";
        } else {
            return list(patientDocId, uiModel);
        }
    }

    @RequestMapping(value = "/create/{clinicVisitId}", method = RequestMethod.POST)
    public String create(@PathVariable("clinicVisitId") String clinicVisitId,
                         ClinicVisitUIModel clinicVisitUIModel,
                         TreatmentAdvice treatmentAdvice,
                         LabResultsUIModel labResultsUiModel,
                         @Valid VitalStatistics vitalStatistics,
                         @Valid OpportunisticInfectionsUIModel opportunisticInfections,
                         BindingResult bindingResult,
                         Model uiModel,
                         HttpServletRequest httpServletRequest) {
        String patientId = treatmentAdvice.getPatientId();
        if (bindingResult.hasErrors()) {
            return "clinicvisits/create";
        }
        String treatmentAdviceId = null;
        if (isNotBlank(treatmentAdvice.getRegimenId())) {
            try {
                treatmentAdviceId = treatmentAdviceController.create(bindingResult, uiModel, treatmentAdvice, loggedInUserId(httpServletRequest));
            } catch (RuntimeException e) {
                httpServletRequest.setAttribute("flash.flashError", "Error occurred while creating treatment advice: " + e.getMessage());
                return redirectToCreateFormUrl(clinicVisitId, treatmentAdvice.getPatientId());
            }
        } else {
            ClinicVisit clinicVisit = allClinicVisits.get(patientId, clinicVisitId);
            if (clinicVisit != null) {
                treatmentAdviceId = clinicVisit.getTreatmentAdviceId();
            }
        }
        List<String> labResultIds = labResultsController.create(labResultsUiModel, bindingResult, uiModel, httpServletRequest);
        String vitalStatisticsId = vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel, httpServletRequest);
        String reportedOpportunisticInfectionsId = opportunisticInfectionsController.create(opportunisticInfections, bindingResult, uiModel, httpServletRequest);
        try {
            allClinicVisits.updateVisitDetails(clinicVisitId, clinicVisitUIModel.getDefaultVisitDate(), patientId, treatmentAdviceId, labResultIds, vitalStatisticsId, reportedOpportunisticInfectionsId, loggedInUserId(httpServletRequest));
            patientDetailsService.update(patientId);
        } catch (RuntimeException e) {
            httpServletRequest.setAttribute("flash.flashError", "Error occurred while creating clinic visit. Please try again: " + e.getMessage());
            return redirectToCreateFormUrl(clinicVisitId, treatmentAdvice.getPatientId());
        }
        return redirectToShowClinicVisitUrl(clinicVisitId, patientId, httpServletRequest);
    }

    @RequestMapping(value = "/{clinicVisitId}", method = RequestMethod.GET)
    public String show(@PathVariable("clinicVisitId") String clinicVisitId, @RequestParam(value = "patientId", required = true) String patientDocId, Model uiModel) {
        List<String> warningMessage = null;
        List<String> adviceMessage = null;
        ClinicVisit clinicVisit = allClinicVisits.get(patientDocId, clinicVisitId);
        String treatmentAdviceId = clinicVisit.getTreatmentAdviceId();
        if (treatmentAdviceId == null)
            treatmentAdviceId = allTreatmentAdvices.currentTreatmentAdvice(patientDocId).getId();
        treatmentAdviceController.show(treatmentAdviceId, uiModel);
        labResultsController.show(patientDocId, clinicVisit.getId(), clinicVisit.getLabResultIds(), uiModel);
        vitalStatisticsController.show(clinicVisit.getVitalStatisticsId(), uiModel);
        opportunisticInfectionsController.show(clinicVisit, uiModel);
        List<String> warning = new IncompletePatientDataWarning(clinicVisit.getPatient(), allVitalStatistics, allTreatmentAdvices, allLabResults, allClinicVisits).value();
        List<String> patientsWithSameMobileNumber = new UniquePatientMobileNumberWarning(allPatients).findAllMobileNumbersWhichMatchTheGivenNumber(clinicVisit.getPatient().getMobilePhoneNumber(), clinicVisit.getPatient().getPatientId(), clinicVisit.getPatient().getClinic().getName(), PatientController.PATIENT);
        List<String> patientsClinicWithSameMobileNumber = new UniquePatientMobileNumberWarning(allPatients).findAllMobileNumbersWhichMatchTheGivenNumber(clinicVisit.getPatient().getMobilePhoneNumber(), clinicVisit.getPatient().getPatientId(), clinicVisit.getPatient().getClinic().getName(), PatientController.CLINIC);
        if (CollectionUtils.isNotEmpty(patientsWithSameMobileNumber)) {
            warningMessage = new ArrayList<>();
            warningMessage.add(PatientController.WARNING_DUPLICATE_PHONE_NUMBERS);
            adviceMessage = new ArrayList<>();
            adviceMessage.add(PatientController.WARNING_DUPLICATE_PHONE_NUMBERS_SUGGESTION);
        }
        uiModel.addAttribute("patientsWithSameMobileNumber", patientsWithSameMobileNumber);
        uiModel.addAttribute("patientsClinicWithSameMobileNumber", patientsClinicWithSameMobileNumber);
        uiModel.addAttribute("warningMessage", warningMessage);
        uiModel.addAttribute("adviceMessage", adviceMessage);
        uiModel.addAttribute("clinicVisit", new ClinicVisitUIModel(clinicVisit));
        uiModel.addAttribute("patient", clinicVisit.getPatient());
        uiModel.addAttribute(PatientController.WARNING, warning);
        return "clinicvisits/show";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(@RequestParam(value = "patientId", required = true) String patientDocId, Model uiModel) {
        List<String> warningMessage = null;
        List<String> adviceMessage = null;
        List<ClinicVisitUIModel> clinicVisitUIModels = allClinicVisits(patientDocId);
        Patient patient = clinicVisitUIModels.get(0).getPatient();
        List<String> warning = new IncompletePatientDataWarning(patient, allVitalStatistics, allTreatmentAdvices, allLabResults, allClinicVisits).value();
        List<String> patientsWithSameMobileNumber = new UniquePatientMobileNumberWarning(allPatients).
                findAllMobileNumbersWhichMatchTheGivenNumberCreateClinicVisit(patient.getMobilePhoneNumber(), patientDocId, patient.getClinic().getName(), PatientController.PATIENT);
        List<String> patientsClinicWithSameMobileNumber = new UniquePatientMobileNumberWarning(allPatients).
                findAllMobileNumbersWhichMatchTheGivenNumberCreateClinicVisit(patient.getMobilePhoneNumber(), patientDocId, patient.getClinic().getName(), PatientController.CLINIC);
        if (CollectionUtils.isNotEmpty(patientsWithSameMobileNumber)) {
            warningMessage = new ArrayList<>();
            warningMessage.add(PatientController.WARNING_DUPLICATE_PHONE_NUMBERS);
            adviceMessage = new ArrayList<>();
            adviceMessage.add(PatientController.WARNING_DUPLICATE_PHONE_NUMBERS_SUGGESTION);
        }
        boolean checkIfBaseLineVisitHasTreatmentAdviceId = checkIfBaseLineVisitHasTreatmentAdviceId(allClinicVisits.clinicVisits(patientDocId));
        uiModel.addAttribute("patientsWithSameMobileNumber", patientsWithSameMobileNumber);
        uiModel.addAttribute("patientsClinicWithSameMobileNumber", patientsClinicWithSameMobileNumber);
        uiModel.addAttribute("warningMessage", warningMessage);
        uiModel.addAttribute("adviceMessage", adviceMessage);
        uiModel.addAttribute("clinicVisits", clinicVisitUIModels);
        uiModel.addAttribute("patient", new PatientViewModel(patient));
        uiModel.addAttribute(PatientController.WARNING, warning);
        uiModel.addAttribute("baseLineVisitTreatmentAdviceExists", checkIfBaseLineVisitHasTreatmentAdviceId);
        if (!patient.getStatus().isActive())
            return "clinicvisits/view_list";
        return "clinicvisits/manage_list";
    }

    @RequestMapping(value = "/list.xls", method = RequestMethod.GET)
    public void downloadList(@RequestParam(value = "patientId", required = true) String patientDocId, HttpServletResponse response) {
        response.setHeader("Content-Disposition", "inline; filename=AppointmentCalendar.xls");
        response.setContentType("application/vnd.ms-excel");
        try {
            ClinicVisits clinicVisits = allClinicVisits.clinicVisits(patientDocId);
            PatientReport patientReport = patientService.getPatientReport(patientDocId);
            AppointmentCalendarBuilder appointmentCalendarBuilder = new AppointmentCalendarBuilder(clinicVisits, patientReport, allTreatmentAdvices, allRegimens);
            writeExcelToResponse(response, appointmentCalendarBuilder);
        } catch (Exception e) {
            logger.error("Error while generating excel report: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/adjustDueDate.json/{clinicVisitId}", method = RequestMethod.POST)
    @ResponseBody
    public String adjustDueDate(@RequestParam(value = "patientId", required = true) String patientDocId, @PathVariable("clinicVisitId") String clinicVisitId, @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    @RequestParam(value = "adjustedDueDate") LocalDate adjustedDueDate, HttpServletRequest request) {
        allClinicVisits.adjustDueDate(patientDocId, clinicVisitId, adjustedDueDate, loggedInUserId(request));
        return "{'adjustedDueDate':'" + adjustedDueDate.toString(TAMAConstants.DATE_FORMAT) + "'}";
    }

    @RequestMapping(value = "/confirmVisitDate.json/{clinicVisitId}", method = RequestMethod.POST)
    @ResponseBody
    public String confirmVisitDate(@RequestParam(value = "patientId", required = true) String patientDocId, @PathVariable("clinicVisitId") String clinicVisitId, @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATETIME_FORMAT)
    @RequestParam(value = "confirmedAppointmentDate") DateTime confirmedAppointmentDate, HttpServletRequest request) {
        allClinicVisits.confirmAppointmentDate(patientDocId, clinicVisitId, confirmedAppointmentDate, loggedInUserId(request));
        return "{'confirmedAppointmentDate':'" + confirmedAppointmentDate.toString(TAMAConstants.DATETIME_FORMAT) + "'}";
    }

    @RequestMapping(value = "/markAsMissed.json/{clinicVisitId}", method = RequestMethod.POST)
    @ResponseBody
    public String markAsMissed(@RequestParam(value = "patientId", required = true) String patientDocId, @PathVariable(value = "clinicVisitId") String clinicVisitId, HttpServletRequest request) {
        allClinicVisits.markAsMissed(patientDocId, clinicVisitId, loggedInUserId(request));
        return "{'missed':true}";
    }

    @RequestMapping(value = "/setVisitDate.json/{clinicVisitId}", method = RequestMethod.POST)
    @ResponseBody
    public String setVisitDate(@RequestParam(value = "patientId", required = true) String patientDocId, @PathVariable(value = "clinicVisitId") String clinicVisitId, @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    @RequestParam(value = "visitDate") DateTime visitDate, HttpServletRequest httpServletRequest) {
        allClinicVisits.closeVisit(patientDocId, clinicVisitId, visitDate, loggedInUserId(httpServletRequest));
        return "{'visitDate':'" + visitDate.toString(TAMAConstants.DATE_FORMAT) + "'}";
    }

    @RequestMapping(value = "/createAppointment.json", method = RequestMethod.POST)
    @ResponseBody
    public String createAppointment(@RequestParam(value = "patientId", required = true) String patientDocId, @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    @RequestParam(value = "appointmentDueDate") DateTime appointmentDueDate, @RequestParam(value = "typeOfVisit") String typeOfVisit, HttpServletRequest request) {
        allClinicVisits.createUnScheduledAppointment(patientDocId, appointmentDueDate, TypeOfVisit.valueOf(typeOfVisit), loggedInUserId(request));
        return "{'result':'success'}";
    }

    public static String redirectToCreateFormUrl(String clinicVisitId, String patientId) {
        String queryParameters = "form&patientId=" + patientId + "&clinicVisitId=" + clinicVisitId;
        return "redirect:/clinicvisits?" + queryParameters;
    }

    private List<ClinicVisitUIModel> allClinicVisits(String patientDocId) {
        ClinicVisits clinicVisits = allClinicVisits.clinicVisits(patientDocId);
        Collections.sort(clinicVisits);
        List<ClinicVisitUIModel> clinicVisitUIModels = new ArrayList<ClinicVisitUIModel>();
        for (ClinicVisit clinicVisit : clinicVisits) {
            clinicVisitUIModels.add(new ClinicVisitUIModel(clinicVisit));
        }
        return clinicVisitUIModels;
    }

    private String redirectToShowClinicVisitUrl(String clinicVisitId, String patientId, HttpServletRequest httpServletRequest) {
        return "redirect:/clinicvisits/" + encodeUrlPathSegment(clinicVisitId, httpServletRequest) + "?patientId=" + patientId;
    }

    private void writeExcelToResponse(HttpServletResponse response, InMemoryReportBuilder appointmentCalendarBuilder) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        HSSFWorkbook excelWorkbook = appointmentCalendarBuilder.getExcelWorkbook();
        excelWorkbook.write(outputStream);
        outputStream.flush();
    }

    private boolean checkIfBaseLineVisitHasTreatmentAdviceId(List<ClinicVisit> clinicVisits) {
        boolean checkIfBaseLineVisitHasTreatmentAdviceId = true;
        for (ClinicVisit clinicVisit : clinicVisits) {
            if (clinicVisit.isBaseline() && clinicVisit.getTreatmentAdviceId() == null) {
                checkIfBaseLineVisitHasTreatmentAdviceId = false;
            }
        }
        return checkIfBaseLineVisitHasTreatmentAdviceId;

    }

    private boolean canAllowUpdateOfClinicVisits(ClinicVisit clinicVisit, String patientDocId) {
        boolean allowUpdate = false;
        if (clinicVisit.isBaseline() && clinicVisit.getTreatmentAdviceId() == null) {
            allowUpdate = true;
        } else {
            List<ClinicVisit> clinicVisits = allClinicVisits.clinicVisits(patientDocId);
            allowUpdate = checkIfBaseLineVisitHasTreatmentAdviceId(clinicVisits);
        }
        return allowUpdate;
    }
}
