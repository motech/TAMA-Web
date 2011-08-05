package org.motechproject.tama.web;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.mapper.PillRegimenRequestMapper;
import org.motechproject.tama.repository.*;
import org.motechproject.tama.web.mapper.TreatmentAdviceViewMapper;
import org.motechproject.tama.web.model.ComboBoxView;
import org.motechproject.tama.web.view.DosageTypesView;
import org.motechproject.tama.web.view.MealAdviceTypesView;
import org.motechproject.tama.web.view.RegimensView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@RequestParam(value = "patientId", required = true) String patientId, Model uiModel, HttpServletRequest httpServletRequest) {
        TreatmentAdvice adviceForPatient = treatmentAdvices.findByPatientId(patientId);
        if (adviceForPatient != null) {
            return "redirect:/treatmentadvices/" + encodeUrlPathSegment(adviceForPatient.getId(), httpServletRequest);
        }
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        treatmentAdvice.setPatientId(patientId);
        treatmentAdvice.setDrugCompositionGroupId("");
        populateModel(uiModel, treatmentAdvice);

        return "treatmentadvices/create";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid TreatmentAdvice treatmentAdvice, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("treatmentAdvice", treatmentAdvice);
            return "treatmentadvices/create";
        }
        uiModel.asMap().clear();
        treatmentAdvices.add(treatmentAdvice);
        pillReminderService.createNew(pillRegimenRequestMapper.map(treatmentAdvice));
        return "redirect:/patients/" + encodeUrlPathSegment(treatmentAdvice.getPatientId(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel) {
        TreatmentAdviceViewMapper treatmentAdviceViewMapper = new TreatmentAdviceViewMapper(treatmentAdvices, patients, regimens, drugs, dosageTypes, mealAdviceTypes);
        uiModel.addAttribute("treatmentAdvice", treatmentAdviceViewMapper.map(id));
        uiModel.addAttribute("itemId", id);
        return "treatmentadvices/show";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ajax/drugCompositionsFor")
    public
    @ResponseBody
    Set<ComboBoxView> drugCompositionsFor(@RequestParam String regimenId) {
        Set<DrugComposition> compositions = regimens.get(regimenId).getDrugCompositions();
        Set<ComboBoxView> comboBoxViews = new HashSet<ComboBoxView>();
        for (DrugComposition drugComposition : compositions) {
            comboBoxViews.add(new ComboBoxView(drugComposition.getId(), drugComposition.getDisplayName()));
        }
        return comboBoxViews;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ajax/drugDosagesFor")
    public String drugDosagesFor(@RequestParam String regimenId, @RequestParam String drugCompositionId, @ModelAttribute("treatmentAdvice") TreatmentAdvice treatmentAdvice) {
        Regimen regimen = regimens.get(regimenId);
        DrugComposition drugComposition = regimen.getDrugCompositionFor(drugCompositionId);

        List<Drug> allDrugs = this.drugs.getDrugs(drugComposition.getDrugIds());

        for (Drug drug : allDrugs) {
            DrugDosage drugDosage = new DrugDosage();
            drugDosage.setDrugId(drug.getId());
            drugDosage.setDrugName(drug.getName());
            drugDosage.setBrands(drug.getBrands());
            treatmentAdvice.addDrugDosage(drugDosage);
        }

        return "treatmentadvices/drugdosages";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ajax/regimens")
    public @ResponseBody List<Regimen> allRegimens() {
        List<Regimen> allRegimens = regimens.getAll();
        return allRegimens;
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
