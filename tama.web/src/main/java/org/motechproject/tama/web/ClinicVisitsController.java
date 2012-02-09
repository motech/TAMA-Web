package org.motechproject.tama.web;

import org.motechproject.tama.patient.domain.ClinicVisit;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.ClinicVisitService;
import org.motechproject.tama.web.model.LabResultsUIModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RequestMapping("/clinicvisits")
@Controller
public class ClinicVisitsController extends BaseController {

    private TreatmentAdviceController treatmentAdviceController;
    private LabResultsController labResultsController;
    private VitalStatisticsController vitalStatisticsController;
    private AllTreatmentAdvices allTreatmentAdvices;
    private ClinicVisitService clinicVisitService;

    @Autowired
    public ClinicVisitsController(TreatmentAdviceController treatmentAdviceController, AllTreatmentAdvices allTreatmentAdvices, LabResultsController labResultsController, VitalStatisticsController vitalStatisticsController, ClinicVisitService clinicVisitService) {
        this.treatmentAdviceController = treatmentAdviceController;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.labResultsController = labResultsController;
        this.vitalStatisticsController = vitalStatisticsController;
        this.clinicVisitService = clinicVisitService;
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@RequestParam(value = "clinicVisitId", required = true) String clinicVisitId, Model uiModel, HttpServletRequest httpServletRequest) {
        ClinicVisit clinicVisit = clinicVisitService.getClinicVisit(clinicVisitId);
        String patientId = clinicVisit.getPatientId();
        final String treatmentAdviceId = clinicVisit.getTreatmentAdviceId();
        if (treatmentAdviceId != null) {
            TreatmentAdvice adviceForPatient = allTreatmentAdvices.get(treatmentAdviceId);// allTreatmentAdvices.currentTreatmentAdvice(patientId);
            if (adviceForPatient != null) {
                return "redirect:/clinicvisits/" + encodeUrlPathSegment(clinicVisitId, httpServletRequest);
            }
        }
        uiModel.addAttribute("patientId", patientId);
        uiModel.addAttribute("clinicVisit", clinicVisitService.getClinicVisit(clinicVisitId));
        treatmentAdviceController.createForm(patientId, uiModel);
        labResultsController.createForm(patientId, uiModel);
        vitalStatisticsController.createForm(patientId, uiModel);
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
        final String clinitVistId = clinicVisitService.createOrUpdateVisit(clinicVisitId, visit.getVisitDate(), treatmentAdvice.getPatientId(), treatmentAdviceId, labResultIds, vitalStatisticsId);
        return "redirect:/clinicvisits/" + encodeUrlPathSegment(clinitVistId, httpServletRequest);
    }

    @RequestMapping(value = "/{clinicVisitId}", method = RequestMethod.GET)
    public String show(@PathVariable("clinicVisitId") String clinicVisitId, Model uiModel) {
        ClinicVisit clinicVisit = clinicVisitService.getClinicVisit(clinicVisitId);
        treatmentAdviceController.show(clinicVisit.getTreatmentAdviceId(), uiModel);
        labResultsController.show(clinicVisit.getPatientId(), clinicVisit.getId(), clinicVisit.getLabResultIds(), uiModel);
        vitalStatisticsController.show(clinicVisit.getVitalStatisticsId(), uiModel);
        uiModel.addAttribute("clinicVisit", clinicVisit);
        return "clinicvisits/show";
    }

    @RequestMapping(value="/list", method = RequestMethod.GET)
    public String list(@RequestParam(value = "patientId", required = true) String patientId, Model uiModel) {
        List<ClinicVisit> clinicVisits = clinicVisitService.getClinicVisits(patientId);
        uiModel.addAttribute("clinicVisits", clinicVisits);
        return "clinicvisits/list";
    }
}
