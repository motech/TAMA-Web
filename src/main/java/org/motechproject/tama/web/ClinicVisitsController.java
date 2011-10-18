package org.motechproject.tama.web;

import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@RooWebScaffold(path = "clinicvisits", formBackingObject = TreatmentAdvice.class)
@RequestMapping("/clinicvisits")
@Controller
public class ClinicVisitsController extends BaseController {


    @Qualifier("treatmentAdviceController")
    @Autowired
    private TreatmentAdviceController treatmentAdviceController;
    @Autowired
    private AllTreatmentAdvices allTreatmentAdvices;

    protected ClinicVisitsController() {
    }

    public ClinicVisitsController(TreatmentAdviceController treatmentAdviceController, AllTreatmentAdvices allTreatmentAdvices) {
        this.treatmentAdviceController = treatmentAdviceController;
        this.allTreatmentAdvices = allTreatmentAdvices;
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@RequestParam(value = "patientId", required = true) String patientId, Model uiModel, HttpServletRequest httpServletRequest) {
        TreatmentAdvice adviceForPatient = allTreatmentAdvices.findByPatientId(patientId);
        if (adviceForPatient != null) {
            return "redirect:/clinicvisits/" + encodeUrlPathSegment(adviceForPatient.getId(), httpServletRequest);
        }
        treatmentAdviceController.createForm(patientId, uiModel);
        return "clinicvisits/create";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(TreatmentAdvice treatmentAdvice, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("treatmentAdvice", treatmentAdvice);
            return "clinicvisits/create";
        }
        treatmentAdviceController.create(treatmentAdvice, uiModel);
        return "redirect:/patients/" + encodeUrlPathSegment(treatmentAdvice.getPatientId(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel) {
        treatmentAdviceController.show(id, uiModel);
        return "clinicvisits/show";
    }
}
