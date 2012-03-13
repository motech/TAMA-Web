package org.motechproject.tama.web;

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
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.web.model.ClinicVisitUIModel;
import org.motechproject.tama.web.model.IncompletePatientDataWarning;
import org.motechproject.tama.web.model.LabResultsUIModel;
import org.motechproject.tama.web.model.OpportunisticInfectionsUIModel;
import org.motechproject.tama.web.viewbuilder.AppointmentCalendarBuilder;
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
                                  PatientService patientService) {

        this.treatmentAdviceController = treatmentAdviceController;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.labResultsController = labResultsController;
        this.allVitalStatistics = allVitalStatistics;
        this.allLabResults = allLabResults;
        this.vitalStatisticsController = vitalStatisticsController;
        this.opportunisticInfectionsController = opportunisticInfectionsController;
        this.allClinicVisits = allClinicVisits;
        this.patientService = patientService;
    }

    @RequestMapping(value = "/newVisit")
    public String newVisit(@RequestParam(value = "patientDocId") String patientDocId, Model uiModel, HttpServletRequest httpServletRequest) {
        String clinicVisitId = allClinicVisits.createUnscheduledVisit(patientDocId, DateUtil.now(), TypeOfVisit.Unscheduled);
        return createForm(patientDocId, clinicVisitId, uiModel, httpServletRequest);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@RequestParam(value = "patientId", required = true) String patientDocId, @RequestParam(value = "clinicVisitId", required = true) String clinicVisitId, Model uiModel, HttpServletRequest httpServletRequest) {
        ClinicVisit clinicVisit = allClinicVisits.get(patientDocId, clinicVisitId);
        final String treatmentAdviceId = clinicVisit.getTreatmentAdviceId();

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
        String warning = new IncompletePatientDataWarning(clinicVisit.getPatient(), allVitalStatistics, allTreatmentAdvices, allLabResults).toString();
        uiModel.addAttribute("patientId", patientDocId);
        uiModel.addAttribute("clinicVisit", new ClinicVisitUIModel(clinicVisit));
        uiModel.addAttribute(PatientController.WARNING, warning);
        labResultsController.createForm(patientDocId, uiModel);
        vitalStatisticsController.createForm(patientDocId, uiModel);
        opportunisticInfectionsController.createForm(clinicVisit, uiModel);
        return "clinicvisits/create";
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
                treatmentAdviceId = treatmentAdviceController.create(bindingResult, uiModel, treatmentAdvice);
            } catch (RuntimeException e) {
                httpServletRequest.setAttribute("flash.flashError", "Error occurred while creating treatment advice: " + e.getMessage());
                return redirectToCreateFormUrl(clinicVisitId, treatmentAdvice.getPatientId());
            }
        }
        List<String> labResultIds = labResultsController.create(labResultsUiModel, bindingResult, uiModel, httpServletRequest);
        String vitalStatisticsId = vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel, httpServletRequest);
        String reportedOpportunisticInfectionsId = opportunisticInfectionsController.create(opportunisticInfections, bindingResult, uiModel, httpServletRequest);

        try {
            allClinicVisits.updateVisitDetails(clinicVisitId, clinicVisitUIModel.getDefaultVisitDate(), patientId, treatmentAdviceId, labResultIds, vitalStatisticsId, reportedOpportunisticInfectionsId);
        } catch (RuntimeException e) {
            httpServletRequest.setAttribute("flash.flashError", "Error occurred while creating clinic visit. Please try again: " + e.getMessage());
            return redirectToCreateFormUrl(clinicVisitId, treatmentAdvice.getPatientId());
        }
        return redirectToShowClinicVisitUrl(clinicVisitId, patientId, httpServletRequest);
    }

    @RequestMapping(value = "/{clinicVisitId}", method = RequestMethod.GET)
    public String show(@PathVariable("clinicVisitId") String clinicVisitId, @RequestParam(value = "patientId", required = true) String patientDocId, Model uiModel) {
        ClinicVisit clinicVisit = allClinicVisits.get(patientDocId, clinicVisitId);
        String treatmentAdviceId = clinicVisit.getTreatmentAdviceId();
        if (treatmentAdviceId == null)
            treatmentAdviceId = allTreatmentAdvices.currentTreatmentAdvice(patientDocId).getId();
        treatmentAdviceController.show(treatmentAdviceId, uiModel);
        labResultsController.show(patientDocId, clinicVisit.getId(), clinicVisit.getLabResultIds(), uiModel);
        vitalStatisticsController.show(clinicVisit.getVitalStatisticsId(), uiModel);
        opportunisticInfectionsController.show(clinicVisit, uiModel);
        String warning = new IncompletePatientDataWarning(clinicVisit.getPatient(), allVitalStatistics, allTreatmentAdvices, allLabResults).toString();
        uiModel.addAttribute("clinicVisit", new ClinicVisitUIModel(clinicVisit));
        uiModel.addAttribute(PatientController.WARNING, warning);
        return "clinicvisits/show";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(@RequestParam(value = "patientId", required = true) String patientDocId, Model uiModel) {
        List<ClinicVisitUIModel> clinicVisitUIModels = allClinicVisits(patientDocId);
        Patient patient = clinicVisitUIModels.get(0).getPatient();
        String warning = new IncompletePatientDataWarning(patient, allVitalStatistics, allTreatmentAdvices, allLabResults).toString();
        uiModel.addAttribute("clinicVisits", clinicVisitUIModels);
        uiModel.addAttribute("patient", patient);
        uiModel.addAttribute(PatientController.WARNING, warning);
        if (!patient.getStatus().isActive())
            return "clinicvisits/view_list";
        return "clinicvisits/manage_list";
    }

    @RequestMapping(value = "/list.xls", method = RequestMethod.GET)
    public void downloadList(@RequestParam(value = "patientId", required = true) String patientDocId, HttpServletResponse response) {
        response.setHeader("Content-Disposition", "inline; filename=AppointmentCalendar.xls");
        response.setContentType("application/vnd.ms-excel");
        try {
            ServletOutputStream outputStream = response.getOutputStream();

            List<ClinicVisitUIModel> clinicVisitUIModels = allClinicVisits(patientDocId);
            PatientReport patientReport = patientService.getPatientReport(patientDocId);

            AppointmentCalendarBuilder appointmentCalendarBuilder = new AppointmentCalendarBuilder(clinicVisitUIModels, patientReport);
            HSSFWorkbook excelWorkbook = appointmentCalendarBuilder.getExcelWorkbook();
            excelWorkbook.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            logger.error("Error while generating excel report: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/adjustDueDate.json/{clinicVisitId}", method = RequestMethod.POST)
    @ResponseBody
    public String adjustDueDate(@RequestParam(value = "patientId", required = true) String patientDocId, @PathVariable("clinicVisitId") String clinicVisitId, @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    @RequestParam(value = "adjustedDueDate") LocalDate adjustedDueDate) {
        allClinicVisits.adjustDueDate(patientDocId, clinicVisitId, adjustedDueDate);
        return "{'adjustedDueDate':'" + adjustedDueDate.toString(TAMAConstants.DATE_FORMAT) + "'}";
    }

    @RequestMapping(value = "/confirmVisitDate.json/{clinicVisitId}", method = RequestMethod.POST)
    @ResponseBody
    public String confirmVisitDate(@RequestParam(value = "patientId", required = true) String patientDocId, @PathVariable("clinicVisitId") String clinicVisitId, @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATETIME_FORMAT)
    @RequestParam(value = "confirmedAppointmentDate") DateTime confirmedAppointmentDate) {
        allClinicVisits.confirmAppointmentDate(patientDocId, clinicVisitId, confirmedAppointmentDate);
        return "{'confirmedAppointmentDate':'" + confirmedAppointmentDate.toString(TAMAConstants.DATETIME_FORMAT) + "'}";
    }

    @RequestMapping(value = "/markAsMissed.json/{clinicVisitId}", method = RequestMethod.POST)
    @ResponseBody
    public String markAsMissed(@RequestParam(value = "patientId", required = true) String patientDocId, @PathVariable(value = "clinicVisitId") String clinicVisitId) {
        allClinicVisits.markAsMissed(patientDocId, clinicVisitId);
        return "{'missed':true}";
    }

    @RequestMapping(value = "/setVisitDate.json/{clinicVisitId}", method = RequestMethod.POST)
    @ResponseBody
    public String setVisitDate(@RequestParam(value = "patientId", required = true) String patientDocId, @PathVariable(value = "clinicVisitId") String clinicVisitId, @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    @RequestParam(value = "visitDate") DateTime visitDate) {
        allClinicVisits.closeVisit(patientDocId, clinicVisitId, visitDate);
        return "{'visitDate':'" + visitDate.toString(TAMAConstants.DATE_FORMAT) + "'}";
    }

    @RequestMapping(value = "/createAppointment.json", method = RequestMethod.POST)
    @ResponseBody
    public String createAppointment(@RequestParam(value = "patientId", required = true) String patientDocId, @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    @RequestParam(value = "appointmentDueDate") DateTime appointmentDueDate, @RequestParam(value = "typeOfVisit") String typeOfVisit) {
        allClinicVisits.createUnScheduledAppointment(patientDocId, appointmentDueDate, TypeOfVisit.valueOf(typeOfVisit));
        return "{'result':'success'}";
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

    public static String redirectToCreateFormUrl(String clinicVisitId, String patientId) {
        String queryParameters = "form&patientId=" + patientId + "&clinicVisitId=" + clinicVisitId;
        return "redirect:/clinicvisits?" + queryParameters;
    }
}
