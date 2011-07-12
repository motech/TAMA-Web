package org.motechproject.tama.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.*;
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

    protected TreatmentAdviceController() {
    }

    public TreatmentAdviceController(MealAdviceTypes mealAdviceTypes, DosageTypes dosageTypes, Drugs drugs, Regimens regimens, TreatmentAdvices treatmentAdvices) {
        this.mealAdviceTypes = mealAdviceTypes;
        this.dosageTypes = dosageTypes;
        this.drugs = drugs;
        this.regimens = regimens;
        this.treatmentAdvices = treatmentAdvices;
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@RequestParam(value = "patientId", required = true) String patientId, Model uiModel) {
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId(patientId);
        uiModel.addAttribute("treatmentAdvice", treatmentAdvice);
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
        return "redirect:/patients/" + encodeUrlPathSegment(treatmentAdvice.getPatientId(), httpServletRequest);
    }

	@RequestMapping(method = RequestMethod.GET, value = "/regimenCompositionsFor")
	public @ResponseBody Set<ComboBoxView> regimenCompositionsFor(@RequestParam String regimenId) {
        Set<RegimenComposition> compositions = regimens.get(regimenId).getCompositions();
        Set<ComboBoxView> comboBoxViews = new HashSet<ComboBoxView>();
        for (RegimenComposition regimenComposition : compositions) {
            List<Drug> allDrugs = this.drugs.getDrugs(regimenComposition.getDrugIds());
            String displayName = StringUtils.join(allDrugs.toArray(), " / ");
            comboBoxViews.add(new ComboBoxView(regimenComposition.getRegimenCompositionId(), displayName));
        }
        return comboBoxViews;
	}

    @RequestMapping(method = RequestMethod.GET, value = "/drugDosagesFor")
	public String drugDosagesFor(@RequestParam String regimenId, @RequestParam String regimenCompositionId, @ModelAttribute("treatmentAdvice") TreatmentAdvice treatmentAdvice) {
        Regimen regimen = regimens.get(regimenId);
        RegimenComposition regimenComposition = regimen.getCompositionsFor(regimenCompositionId);

        List<Drug> allDrugs = this.drugs.getDrugs(regimenComposition.getDrugIds());
        for (Drug drug: allDrugs) {
            DrugDosage drugDosage = new DrugDosage();
            drugDosage.setDrugId(drug.getId());
            drugDosage.setDrugName(drug.getName());
            drugDosage.setBrands(drug.getBrands());
            treatmentAdvice.addDrugDosage(drugDosage);
        }
        return "treatmentadvices/drugdosages";
	}

    @ModelAttribute("regimens")
    public List<ComboBoxView> regimens() {
        List<Regimen> allRegimens = regimens.getAll();
        List<ComboBoxView> comboBoxViews = new ArrayList<ComboBoxView>();
        for (Regimen regimen : allRegimens) {
            comboBoxViews.add(new ComboBoxView(regimen.getId(), regimen.getRegimenDisplayName()));
        }
        return comboBoxViews;
    }

    @ModelAttribute("regimenCompositions")
    public List<String> regimenCompositions() {
        ArrayList<String> regimenCompositions = new ArrayList<String>();
        regimenCompositions.add("regimenCompositions");
        return regimenCompositions;
    }

    @ModelAttribute("mealAdviceTypes")
    public List<MealAdviceType> mealAdviceTypes() {
        return mealAdviceTypes.getAll();
    }

    @ModelAttribute("dosageTypes")
    public List<DosageType> dosageTypes() {
        return dosageTypes.getAll();
    }
}
