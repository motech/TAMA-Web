 package org.motechproject.tama.web;

import org.joda.time.LocalDate;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.builder.WeeklyCronJobExpressionBuilder;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
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
import java.util.Map;

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
    @Qualifier("pillReminderService")
    @Autowired
    private PillReminderService pillReminderService;
    @Autowired
    private MotechSchedulerService schedulerService;
    @Autowired
    private PillRegimenRequestMapper pillRegimenRequestMapper;

    protected TreatmentAdviceController() {
    }

    public TreatmentAdviceController(AllTreatmentAdvices allTreatmentAdvices, AllPatients allPatients, AllRegimens allRegimens, AllDrugs allDrugs, AllDosageTypes allDosageTypes, AllMealAdviceTypes allMealAdviceTypes, PillReminderService pillReminderService, PillRegimenRequestMapper requestMapper) {
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allPatients = allPatients;
        this.allRegimens = allRegimens;
        this.allDrugs = allDrugs;
        this.allDosageTypes = allDosageTypes;
        this.allMealAdviceTypes = allMealAdviceTypes;
        this.pillReminderService = pillReminderService;
        this.pillRegimenRequestMapper = requestMapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ajax/regimens")
    public @ResponseBody List<Regimen> allRegimens() {
        List<Regimen> allRegimens = this.allRegimens.getAll();
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
        allTreatmentAdvices.add(treatmentAdvice);
        pillReminderService.renew(pillRegimenRequestMapper.map(treatmentAdvice));
        unscheduleJobForAdherenceTrendFeedback(existingTreatmentAdviceId);
        scheduleJobForAdherenceTrendFeedback(treatmentAdvice);
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
        pillReminderService.createNew(pillRegimenRequestMapper.map(treatmentAdvice));
        scheduleJobForAdherenceTrendFeedback(treatmentAdvice);
    }

    private void scheduleJobForAdherenceTrendFeedback(TreatmentAdvice treatmentAdvice) {
    	Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(treatmentAdvice.getId())
        .withExternalId(treatmentAdvice.getPatientId())
        .payload();
        LocalDate startDate = DateUtil.newDate(treatmentAdvice.getStartDate()).plusWeeks(5);
        MotechEvent adherenceWeeklyTrendEvent = new MotechEvent(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, eventParams);
        String cronExpression = new WeeklyCronJobExpressionBuilder(startDate.getDayOfWeek()).build();
        CronSchedulableJob adherenceJob = new CronSchedulableJob(adherenceWeeklyTrendEvent, cronExpression, startDate.toDate(), treatmentAdvice.getEndDate());
        schedulerService.scheduleJob(adherenceJob);
    }
    
    private void unscheduleJobForAdherenceTrendFeedback(String oldTreatmentAdviceId) {
    	TreatmentAdvice oldTreatmentAdvice = allTreatmentAdvices.get(oldTreatmentAdviceId);
    	schedulerService.unscheduleJob(oldTreatmentAdvice.getRegimenId());
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
