package org.motechproject.tama.web;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.tama.domain.*;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RooWebScaffold(path = "treatmentadvices", formBackingObject = TreatmentAdvice.class)
@RequestMapping("/treatmentadvices")
@Controller
public class TreatmentAdviceController {
	
	@ModelAttribute("regimens")
	public List<Regimen> regimens() {
		return Regimen.findAllRegimens();
	}

	@ModelAttribute("mealAdviceTypes")
	public List<MealAdviceType> mealAdviceTypes() {
		return MealAdviceType.findAllMealAdviceTypes();
	}

	@ModelAttribute("dosageTypes")
	public List<DosageType> dosageTypes() {
		return DosageType.findAllDosageTypes();
	}

	@ModelAttribute("regimenCompositions")
	public List<String> regimenCompositions() {
        ArrayList<String> regimenCompositions = new ArrayList<String>();
        for(Regimen regimen : regimens()) {
            for (RegimenComposition composition : regimen.getCompositions()) {
                regimenCompositions.add(composition.getRegimentCompositionId());
            }
        }
        return regimenCompositions;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/regimenCompositionsFor")
	public @ResponseBody Set<String> regimenCompositionsFor(@RequestParam String regimenId) {
        Set<String> regimenCompositions = new HashSet<String>();
        Set<RegimenComposition> compositions = Regimen.findRegimen(regimenId).getCompositions();
        for (RegimenComposition composition : compositions) {
            regimenCompositions.add(composition.getDisplayName());
        }
        return regimenCompositions;
	}

    @RequestMapping(method = RequestMethod.GET, value = "/drugDosagesFor")
	public String drugDosagesFor(@RequestParam String regimenId, @RequestParam String regimenCompositionId, Model uiModel) {
        Regimen regimen = Regimen.findRegimen(regimenId);
        RegimenComposition regimenComposition = regimen.getCompositionsFor(regimenCompositionId);

        Set<Drug> drugs = regimenComposition.getDrugs();
        for(int i=0; i< drugs.size(); i++) {
            DrugDosage drugDosage = new DrugDosage();
            Drug drug = (Drug) CollectionUtils.get(drugs, i);
            drugDosage.setDrugId(((Drug) drug).getId());
            drugDosage.setDrugName(drug.getName());
            uiModel.addAttribute(String.format("drugDosages[%d]", i), drugDosage);
        }
        uiModel.addAttribute("drugs", drugs);
        return "treatmentadvices/drugdosages";
	}

}
