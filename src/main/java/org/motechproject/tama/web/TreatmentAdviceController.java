package org.motechproject.tama.web;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.DosageTypes;
import org.motechproject.tama.repository.MealAdviceTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RooWebScaffold(path = "treatmentadvices", formBackingObject = TreatmentAdvice.class)
@RequestMapping("/treatmentadvices")
@Controller
public class TreatmentAdviceController {

    @Autowired
    private MealAdviceTypes mealAdviceTypes;

    @Autowired
    private DosageTypes dosageTypes;

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
        treatmentAdvice.persist();
        return "redirect:/patients/" + encodeUrlPathSegment(treatmentAdvice.getPatientId(), httpServletRequest);
    }

	@RequestMapping(method = RequestMethod.GET, value = "/regimenCompositionsFor")
	public @ResponseBody Set<ComboBoxView> regimenCompositionsFor(@RequestParam String regimenId) {
        Set<RegimenComposition> compositions = Regimen.findRegimen(regimenId).getCompositions();
        Set<ComboBoxView> comboBoxViews = new HashSet<ComboBoxView>();
        for (RegimenComposition regimenComposition : compositions) {
            comboBoxViews.add(new ComboBoxView(regimenComposition.getRegimenCompositionId(), regimenComposition.getDisplayName()));
        }
        return comboBoxViews;
	}

    @RequestMapping(method = RequestMethod.GET, value = "/drugDosagesFor")
	public String drugDosagesFor(@RequestParam String regimenId, @RequestParam String regimenCompositionId, @ModelAttribute("treatmentAdvice") TreatmentAdvice treatmentAdvice, Model uiModel) {
        Regimen regimen = Regimen.findRegimen(regimenId);
        RegimenComposition regimenComposition = regimen.getCompositionsFor(regimenCompositionId);

        Set<Drug> drugs = regimenComposition.getDrugs();
        for(int i=0; i< drugs.size(); i++) {
            DrugDosage drugDosage = new DrugDosage();
            Drug drug = (Drug) CollectionUtils.get(drugs, i);
            drugDosage.setDrugId(((Drug) drug).getId());
            drugDosage.setDrugName(drug.getName());
            drugDosage.setBrands(drug.getBrands());
            treatmentAdvice.addDrugDosage(drugDosage);
        }
        return "treatmentadvices/drugdosages";
	}

    @ModelAttribute("regimens")
    public List<ComboBoxView> regimens() {
        List<Regimen> allRegimens = Regimen.findAllRegimens();
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

    private String encodeUrlPathSegment(String pathSegment, HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
        return pathSegment;
    }

}
