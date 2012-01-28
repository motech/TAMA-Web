package org.motechproject.tama.web;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.ClinicVisitService;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.tama.refdata.builder.RegimenBuilder;
import org.motechproject.tama.refdata.domain.DosageType;
import org.motechproject.tama.refdata.domain.MealAdviceType;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllDosageTypes;
import org.motechproject.tama.refdata.repository.AllMealAdviceTypes;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.web.mapper.TreatmentAdviceViewMapper;
import org.motechproject.tama.web.model.ComboBoxView;
import org.motechproject.tama.web.model.TreatmentAdviceView;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
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
    private TreatmentAdviceService treatmentAdviceService;
    @Mock
    private TreatmentAdviceViewMapper treatmentAdviceViewMapper;
    @Mock
    private ClinicVisitService clinicVisitService;

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

        controller = new TreatmentAdviceController(allPatients, allRegimens, allDosageTypes, allMealAdviceTypes, treatmentAdviceService, treatmentAdviceViewMapper, clinicVisitService);
    }

    @Test
    public void shouldCreateNewTreatmentAdviceFormGivenAPatientWithNoTreatmentAdvice() {
        TreatmentAdvice treatmentAdviceAttr = TreatmentAdvice.newDefault();
        treatmentAdviceAttr.setPatientId(PATIENT_ID);
        treatmentAdviceAttr.setDrugCompositionGroupId("");

        Patient patient = PatientBuilder.startRecording().withId(PATIENT_ID).build();

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(PATIENT_ID)).thenReturn(null);
        controller.createForm(PATIENT_ID, uiModel);

        verify(uiModel).addAttribute("treatmentAdvice", treatmentAdviceAttr);
    }

    @Test
    public void shouldCreateNewTreatmentAdvice() {
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(uiModel.asMap()).thenReturn(new HashMap<String, Object>());

        controller.create(bindingResult, uiModel, treatmentAdvice);

        verify(treatmentAdviceService).createRegimen(treatmentAdvice);
    }

    @Test
    public void shouldShowTreatmentAdvice(){
        final String treatmentAdviceId = "treatmentAdviceId";
        final TreatmentAdviceView treatmentAdviceView = new TreatmentAdviceView();
        when(treatmentAdviceViewMapper.map(treatmentAdviceId)).thenReturn(treatmentAdviceView);
        uiModel = new ExtendedModelMap();

        controller.show(treatmentAdviceId, uiModel);

        assertEquals(treatmentAdviceView, uiModel.asMap().get("treatmentAdvice"));
        assertEquals(treatmentAdviceId, uiModel.asMap().get("itemId"));
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
        String patientId = PATIENT_ID;
        String treatmentAdviceId = "treatmentAdviceId";
        String clinicVisitId = "clinicVisitId";
        TreatmentAdvice treatmentAdviceAttr = TreatmentAdvice.newDefault();
        treatmentAdviceAttr.setPatientId(PATIENT_ID);
        treatmentAdviceAttr.setDrugCompositionGroupId("");
        Patient patient = PatientBuilder.startRecording().withPatientId(patientId).build();

        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(new LocalDate(2012, 12, 12));

        when(allPatients.get(patientId)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(null);
        String returnURL = controller.changeRegimenForm(treatmentAdviceId, patientId, clinicVisitId, uiModel);

        assertThat(returnURL, is("treatmentadvices/update"));
        verify(uiModel).addAttribute("adviceEndDate", "12/12/2012");
        verify(uiModel).addAttribute("existingTreatmentAdviceId", treatmentAdviceId);
        verify(uiModel).addAttribute("treatmentAdvice", treatmentAdviceAttr);
        verify(uiModel).addAttribute("clinicVisitId", clinicVisitId);
    }

    @Test
    public void changeRegimenShouldEndCurrentRegimenAndCreateANewRegimen_WhenPatientIsOnDailyPillReminderCalls() {
        String existingTreatmentAdviceId = "existingTreatmentAdviceId";
        String discontinuationReason = "bad medicine";
        String regimenId = "existingTreatmentRegimenId";
        String clinicVisitId = "clinicVisitId";
        TreatmentAdvice existingTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withId(existingTreatmentAdviceId).withRegimenId(regimenId).build();

        when(allTreatmentAdvices.get(existingTreatmentAdviceId)).thenReturn(existingTreatmentAdvice);
        when(treatmentAdviceService.changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice)).thenReturn(treatmentAdvice.getId());
        String redirectURL = controller.changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice, clinicVisitId, uiModel, request);

        assertThat(redirectURL, is("redirect:/clinicvisits/" + PATIENT_ID));
        verify(treatmentAdviceService).changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice);
        verify(clinicVisitService).changeRegimen(clinicVisitId, treatmentAdvice.getId());
    }

    @Test
    public void changeRegimenShouldEndCurrentRegimenAndCreateANewRegimen_WhenPatientIsOnFourDayRecallCalls() {
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        String existingTreatmentAdviceId = "existingTreatmentAdviceId";
        String discontinuationReason = "bad medicine";
        String regimenId = "existingTreatmentRegimenId";
        String clinicVisitId = "clinicVisitId";
        TreatmentAdvice existingTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withId(existingTreatmentAdviceId).withRegimenId(regimenId).build();

        when(allTreatmentAdvices.get(existingTreatmentAdviceId)).thenReturn(existingTreatmentAdvice);
        when(treatmentAdviceService.changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice)).thenReturn(treatmentAdvice.getId());
        String redirectURL = controller.changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice, clinicVisitId, uiModel, request);

        assertThat(redirectURL, is("redirect:/clinicvisits/" + PATIENT_ID));
        verify(treatmentAdviceService).changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice);
        verify(clinicVisitService).changeRegimen(clinicVisitId, treatmentAdvice.getId());
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
