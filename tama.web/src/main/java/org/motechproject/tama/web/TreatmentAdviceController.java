package org.motechproject.tama.web;

import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.ClinicVisitService;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.tama.refdata.domain.DosageType;
import org.motechproject.tama.refdata.domain.MealAdviceType;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllDosageTypes;
import org.motechproject.tama.refdata.repository.AllMealAdviceTypes;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.web.mapper.TreatmentAdviceViewMapper;
import org.motechproject.tama.web.model.ComboBoxView;
import org.motechproject.tama.web.view.DosageTypesView;
import org.motechproject.tama.web.view.MealAdviceTypesView;
import org.motechproject.tama.web.view.RegimensView;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/treatmentadvices")
@Controller
public class TreatmentAdviceController extends BaseController {

    private AllMealAdviceTypes allMealAdviceTypes;
    private AllDosageTypes allDosageTypes;
    private AllRegimens allRegimens;
    private AllPatients allPatients;
    private TreatmentAdviceService treatmentAdviceService;
    private ClinicVisitService clinicVisitService;
    private TreatmentAdviceViewMapper treatmentAdviceViewMapper;

    @Autowired
    public TreatmentAdviceController(AllPatients allPatients, AllRegimens allRegimens, AllDosageTypes allDosageTypes, AllMealAdviceTypes allMealAdviceTypes, TreatmentAdviceService treatmentAdviceService, TreatmentAdviceViewMapper treatmentAdviceViewMapper, ClinicVisitService clinicVisitService) {
        this.allPatients = allPatients;
        this.allRegimens = allRegimens;
        this.allDosageTypes = allDosageTypes;
        this.allMealAdviceTypes = allMealAdviceTypes;
        this.treatmentAdviceService = treatmentAdviceService;
        this.treatmentAdviceViewMapper = treatmentAdviceViewMapper;
        this.clinicVisitService = clinicVisitService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ajax/regimens")
    @ResponseBody
    List<Regimen> allRegimens() {
        return this.allRegimens.getAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/changeRegimen")
    public String changeRegimenForm(@RequestParam String id, @RequestParam String patientId, @RequestParam String clinicVisitId, Model uiModel) {
        uiModel.addAttribute("clinicVisitId", clinicVisitId);
        uiModel.addAttribute("adviceEndDate", DateUtil.today().toString(TAMAConstants.DATE_FORMAT));
        uiModel.addAttribute("existingTreatmentAdviceId", id);
        uiModel.addAttribute("discontinuationReason", "");
        createForm(patientId, uiModel);
        return "treatmentadvices/update";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String changeRegimen(String existingTreatmentAdviceId, String discontinuationReason, TreatmentAdvice treatmentAdvice, String clinicVisitId, Model uiModel, HttpServletRequest httpServletRequest) {
        uiModel.asMap().clear();
        fixTimeString(treatmentAdvice);
        final String newTreatmentAdviceId = treatmentAdviceService.changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice);
        clinicVisitService.changeRegimen(clinicVisitId, newTreatmentAdviceId);
        return "redirect:/clinicvisits/" + encodeUrlPathSegment(treatmentAdvice.getPatientId(), httpServletRequest);
    }

    public void createForm(String patientId, Model uiModel) {
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        treatmentAdvice.setPatientId(patientId);
        treatmentAdvice.setDrugCompositionGroupId("");
        populateModel(uiModel, treatmentAdvice);
    }

    public String create(BindingResult bindingResult, Model uiModel, TreatmentAdvice treatmentAdvice) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("treatmentAdvice", treatmentAdvice);
            return null;
        }
        uiModel.asMap().clear();
        fixTimeString(treatmentAdvice);
        return treatmentAdviceService.createRegimen(treatmentAdvice);
    }

    private void fixTimeString(TreatmentAdvice treatmentAdvice) {
        for (DrugDosage drugDosage : treatmentAdvice.getDrugDosages()) {
            final String morningTime = drugDosage.getMorningTime();
            if (morningTime != null && !morningTime.isEmpty())
                drugDosage.setMorningTime(morningTime + "am");
            final String eveningTime = drugDosage.getEveningTime();
            if (eveningTime != null && !eveningTime.isEmpty())
                drugDosage.setEveningTime(eveningTime + "pm");
        }
    }

    public void show(String treatmentAdviceId, Model uiModel) {
        uiModel.addAttribute("treatmentAdvice", treatmentAdviceViewMapper.map(treatmentAdviceId));
        uiModel.addAttribute("itemId", treatmentAdviceId);
    }

    public List<ComboBoxView> regimens() {
        List<Regimen> allRegimens = new RegimensView(this.allRegimens).getAll();
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
        return new MealAdviceTypesView(allMealAdviceTypes).getAll();
    }

    @ModelAttribute("dosageTypes")
    public List<DosageType> dosageTypes() {
        return new DosageTypesView(allDosageTypes).getAll();
    }

    private void populateModel(Model uiModel, TreatmentAdvice treatmentAdvice) {
        uiModel.addAttribute("treatmentAdvice", treatmentAdvice);
        uiModel.addAttribute("patientIdentifier", allPatients.get(treatmentAdvice.getPatientId()).getPatientId());
        uiModel.addAttribute("regimens", regimens());
        uiModel.addAttribute("drugCompositions", drugCompositions());
        uiModel.addAttribute("drugCompositionGroups", drugCompositionGroups());
        uiModel.addAttribute("dosageTypes", dosageTypes());
        uiModel.addAttribute("mealAdviceTypes", mealAdviceTypes());
    }
}
