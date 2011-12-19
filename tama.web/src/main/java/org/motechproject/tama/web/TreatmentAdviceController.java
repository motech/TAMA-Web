package org.motechproject.tama.web;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderSchedulerService;
import org.motechproject.tama.fourdayrecall.service.FourDayRecallSchedulerService;
import org.motechproject.tama.mapper.PillRegimenRequestMapper;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientPreferences;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.refdata.domain.DosageType;
import org.motechproject.tama.refdata.domain.MealAdviceType;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllDosageTypes;
import org.motechproject.tama.refdata.repository.AllDrugs;
import org.motechproject.tama.refdata.repository.AllMealAdviceTypes;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.web.mapper.TreatmentAdviceViewMapper;
import org.motechproject.tama.web.model.ComboBoxView;
import org.motechproject.tama.web.view.DosageTypesView;
import org.motechproject.tama.web.view.MealAdviceTypesView;
import org.motechproject.tama.web.view.RegimensView;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AllMealAdviceTypes allMealAdviceTypes;
    @Autowired
    private AllDosageTypes allDosageTypes;
    @Autowired
    private AllDrugs allDrugs;
    @Autowired
    private AllRegimens allRegimens;
    @Autowired
    private AllTreatmentAdvices allTreatmentAdvices;
    @Autowired
    private AllPatients allPatients;
    @Autowired
    private PillReminderService pillReminderService;
    @Autowired
    private PillRegimenRequestMapper pillRegimenRequestMapper;
    @Autowired
    private DailyPillReminderSchedulerService dailyPillReminderSchedulerService;
    @Autowired
    private FourDayRecallSchedulerService fourDayRecallSchedulerService;

    protected TreatmentAdviceController() {
    }

    public TreatmentAdviceController(AllTreatmentAdvices allTreatmentAdvices, AllPatients allPatients, AllRegimens allRegimens, AllDrugs allDrugs, AllDosageTypes allDosageTypes, AllMealAdviceTypes allMealAdviceTypes, PillReminderService pillReminderService, DailyPillReminderSchedulerService dailyPillReminderSchedulerService, FourDayRecallSchedulerService fourDayRecallSchedulerService, PillRegimenRequestMapper requestMapper) {
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allPatients = allPatients;
        this.allRegimens = allRegimens;
        this.allDrugs = allDrugs;
        this.allDosageTypes = allDosageTypes;
        this.allMealAdviceTypes = allMealAdviceTypes;
        this.pillReminderService = pillReminderService;
        this.dailyPillReminderSchedulerService = dailyPillReminderSchedulerService;
        this.fourDayRecallSchedulerService = fourDayRecallSchedulerService;
        this.pillRegimenRequestMapper = requestMapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ajax/regimens")
    @ResponseBody
    List<Regimen> allRegimens() {
        return this.allRegimens.getAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/changeRegimen")
    public String changeRegimenForm(@RequestParam String id, @RequestParam String patientId, Model uiModel) {
        uiModel.addAttribute("adviceEndDate", DateUtil.today().toString(TAMAConstants.DATE_FORMAT));
        uiModel.addAttribute("existingTreatmentAdviceId", id);
        uiModel.addAttribute("discontinuationReason", "");
        createForm(patientId, uiModel);
        return "treatmentadvices/update";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String changeRegimen(String existingTreatmentAdviceId, String discontinuationReason, TreatmentAdvice treatmentAdvice, Model uiModel, HttpServletRequest httpServletRequest) {
        endCurrentRegimen(existingTreatmentAdviceId, discontinuationReason);
        uiModel.asMap().clear();
        Patient patient = allPatients.get(treatmentAdvice.getPatientId());
        allTreatmentAdvices.add(treatmentAdvice);
        final CallPreference callPreference = patient.getPatientPreferences().getCallPreference();
        if (callPreference.equals(CallPreference.DailyPillReminder)) {
            TreatmentAdvice oldTreatmentAdvice = allTreatmentAdvices.get(existingTreatmentAdviceId);
            pillReminderService.renew(pillRegimenRequestMapper.map(treatmentAdvice));
            //TODO falling adherence alerts are triggered as a part of adherence trend jobs.
            //TODO Instead of the four calls below we should put all of these in single service
            dailyPillReminderSchedulerService.unscheduleJobForAdherenceTrendFeedbackForDailyPillReminder(oldTreatmentAdvice);
            dailyPillReminderSchedulerService.unscheduleJobForDeterminingAdherenceQualityInDailyPillReminder(patient);
            dailyPillReminderSchedulerService.scheduleJobForAdherenceTrendFeedbackForDailyPillReminder(treatmentAdvice);
            dailyPillReminderSchedulerService.scheduleJobForDeterminingAdherenceQualityInDailyPillReminder(patient, treatmentAdvice);
        } else if (CallPreference.FourDayRecall.equals(callPreference)) {
            fourDayRecallSchedulerService.unScheduleFourDayRecallJobs(patient);
            fourDayRecallSchedulerService.scheduleJobsForFourDayRecall(patient, treatmentAdvice);
        }
        return "redirect:/clinicvisits/" + encodeUrlPathSegment(treatmentAdvice.getId(), httpServletRequest);
    }

    private void endCurrentRegimen(String treatmentAdviceId, String discontinuationReason) {
        TreatmentAdvice existingTreatmentAdvice = allTreatmentAdvices.get(treatmentAdviceId);
        existingTreatmentAdvice.setReasonForDiscontinuing(discontinuationReason);
        existingTreatmentAdvice.endTheRegimen();
        allTreatmentAdvices.update(existingTreatmentAdvice);
    }

    public void createForm(String patientId, Model uiModel) {
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        treatmentAdvice.setPatientId(patientId);
        treatmentAdvice.setDrugCompositionGroupId("");
        populateModel(uiModel, treatmentAdvice);
    }

    public void create(TreatmentAdvice treatmentAdvice, Model uiModel) {
        uiModel.asMap().clear();
        allTreatmentAdvices.add(treatmentAdvice);
        Patient patient = allPatients.get(treatmentAdvice.getPatientId());
        PatientPreferences patientPreferences = patient.getPatientPreferences();
        if (patientPreferences.getCallPreference().equals(CallPreference.FourDayRecall)) {
            fourDayRecallSchedulerService.scheduleJobsForFourDayRecall(patient, treatmentAdvice);
        } else {
            pillReminderService.createNew(pillRegimenRequestMapper.map(treatmentAdvice));
            dailyPillReminderSchedulerService.scheduleJobForAdherenceTrendFeedbackForDailyPillReminder(treatmentAdvice);
            dailyPillReminderSchedulerService.scheduleJobForDeterminingAdherenceQualityInDailyPillReminder(patient, treatmentAdvice);
        }
    }

    public void show(String id, Model uiModel) {
        TreatmentAdviceViewMapper treatmentAdviceViewMapper = new TreatmentAdviceViewMapper(allTreatmentAdvices, allPatients, allRegimens, allDrugs, allDosageTypes, allMealAdviceTypes);
        uiModel.addAttribute("treatmentAdvice", treatmentAdviceViewMapper.map(id));
        uiModel.addAttribute("itemId", id);
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
