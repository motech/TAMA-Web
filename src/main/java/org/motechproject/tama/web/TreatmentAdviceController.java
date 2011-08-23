package org.motechproject.tama.web;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.DosageType;
import org.motechproject.tama.domain.MealAdviceType;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.mapper.PillRegimenRequestMapper;
import org.motechproject.tama.repository.*;
import org.motechproject.tama.web.mapper.TreatmentAdviceViewMapper;
import org.motechproject.tama.web.model.ComboBoxView;
import org.motechproject.tama.web.view.DosageTypesView;
import org.motechproject.tama.web.view.MealAdviceTypesView;
import org.motechproject.tama.web.view.RegimensView;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RooWebScaffold(path = "treatmentadvices", formBackingObject = TreatmentAdvice.class)
@RequestMapping("/treatmentadvices")
@Controller
public class TreatmentAdviceController extends BaseController {

    @Autowired
    private MealAdviceTypes mealAdviceTypes;
    @Autowired
    private DosageTypes dosageTypes;
    @Autowired
    private Drugs drugs;
    @Autowired
    private Regimens regimens;
    @Autowired
    private TreatmentAdvices treatmentAdvices;
    @Autowired
    private Patients patients;
    @Qualifier("pillReminderService")
    @Autowired
    private PillReminderService pillReminderService;
    @Autowired
    private PillRegimenRequestMapper pillRegimenRequestMapper;

    protected TreatmentAdviceController() {
    }

    public TreatmentAdviceController(TreatmentAdvices treatmentAdvices, Patients patients, Regimens regimens, Drugs drugs, DosageTypes dosageTypes, MealAdviceTypes mealAdviceTypes, PillReminderService pillReminderService, PillRegimenRequestMapper requestMapper) {
        this.treatmentAdvices = treatmentAdvices;
        this.patients = patients;
        this.regimens = regimens;
        this.drugs = drugs;
        this.dosageTypes = dosageTypes;
        this.mealAdviceTypes = mealAdviceTypes;
        this.pillReminderService = pillReminderService;
        this.pillRegimenRequestMapper = requestMapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ajax/regimens")
    public @ResponseBody List<Regimen> allRegimens() {
        List<Regimen> allRegimens = regimens.getAll();
        return allRegimens;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/changeRegimen")
    public String changeRegimenForm(@RequestParam String id, @RequestParam String patientId, Model uiModel) {
        uiModel.addAttribute("adviceEndDate", DateUtil.today().toString(TAMAConstants.DATE_FORMAT));
        uiModel.addAttribute("existingTreatmentAdviceId", id);
        createForm(patientId, uiModel);
        return "treatmentadvices/update";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String changeRegimen(String existingTreatmentAdviceId, String discontinuationReason, TreatmentAdvice treatmentAdvice, Model uiModel, HttpServletRequest httpServletRequest) {
        endCurrentRegimen(existingTreatmentAdviceId, discontinuationReason);
        uiModel.asMap().clear();
        treatmentAdvices.add(treatmentAdvice);
        pillReminderService.renew(pillRegimenRequestMapper.map(treatmentAdvice));
        return "redirect:/clinicvisits/" + encodeUrlPathSegment(treatmentAdvice.getId(), httpServletRequest);
    }

    private void endCurrentRegimen(String treatmentAdviceId, String discontinuationReason) {
        TreatmentAdvice existingTreatmentAdvice = treatmentAdvices.get(treatmentAdviceId);
        existingTreatmentAdvice.setReasonForDiscontinuing(discontinuationReason);
        existingTreatmentAdvice.endTheRegimen();
        treatmentAdvices.update(existingTreatmentAdvice);
    }

    public void createForm(String patientId, Model uiModel) {
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        treatmentAdvice.setPatientId(patientId);
        treatmentAdvice.setDrugCompositionGroupId("");
        populateModel(uiModel, treatmentAdvice);
    }

    public void create(TreatmentAdvice treatmentAdvice, Model uiModel) {
        uiModel.asMap().clear();
        treatmentAdvices.add(treatmentAdvice);
        pillReminderService.createNew(pillRegimenRequestMapper.map(treatmentAdvice));
    }

    public void show(String id, Model uiModel) {
        TreatmentAdviceViewMapper treatmentAdviceViewMapper = new TreatmentAdviceViewMapper(treatmentAdvices, patients, regimens, drugs, dosageTypes, mealAdviceTypes);
        uiModel.addAttribute("treatmentAdvice", treatmentAdviceViewMapper.map(id));
        uiModel.addAttribute("itemId", id);
    }

    public List<ComboBoxView> regimens() {
        List<Regimen> allRegimens = new RegimensView(regimens).getAll();
        List<ComboBoxView> comboBoxViews = new ArrayList<ComboBoxView>();
        for (Regimen regimen : allRegimens) {
            comboBoxViews.add(new ComboBoxView(regimen.getId(), regimen.getDisplayName()));
        }
        return comboBoxViews;
    }

    public List<String> drugCompositionGroups() {
        ArrayList<String> drugCompositionGroups = new ArrayList<String>();
        drugCompositionGroups.add("drugCompositionGroups");
        return drugCompositionGroups;
    }

    public List<String> drugCompositions() {
        ArrayList<String> drugCompositions = new ArrayList<String>();
        drugCompositions.add("drugCompositions");
        return drugCompositions;
    }

    @ModelAttribute("mealAdviceTypes")
    public List<MealAdviceType> mealAdviceTypes() {
        return new MealAdviceTypesView(mealAdviceTypes).getAll();
    }

    @ModelAttribute("dosageTypes")
    public List<DosageType> dosageTypes() {
        return new DosageTypesView(dosageTypes).getAll();
    }

    private void populateModel(Model uiModel, TreatmentAdvice treatmentAdvice) {
        uiModel.addAttribute("treatmentAdvice", treatmentAdvice);
        uiModel.addAttribute("patientIdentifier", patients.get(treatmentAdvice.getPatientId()).getPatientId());
        uiModel.addAttribute("regimens", regimens());
        uiModel.addAttribute("drugCompositions", drugCompositions());
        uiModel.addAttribute("drugCompositionGroups", drugCompositionGroups());
        uiModel.addAttribute("dosageTypes", dosageTypes());
        uiModel.addAttribute("mealAdviceTypes", mealAdviceTypes());
    }
}
