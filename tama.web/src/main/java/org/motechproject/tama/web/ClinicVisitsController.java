package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.web.model.LabResultsUIModel;
import org.motechproject.tama.web.model.OpportunisticInfectionsUIModel;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    private AllTreatmentAdvices allTreatmentAdvices;

    @Autowired
    public ClinicVisitsController(TreatmentAdviceController treatmentAdviceController, AllTreatmentAdvices allTreatmentAdvices, LabResultsController labResultsController, VitalStatisticsController vitalStatisticsController, OpportunisticInfectionsController opportunisticInfectionsController, AllClinicVisits allClinicVisits) {
        this.treatmentAdviceController = treatmentAdviceController;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.labResultsController = labResultsController;
        this.vitalStatisticsController = vitalStatisticsController;
        this.opportunisticInfectionsController = opportunisticInfectionsController;
        this.allClinicVisits = allClinicVisits;
    }

    @RequestMapping(value = "/newVisit")
    public String newVisit(@RequestParam(value = "patientDocId") String patientDocId, Model uiModel, HttpServletRequest httpServletRequest) {
        String clinicVisitId = allClinicVisits.createUnscheduledVisit(patientDocId, DateUtil.now(), TypeOfVisit.Unscheduled);
        allClinicVisits.closeVisit(patientDocId, clinicVisitId, DateUtil.now());
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
            final boolean wasVisitDetailsEdited = (clinicVisit.getTreatmentAdviceId() != null ||
                    !clinicVisit.getLabResultIds().isEmpty() ||
                    clinicVisit.getVitalStatisticsId() != null ||
                    clinicVisit.getReportedOpportunisticInfectionsId() != null);
            if (wasVisitDetailsEdited)
                return redirectToShowClinicVisitUrl(clinicVisitId, patientDocId, httpServletRequest);
        } else {
            treatmentAdviceController.createForm(patientDocId, uiModel);
        }

        uiModel.addAttribute("patientId", patientDocId);
        if (clinicVisit.getVisitDate() == null) clinicVisit.setVisitDate(DateUtil.now());
        uiModel.addAttribute("clinicVisit", clinicVisit);
        labResultsController.createForm(patientDocId, uiModel);
        vitalStatisticsController.createForm(patientDocId, uiModel);
        opportunisticInfectionsController.createForm(clinicVisit, uiModel);
        return "clinicvisits/create";
    }

    @RequestMapping(value = "/create/{clinicVisitId}", method = RequestMethod.POST)
    public String create(@PathVariable("clinicVisitId") String clinicVisitId, ClinicVisit visit, TreatmentAdvice treatmentAdvice,
                         LabResultsUIModel labResultsUiModel, @Valid VitalStatistics vitalStatistics, @Valid OpportunisticInfectionsUIModel opportunisticInfections, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        List<String> labResultIds = new ArrayList<String>();
        String vitalStatisticsId = null;
        String reportedOpportunisticInfectionsId = null;
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
                return redirectToCreateFormUrl(clinicVisitId, treatmentAdvice.getPatientId(), httpServletRequest);
            }
        }
        labResultIds = labResultsController.create(labResultsUiModel, bindingResult, uiModel, httpServletRequest);
        vitalStatisticsId = vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel, httpServletRequest);

        try {
            reportedOpportunisticInfectionsId = opportunisticInfectionsController.create(opportunisticInfections, bindingResult, uiModel);
        } catch (RuntimeException e) {
            httpServletRequest.setAttribute("flash.flashErrorOpportunisticInfections", "Error occurred while creating Opportunistic Infections: " + e.getMessage());
        }

        try {
            allClinicVisits.updateVisit(clinicVisitId, visit.getVisitDate(), patientId, treatmentAdviceId, labResultIds, vitalStatisticsId, reportedOpportunisticInfectionsId);
        } catch (RuntimeException e) {
            httpServletRequest.setAttribute("flash.flashError", "Error occurred while creating clinic visit. Please try again: " + e.getMessage());
            return redirectToCreateFormUrl(clinicVisitId, treatmentAdvice.getPatientId(), httpServletRequest);
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
        uiModel.addAttribute("clinicVisit", clinicVisit);
        return "clinicvisits/show";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(@RequestParam(value = "patientId", required = true) String patientId, Model uiModel) {
        ClinicVisits clinicVisits = allClinicVisits.clinicVisits(patientId);
        Collections.sort(clinicVisits);
        uiModel.addAttribute("clinicVisits", clinicVisits);
        final Patient patient = clinicVisits.get(0).getPatient();
        uiModel.addAttribute("patient", patient);

        if (!patient.getStatus().isActive())
            return "clinicvisits/view_list";

        return "clinicvisits/manage_list";
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
    @RequestParam(value = "confirmedVisitDate") DateTime confirmedVisitDate) {
        allClinicVisits.confirmVisitDate(patientDocId, clinicVisitId, confirmedVisitDate);
        return "{'confirmedVisitDate':'" + confirmedVisitDate.toString(TAMAConstants.DATETIME_FORMAT) + "'}";
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

    private String redirectToShowClinicVisitUrl(String clinicVisitId, String patientId, HttpServletRequest httpServletRequest) {
        return "redirect:/clinicvisits/" + encodeUrlPathSegment(clinicVisitId, httpServletRequest) + "?patientId=" + patientId;
    }

    private String redirectToCreateFormUrl(String clinicVisitId, String patientId, HttpServletRequest httpServletRequest) {
        String queryParameters = "form&patientId=" + patientId + "&clinicVisitId=" + clinicVisitId;
        return "redirect:/clinicvisits?" + encodeUrlPathSegment(queryParameters, httpServletRequest);
    }
}
