package org.motechproject.tama.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.*;
import org.motechproject.tama.web.mapper.TreatmentAdviceViewMapper;
import org.motechproject.tama.web.model.ComboBoxView;
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
    private TreatmentAdviceViewMapper treatmentAdviceViewMapper;

    protected TreatmentAdviceController() {
        treatmentAdviceViewMapper = new TreatmentAdviceViewMapper(regimens, treatmentAdvices, drugs);
    }

    public TreatmentAdviceController(MealAdviceTypes mealAdviceTypes, DosageTypes dosageTypes, Drugs drugs, Regimens regimens, TreatmentAdvices treatmentAdvices, TreatmentAdviceViewMapper treatmentAdviceViewMapper) {
        this.mealAdviceTypes = mealAdviceTypes;
        this.dosageTypes = dosageTypes;
        this.drugs = drugs;
        this.regimens = regimens;
        this.treatmentAdvices = treatmentAdvices;
        this.treatmentAdviceViewMapper = treatmentAdviceViewMapper;
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@RequestParam(value = "patientId", required = true) String patientId, Model uiModel, HttpServletRequest httpServletRequest) {
        TreatmentAdvice adviceForPatient = treatmentAdvices.findByPatientId(patientId);
        if (adviceForPatient != null) {
            return "redirect:/treatmentadvices/" + encodeUrlPathSegment(adviceForPatient.getId(), httpServletRequest);
        }
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId(patientId);
        uiModel.addAttribute("treatmentAdvice", treatmentAdvice);
        populateModel(uiModel);
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

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("treatmentAdvice", treatmentAdviceViewMapper.map(id));
        uiModel.addAttribute("itemId", id);
        return "treatmentadvices/show";
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

    public List<ComboBoxView> regimens() {
        List<Regimen> allRegimens = regimens.getAll();
        List<ComboBoxView> comboBoxViews = new ArrayList<ComboBoxView>();
        for (Regimen regimen : allRegimens) {
            comboBoxViews.add(new ComboBoxView(regimen.getId(), regimen.getRegimenDisplayName()));
        }
        return comboBoxViews;
    }

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

    private void populateModel(Model uiModel) {
        uiModel.addAttribute("regimens", regimens());
        uiModel.addAttribute("regimenCompositions", regimenCompositions());
    }
}
