package org.motechproject.tama.web;

import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
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

@RequestMapping("/clinicvisits")
@Controller
public class ClinicVisitsController extends BaseController {

    private TreatmentAdviceController treatmentAdviceController;
    private LabResultsController labResultsController;
    private VitalStatisticsController vitalStatisticsController;
    private AllTreatmentAdvices allTreatmentAdvices;

    @Autowired
    public ClinicVisitsController(TreatmentAdviceController treatmentAdviceController, AllTreatmentAdvices allTreatmentAdvices, LabResultsController labResultsController, VitalStatisticsController vitalStatisticsController) {
        this.treatmentAdviceController = treatmentAdviceController;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.labResultsController = labResultsController;
        this.vitalStatisticsController = vitalStatisticsController;
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@RequestParam(value = "patientId", required = true) String patientId, Model uiModel, HttpServletRequest httpServletRequest) {
        TreatmentAdvice adviceForPatient = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        if (adviceForPatient != null) {
            return "redirect:/clinicvisits/" + encodeUrlPathSegment(adviceForPatient.getId(), httpServletRequest);
        }

        uiModel.addAttribute("patientId", patientId);

        treatmentAdviceController.createForm(patientId, uiModel);
        labResultsController.createForm(patientId, uiModel, httpServletRequest);
        vitalStatisticsController.createForm(patientId, uiModel, httpServletRequest);

        return "clinicvisits/create";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(TreatmentAdvice treatmentAdvice, @Valid LabResultsUIModel labResultsUiModel, @Valid VitalStatistics vitalStatistics, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("treatmentAdvice", treatmentAdvice);
            return "clinicvisits/create";
        }

        treatmentAdviceController.create(treatmentAdvice, uiModel);
        labResultsController.create(labResultsUiModel, bindingResult, uiModel, httpServletRequest);
        vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel, httpServletRequest);
        return "redirect:/patients/" + encodeUrlPathSegment(treatmentAdvice.getPatientId(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel) {
        treatmentAdviceController.show(id, uiModel);
        return "clinicvisits/show";
    }
}
