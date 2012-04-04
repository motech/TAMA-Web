package org.motechproject.tama.web;

import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.CallTimeSlotService;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.tama.refdata.domain.DosageType;
import org.motechproject.tama.refdata.domain.MealAdviceType;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.objectcache.AllDosageTypesCache;
import org.motechproject.tama.refdata.objectcache.AllMealAdviceTypesCache;
import org.motechproject.tama.refdata.objectcache.AllRegimensCache;
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

    private AllMealAdviceTypesCache allMealAdviceTypes;
    private AllDosageTypesCache allDosageTypes;
    private AllRegimensCache allRegimens;
    private AllPatients allPatients;
    private TreatmentAdviceService treatmentAdviceService;
    private AllClinicVisits allClinicVisits;
    private TreatmentAdviceViewMapper treatmentAdviceViewMapper;
    private CallTimeSlotService callTimeSlotService;

    @Autowired
    public TreatmentAdviceController(AllPatients allPatients, AllRegimensCache allRegimens, AllDosageTypesCache allDosageTypes, AllMealAdviceTypesCache allMealAdviceTypes, TreatmentAdviceService treatmentAdviceService, TreatmentAdviceViewMapper treatmentAdviceViewMapper, AllClinicVisits allClinicVisits, CallTimeSlotService callTimeSlotService) {
        this.allPatients = allPatients;
        this.allRegimens = allRegimens;
        this.allDosageTypes = allDosageTypes;
        this.allMealAdviceTypes = allMealAdviceTypes;
        this.treatmentAdviceService = treatmentAdviceService;
        this.treatmentAdviceViewMapper = treatmentAdviceViewMapper;
        this.allClinicVisits = allClinicVisits;
        this.callTimeSlotService = callTimeSlotService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/regimens")
    @ResponseBody
    List<Regimen> allRegimens() {
        return this.allRegimens.getAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/changeRegimen")
    public String changeRegimenForm(@RequestParam String id, @RequestParam String patientId, @RequestParam String clinicVisitId, Model uiModel) {
        Patient patient = allPatients.get(patientId);
        uiModel.addAttribute("patient", patient);
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
        String treatmentAdviceId = existingTreatmentAdviceId;
        try {
            treatmentAdviceId = treatmentAdviceService.changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice, loggedInUserId(httpServletRequest));
            allClinicVisits.changeRegimen(treatmentAdvice.getPatientId(), clinicVisitId, treatmentAdviceId);
            return ClinicVisitsController.redirectToCreateFormUrl(clinicVisitId, treatmentAdvice.getPatientId());
        } catch (RuntimeException e){
            httpServletRequest.setAttribute("flash.flashError", "Error occurred while changing Regimen. Please try again: " + e.getMessage());
            return "redirect:/treatmentadvices/changeRegimen?id=" + treatmentAdviceId + "&clinicVisitId=" + clinicVisitId + "&patientId=" + treatmentAdvice.getPatientId();
        }
    }

    public void createForm(String patientId, Model uiModel) {
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        treatmentAdvice.setPatientId(patientId);
        treatmentAdvice.setDrugCompositionGroupId("");
        populateModel(uiModel, treatmentAdvice);
    }

    public String create(BindingResult bindingResult, Model uiModel, TreatmentAdvice treatmentAdvice, String userName) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("treatmentAdvice", treatmentAdvice);
            return null;
        }
        uiModel.asMap().clear();
        fixTimeString(treatmentAdvice);
        return treatmentAdviceService.createRegimen(treatmentAdvice, userName);
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
        final Patient patient = allPatients.get(treatmentAdvice.getPatientId());
        uiModel.addAttribute("treatmentAdvice", treatmentAdvice);
        uiModel.addAttribute("patientIdentifier", patient.getPatientId());
        uiModel.addAttribute("regimens", regimens());
        uiModel.addAttribute("drugCompositions", drugCompositions());
        uiModel.addAttribute("drugCompositionGroups", drugCompositionGroups());
        uiModel.addAttribute("dosageTypes", dosageTypes());
        uiModel.addAttribute("mealAdviceTypes", mealAdviceTypes());
        uiModel.addAttribute("callPlan", patient.getPatientPreferences().getCallPreference());
        uiModel.addAttribute("morningTimeSlots", callTimeSlotService.availableMorningSlots());
        uiModel.addAttribute("eveningTimeSlots", callTimeSlotService.availableEveningSlots());
    }
}
