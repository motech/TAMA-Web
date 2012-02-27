package org.motechproject.tama.web;

import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.domain.OpportunisticInfections;
import org.motechproject.tama.patient.repository.AllOpportunisticInfections;
import org.motechproject.tama.web.model.OpportunisticInfectionsUIModel;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/opportunisticInfections")
@Controller
public class OpportunisticInfectionsController extends BaseController {

    public static final String REDIRECT_AND_SHOW_CLINIC_VISIT = "redirect:/clinicvisits/";
    public static final String OPPORTUNISTIC_INFECTIONS_UIMODEL = "opportunisticInfectionsUIModel";

    private AllClinicVisits allClinicVisits;
    private AllOpportunisticInfections allOpportunisticInfections;

    @Autowired
    public OpportunisticInfectionsController(AllClinicVisits allClinicVisits, AllOpportunisticInfections allOpportunisticInfections) {
        this.allClinicVisits = allClinicVisits;
        this.allOpportunisticInfections = allOpportunisticInfections;
    }

    public void createForm(String patientId, Model uiModel) {
        uiModel.addAttribute(OPPORTUNISTIC_INFECTIONS_UIMODEL, new OpportunisticInfectionsUIModel(patientId));
    }

    public String create(OpportunisticInfectionsUIModel opportunisticInfectionsUIModel, BindingResult bindingResult, Model uiModel) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute(OPPORTUNISTIC_INFECTIONS_UIMODEL, opportunisticInfectionsUIModel);
            return null;
        }
        OpportunisticInfections opportunisticInfections = opportunisticInfectionsUIModel.getOpportunisticInfections();
        opportunisticInfections.setCaptureDate(DateUtil.today());
        allOpportunisticInfections.add(opportunisticInfections);
        return opportunisticInfections.getId();
    }
}
