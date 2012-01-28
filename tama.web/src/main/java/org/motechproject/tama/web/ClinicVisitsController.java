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
    public String createForm(@RequestParam(value = "patientId", required = true) String patientId, Model uiModel, HttpServletRequest httpServletRequest) {
        TreatmentAdvice adviceForPatient = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        if (adviceForPatient != null) {
            return "redirect:/clinicvisits/" + encodeUrlPathSegment(patientId, httpServletRequest);
        }

        uiModel.addAttribute("patientId", patientId);

        treatmentAdviceController.createForm(patientId, uiModel);
        labResultsController.createForm(patientId, uiModel);
        vitalStatisticsController.createForm(patientId, uiModel);

        return "clinicvisits/create";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(TreatmentAdvice treatmentAdvice, LabResultsUIModel labResultsUiModel, @Valid VitalStatistics vitalStatistics, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        String treatmentAdviceId = treatmentAdviceController.create(bindingResult, uiModel, treatmentAdvice);
        List<String> labResultIds = labResultsController.create(labResultsUiModel, bindingResult, uiModel);
        String vitalStatisticsId = vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel);

        if (bindingResult.hasErrors()) {
            return "clinicvisits/create";
        }

        clinicVisitService.createVisit(treatmentAdvice.getPatientId(), treatmentAdviceId, labResultIds, vitalStatisticsId);

        return "redirect:/patients/" + encodeUrlPathSegment(treatmentAdvice.getPatientId(), httpServletRequest);
    }

    @RequestMapping(value = "/{patientId}", method = RequestMethod.GET)
    public String show(@PathVariable("patientId") String patientId, Model uiModel) {
        ClinicVisit clinicVisit = clinicVisitService.visitZero(patientId);
        treatmentAdviceController.show(clinicVisit.getTreatmentAdviceId(), uiModel);
        labResultsController.show(patientId, clinicVisit.getId(), clinicVisit.getLabResultIds(), uiModel);
        vitalStatisticsController.show(clinicVisit.getVitalStatisticsId(), uiModel);

        uiModel.addAttribute("clinicVisitId", clinicVisit.getId());

        return "clinicvisits/show";
    }
}
