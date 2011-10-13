package org.motechproject.tama.web;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.server.pillreminder.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.builder.RegimenBuilder;
import org.motechproject.tama.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.mapper.PillRegimenRequestMapper;
import org.motechproject.tama.repository.*;
import org.motechproject.tama.service.TamaSchedulerService;
import org.motechproject.tama.web.model.ComboBoxView;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class TreatmentAdviceControllerTest {
    @Mock
    private Model uiModel;
    @Mock
    private HttpServletRequest request;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllRegimens allRegimens;
    @Mock
    private AllDosageTypes allDosageTypes;
    @Mock
    private AllMealAdviceTypes allMealAdviceTypes;
    @Mock
    private PillReminderService pillReminderService;
    @Mock
    private PillRegimenRequestMapper requestMapper;
    @Mock
    private TamaSchedulerService schedulerService;

    private TreatmentAdviceController controller;
    private TreatmentAdvice treatmentAdvice;
    private static String PATIENT_ID = "patientId";
    private Patient patient;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        treatmentAdvice = getTreatmentAdvice();

        patient = new Patient();
        patient.getPatientPreferences().setCallPreference(CallPreference.DailyPillReminder);
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        controller = new TreatmentAdviceController(allTreatmentAdvices, allPatients, allRegimens, null, allDosageTypes, allMealAdviceTypes, pillReminderService, requestMapper, schedulerService);
    }

    @Test
    public void shouldScheduleCallsForPatientsOnDailyPillReminder() {
        controller.create(treatmentAdvice, uiModel);
        verify(pillReminderService).createNew(any(DailyPillRegimenRequest.class));
        verify(schedulerService).scheduleJobForAdherenceTrendFeedback(treatmentAdvice);
    }

    @Test
    public void shouldScheduleCallsForPatientsOnWeeklyAdherence() {
        DayOfWeek dayOfWeek = DayOfWeek.Friday;
        TimeOfDay bestCallTime = new TimeOfDay(10, 30, TimeMeridiem.AM);

        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        patient.getPatientPreferences().setBestCallTime(bestCallTime);
        patient.getPatientPreferences().setDayOfWeeklyCall(dayOfWeek);
        
        controller.create(treatmentAdvice, uiModel);
        verify(schedulerService).scheduleJobsForFourDayRecall(patient, treatmentAdvice);
    }

    @Test
    public void shouldCreateNewTreatmentAdviceFormGivenAPatientWithNoTreatmentAdvice() {
        String patientId = this.PATIENT_ID;
        TreatmentAdvice treatmentAdviceAttr = TreatmentAdvice.newDefault();
        treatmentAdviceAttr.setPatientId(this.PATIENT_ID);
        treatmentAdviceAttr.setDrugCompositionGroupId("");

        Patient patient = PatientBuilder.startRecording().withPatientId(patientId).build();

        when(allPatients.get(this.PATIENT_ID)).thenReturn(patient);
        when(allTreatmentAdvices.findByPatientId(patientId)).thenReturn(null);
        controller.createForm(patientId, uiModel);

        verify(uiModel).addAttribute("treatmentAdvice", treatmentAdviceAttr);
    }

    @Test
    public void shouldCreateNewTreatmentAdvice() {
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(uiModel.asMap()).thenReturn(new HashMap<String, Object>());

        controller.create(treatmentAdvice, uiModel);

        verify(allTreatmentAdvices).add(treatmentAdvice);
    }

    @Test
    public void shouldGetAllRegimens() {
        List<Regimen> returnedRegimens = new ArrayList<Regimen>();
        returnedRegimens.add(RegimenBuilder.startRecording().withDefaults().build());
        when(allRegimens.getAll()).thenReturn(returnedRegimens);

        List<ComboBoxView> viewRegimens = controller.regimens();
        junit.framework.Assert.assertEquals(1, viewRegimens.size());
        junit.framework.Assert.assertEquals("regimenId", viewRegimens.get(0).getId());
        junit.framework.Assert.assertEquals("regimenName", viewRegimens.get(0).getDisplayName());
    }

    @Test
    public void shouldGetAllRegimenCompositions() {
        List<String> viewRegimenCompositions = controller.drugCompositions();
        junit.framework.Assert.assertEquals(1, viewRegimenCompositions.size());
    }

    @Test
    public void shouldGetAllMealAdviceTypes() {
        List<MealAdviceType> allMealAdviceTypes = new ArrayList<MealAdviceType>();
        allMealAdviceTypes.add(new MealAdviceType("Before Meal"));

        when(this.allMealAdviceTypes.getAll()).thenReturn(allMealAdviceTypes);

        List<MealAdviceType> returnedMealAdviceTypes = controller.mealAdviceTypes();
        junit.framework.Assert.assertEquals(1, returnedMealAdviceTypes.size());
        junit.framework.Assert.assertEquals("Before Meal", returnedMealAdviceTypes.get(0).getType());
    }

    @Test
    public void shouldGetAllDosageTypes() {
        List<DosageType> allDosageTypes = new ArrayList<DosageType>();
        allDosageTypes.add(new DosageType("Once Daily"));

        when(this.allDosageTypes.getAll()).thenReturn(allDosageTypes);

        List<DosageType> returnedDosageTypes = controller.dosageTypes();
        junit.framework.Assert.assertEquals(1, returnedDosageTypes.size());
        junit.framework.Assert.assertEquals("Once Daily", returnedDosageTypes.get(0).getType());
    }

    @Test
    public void shouldCreateAChangeRegimenForm() {
        String patientId = this.PATIENT_ID;
        String treatmentAdviceId = "treatmentAdviceId";
        TreatmentAdvice treatmentAdviceAttr = TreatmentAdvice.newDefault();
        treatmentAdviceAttr.setPatientId(this.PATIENT_ID);
        treatmentAdviceAttr.setDrugCompositionGroupId("");
        Patient patient = PatientBuilder.startRecording().withPatientId(patientId).build();

        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(new LocalDate(2012, 12, 12));

        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.findByPatientId(patientId)).thenReturn(null);
        String returnURL = controller.changeRegimenForm(treatmentAdviceId, patientId, uiModel);

        assertThat(returnURL, is("treatmentadvices/update"));
        verify(uiModel).addAttribute("adviceEndDate", "12/12/2012");
        verify(uiModel).addAttribute("existingTreatmentAdviceId", treatmentAdviceId);
        verify(uiModel).addAttribute("treatmentAdvice", treatmentAdviceAttr);
    }

    @Test
    public void changeRegimenShouldEndCurrentRegimenAndCreateANewRegimen() {
        String existingTreatmentAdviceId = "existingTreatmentAdviceId";
        String discontinuationReason = "bad medicine";
        String regimenId = "existingTreatmentRegimenId";
        TreatmentAdvice existingTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withId(existingTreatmentAdviceId).withRegimenId(regimenId).build();

        when(allTreatmentAdvices.get(existingTreatmentAdviceId)).thenReturn(existingTreatmentAdvice);
        String redirectURL = controller.changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice, uiModel, request);

        assertThat(redirectURL, is("redirect:/clinicvisits/treatmentAdviceId"));
        assertThat(existingTreatmentAdvice.getReasonForDiscontinuing(), is(discontinuationReason));
        verify(allTreatmentAdvices).update(existingTreatmentAdvice);
        verify(allTreatmentAdvices).add(treatmentAdvice);
        verify(pillReminderService).renew(any(DailyPillRegimenRequest.class));
        verify(schedulerService).unscheduleJobForAdherenceTrendFeedback(existingTreatmentAdvice);
        verify(schedulerService).scheduleJobForAdherenceTrendFeedback(treatmentAdvice);
    }

    private TreatmentAdvice getTreatmentAdvice() {
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        treatmentAdvice.setId("treatmentAdviceId");
        treatmentAdvice.setPatientId(PATIENT_ID);
        ArrayList<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
        DrugDosage drugDosage = new DrugDosage();
        treatmentAdvice.setDrugCompositionGroupId("");
        drugDosage.setStartDate(DateUtil.newDate(2012, 12, 12));
        drugDosages.add(drugDosage);
        treatmentAdvice.setDrugDosages(drugDosages);
        return treatmentAdvice;
    }
}
