package org.motechproject.tama.web;

import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping("/opportunisticInfections")
@Controller
public class OpportunisticInfectionsController extends BaseController {

    public static final String REDIRECT_AND_SHOW_CLINIC_VISIT = "redirect:/clinicvisits/";
    public static final String OPPORTUNISTIC_INFECTIONS_UIMODEL = "opportunisticInfectionsUIModel";
    private static final String UPDATE_FORM = "opportunisticInfections/update";

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

    public String create(@Valid OpportunisticInfectionsUIModel opportunisticInfectionsUIModel, BindingResult bindingResult, Model uiModel) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute(OPPORTUNISTIC_INFECTIONS_UIMODEL, opportunisticInfectionsUIModel);
            return null;
        }
        OpportunisticInfections opportunisticInfections = opportunisticInfectionsUIModel.getOpportunisticInfections();
        opportunisticInfections.setCaptureDate(DateUtil.today());
        allOpportunisticInfections.add(opportunisticInfections);
        return opportunisticInfections.getId();
    }

    public void show(String opportunisticInfectionsId, Model uiModel) {
        OpportunisticInfections opportunisticInfections = null;
        OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = new OpportunisticInfectionsUIModel();
        if (opportunisticInfectionsId != null)
            opportunisticInfections = allOpportunisticInfections.get(opportunisticInfectionsId);
        opportunisticInfectionsUIModel.setOpportunisticInfections(opportunisticInfections);
        uiModel.addAttribute("opportunisticInfectionsUIModel", opportunisticInfectionsUIModel);
    }

    @RequestMapping(value = "/update", params = "form", method = RequestMethod.GET)
    public String updateForm(@RequestParam(value = "patientId", required = true) String patientDocId, @RequestParam(value = "clinicVisitId", required = true) String clinicVisitId, Model uiModel) {
        final ClinicVisit clinicVisit = allClinicVisits.get(patientDocId, clinicVisitId);
        if (clinicVisit.getOpportunisticInfectionsId() == null) {
            uiModel.addAttribute("OpportunisticInfectionsUIModel", OpportunisticInfectionsUIModel.newDefault(clinicVisit));
        } else {
            uiModel.addAttribute("OpportunisticInfectionsUIModel", OpportunisticInfectionsUIModel.get(clinicVisit, allOpportunisticInfections.get(clinicVisit.getOpportunisticInfectionsId())));
        }
        uiModel.addAttribute("patient", clinicVisit.getPatient());
        uiModel.addAttribute("_method", "put");
        return UPDATE_FORM;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(OpportunisticInfectionsUIModel opportunisticInfectionsUIModel, HttpServletRequest httpServletRequest) {
        OpportunisticInfections opportunisticInfections = opportunisticInfectionsUIModel.getOpportunisticInfections();
        opportunisticInfections.setCaptureDate(DateUtil.today());
        if (opportunisticInfections.getId() == null || opportunisticInfections.getId().isEmpty()) {
            allOpportunisticInfections.add(opportunisticInfections);
            allClinicVisits.updateOpportunisticInfections(opportunisticInfectionsUIModel.getPatientId(), opportunisticInfectionsUIModel.getClinicVisitId(), opportunisticInfections.getId());
        } else {
            OpportunisticInfections savedOpportunisticInfections = allOpportunisticInfections.get(opportunisticInfections.getId());
            opportunisticInfections.setRevision(savedOpportunisticInfections.getRevision());
            allOpportunisticInfections.update(opportunisticInfections);
            allClinicVisits.updateOpportunisticInfections(opportunisticInfectionsUIModel.getPatientId(), opportunisticInfectionsUIModel.getClinicVisitId(), opportunisticInfections.getId());
        }
        return REDIRECT_AND_SHOW_CLINIC_VISIT + encodeUrlPathSegment(opportunisticInfectionsUIModel.getClinicVisitId(), httpServletRequest) + "?patientId=" + opportunisticInfectionsUIModel.getPatientId();
    }
}
