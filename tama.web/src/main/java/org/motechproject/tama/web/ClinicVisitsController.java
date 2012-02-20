package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.web.model.LabResultsUIModel;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RequestMapping("/clinicvisits")
@Controller
public class ClinicVisitsController extends BaseController {

    private TreatmentAdviceController treatmentAdviceController;
    private LabResultsController labResultsController;
    private VitalStatisticsController vitalStatisticsController;
    private AllClinicVisits allClinicVisits;
    private AllTreatmentAdvices allTreatmentAdvices;

    @Autowired
    public ClinicVisitsController(TreatmentAdviceController treatmentAdviceController, AllTreatmentAdvices allTreatmentAdvices, LabResultsController labResultsController, VitalStatisticsController vitalStatisticsController, AllClinicVisits allClinicVisits) {
        this.treatmentAdviceController = treatmentAdviceController;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.labResultsController = labResultsController;
        this.vitalStatisticsController = vitalStatisticsController;
        this.allClinicVisits = allClinicVisits;
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
            return "redirect:/clinicvisits/" + encodeUrlPathSegment(clinicVisitId, httpServletRequest) + "?patientId=" + patientDocId;
        }

        uiModel.addAttribute("patientId", patientDocId);
        if (clinicVisit.getVisitDate() == null) clinicVisit.setVisitDate(DateUtil.now());
        uiModel.addAttribute("clinicVisit", clinicVisit);
        treatmentAdviceController.createForm(patientDocId, uiModel);
        labResultsController.createForm(patientDocId, uiModel);
        vitalStatisticsController.createForm(patientDocId, uiModel);
        return "clinicvisits/create";
    }

    @RequestMapping(value = "/create/{clinicVisitId}", method = RequestMethod.POST)
    public String create(@PathVariable("clinicVisitId") String clinicVisitId, ClinicVisit visit, TreatmentAdvice treatmentAdvice, LabResultsUIModel labResultsUiModel, @Valid VitalStatistics vitalStatistics, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            return "clinicvisits/create";
        }
        String treatmentAdviceId = treatmentAdviceController.create(bindingResult, uiModel, treatmentAdvice);
        List<String> labResultIds = labResultsController.create(labResultsUiModel, bindingResult, uiModel);
        String vitalStatisticsId = vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel);
        String patientId = treatmentAdvice.getPatientId();
        final String clinitVistId = allClinicVisits.updateVisit(clinicVisitId, visit.getVisitDate(), patientId, treatmentAdviceId, labResultIds, vitalStatisticsId);
        return "redirect:/clinicvisits/" + encodeUrlPathSegment(clinitVistId, httpServletRequest) + "?patientId=" + patientId;
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
        uiModel.addAttribute("clinicVisit", clinicVisit);
        return "clinicvisits/show";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(@RequestParam(value = "patientId", required = true) String patientId, Model uiModel) {
        List<ClinicVisit> clinicVisits = allClinicVisits.clinicVisits(patientId);
        Collections.sort(clinicVisits);
        uiModel.addAttribute("clinicVisits", clinicVisits);
        uiModel.addAttribute("patientId", patientId);
        return "clinicvisits/list";
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
        allClinicVisits.setVisitDate(patientDocId, clinicVisitId, visitDate);
        return "{'visitDate':'" + visitDate.toString(TAMAConstants.DATE_FORMAT) + "'}";
    }
}
