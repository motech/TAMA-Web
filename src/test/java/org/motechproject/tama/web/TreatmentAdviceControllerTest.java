package org.motechproject.tama.web;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import junitx.util.PrivateAccessor;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.pillreminder.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.builder.RegimenBuilder;
import org.motechproject.tama.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.domain.DosageType;
import org.motechproject.tama.domain.DrugDosage;
import org.motechproject.tama.domain.MealAdviceType;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.mapper.PillRegimenRequestMapper;
import org.motechproject.tama.repository.AllDosageTypes;
import org.motechproject.tama.repository.AllMealAdviceTypes;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllRegimens;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.web.model.ComboBoxView;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class TreatmentAdviceControllerTest {

    private TreatmentAdviceController controller;

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
    private MotechSchedulerService motechSchedulerService;

    @Before
    public void setUp() throws Exception{
        initMocks(this);
        controller = new TreatmentAdviceController(allTreatmentAdvices, allPatients, allRegimens, null, allDosageTypes, allMealAdviceTypes, pillReminderService, requestMapper);
        PrivateAccessor.setField(controller, "schedulerService", motechSchedulerService);
        
    }

    @Test
    public void shouldCreatePillRegimenRequest() {
        TreatmentAdvice treatmentAdvice = getTreatmentAdvice();

        controller.create(treatmentAdvice, uiModel);
        verify(requestMapper).map(treatmentAdvice);
        verify(pillReminderService).createNew(any(DailyPillRegimenRequest.class));
    }

    @Test
    public void shouldCreateNewTreatmentAdviceFormGivenAPatientWithNoTreatmentAdvice() {
        String patientId = "patientId";
        TreatmentAdvice treatmentAdviceAttr = TreatmentAdvice.newDefault();
        treatmentAdviceAttr.setPatientId("patientId");
        treatmentAdviceAttr.setDrugCompositionGroupId("");

        Patient patient = PatientBuilder.startRecording().withPatientId(patientId).build();

        when(allPatients.get("patientId")).thenReturn(patient);
        when(allTreatmentAdvices.findByPatientId(patientId)).thenReturn(null);
        controller.createForm(patientId, uiModel);

        verify(uiModel).addAttribute("treatmentAdvice", treatmentAdviceAttr);
    }

    @Test
    public void shouldCreateNewTreatmentAdvice() {
        BindingResult bindingResult = mock(BindingResult.class);
        TreatmentAdvice treatmentAdvice = getTreatmentAdvice();

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
        String patientId = "patientId";
        String treatmentAdviceId = "treatmentAdviceId";
        TreatmentAdvice treatmentAdviceAttr = TreatmentAdvice.newDefault();
        treatmentAdviceAttr.setPatientId("patientId");
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
        TreatmentAdvice newTreatmentAdvice = getTreatmentAdvice();

        when(allTreatmentAdvices.get(existingTreatmentAdviceId)).thenReturn(existingTreatmentAdvice);
        String redirectURL = controller.changeRegimen(existingTreatmentAdviceId, discontinuationReason, newTreatmentAdvice, uiModel, request);

        assertThat(redirectURL, is("redirect:/clinicvisits/treatmentAdviceId"));
        assertThat(existingTreatmentAdvice.getReasonForDiscontinuing(), is(discontinuationReason));
        verify(allTreatmentAdvices).update(existingTreatmentAdvice);
        verify(allTreatmentAdvices).add(newTreatmentAdvice);
        verify(pillReminderService).renew(any(DailyPillRegimenRequest.class));
        verify(motechSchedulerService).unscheduleJob(regimenId);
        ArgumentCaptor<CronSchedulableJob> jobCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).scheduleJob(jobCaptor.capture());
        assertEquals("0 0 0 ? * 4", jobCaptor.getValue().getCronExpression());
    }
    

    @Test
    public void shouldCreateNewTreatmentAdviceAlongWithWeeklyAdherenceTrendJob() throws Exception{
        BindingResult bindingResult = mock(BindingResult.class);
        TreatmentAdvice treatmentAdvice = getTreatmentAdvice();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(uiModel.asMap()).thenReturn(new HashMap<String, Object>());

        controller.create(treatmentAdvice, uiModel);
        verify(allTreatmentAdvices).add(treatmentAdvice);
        ArgumentCaptor<CronSchedulableJob> jobCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).scheduleJob(jobCaptor.capture());
        assertEquals("0 0 0 ? * 4", jobCaptor.getValue().getCronExpression());
    }

	private TreatmentAdvice getTreatmentAdvice() {
		TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
		treatmentAdvice.setId("treatmentAdviceId");
        treatmentAdvice.setPatientId("patientId");
        ArrayList<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
        DrugDosage drugDosage = new DrugDosage();
        treatmentAdvice.setDrugCompositionGroupId("");
        drugDosage.setStartDate(DateUtil.newDate(2012, 12, 12));
		drugDosages.add(drugDosage);
		treatmentAdvice.setDrugDosages(drugDosages);
		return treatmentAdvice;
	}
}
