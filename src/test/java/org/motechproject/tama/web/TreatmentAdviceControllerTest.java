package org.motechproject.tama.web;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.DrugBuilder;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.builder.RegimenBuilder;
import org.motechproject.tama.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.*;
import org.motechproject.tama.web.model.ComboBoxView;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.mockito.Mockito.*;

public class TreatmentAdviceControllerTest {

    private TreatmentAdviceController controller;
    private HttpServletRequest request;
    private Model uiModel;

    private TreatmentAdvices treatmentAdvices;
    private Patients patients;
    private Regimens regimens;
    private Drugs drugs;
    private DosageTypes dosageTypes;
    private MealAdviceTypes mealAdviceTypes;

    @Before
    public void setUp() {
        treatmentAdvices = mock(TreatmentAdvices.class);
        patients = mock(Patients.class);
        regimens = mock(Regimens.class);
        drugs = mock(Drugs.class);
        dosageTypes = mock(DosageTypes.class);
        mealAdviceTypes = mock(MealAdviceTypes.class);

        controller = new TreatmentAdviceController(treatmentAdvices, patients, regimens, drugs, dosageTypes, mealAdviceTypes);
        request = mock(HttpServletRequest.class);
        uiModel = mock(Model.class);
    }

    @Test
    public void shouldCreateNewTreatmentAdviceFormGivenAPatientWithNoTreatmentAdvice() {
        String patientId = "patientId";
        TreatmentAdvice treatmentAdviceAttr = new TreatmentAdvice();
        Patient patient = PatientBuilder.startRecording().withPatientId("patientId").build();

        when(patients.get("patientId")).thenReturn(patient);
        when(treatmentAdvices.findByPatientId(patientId)).thenReturn(null);
        String redirectURL = controller.createForm(patientId, uiModel, request);

        junit.framework.Assert.assertEquals("treatmentadvices/create", redirectURL);
        verify(uiModel).addAttribute("treatmentAdvice", treatmentAdviceAttr);
    }

    @Test
    public void shouldRedirectToShowTreatmentAdvice_WhenPatientHasATreatmentAdvice() {
        String patientId = "patientId";
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(treatmentAdvices.findByPatientId(patientId)).thenReturn(treatmentAdvice);
        String redirectURL = controller.createForm(patientId, uiModel, request);

        junit.framework.Assert.assertEquals("redirect:/treatmentadvices/treatmentAdviceId", redirectURL);
    }

    @Test
    public void shouldCreateNewTreatmentAdvice() {
        BindingResult bindingResult = mock(BindingResult.class);
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId("patientId");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(uiModel.asMap()).thenReturn(new HashMap<String, Object>());

        String redirectURL = controller.create(treatmentAdvice, bindingResult, uiModel, request);

        junit.framework.Assert.assertEquals("redirect:/patients/patientId", redirectURL);
        verify(treatmentAdvices).add(treatmentAdvice);
    }

    @Test
    public void shouldGetRegimenCompositionsForARegimen() {
        String regimenId = "patientId";
        Regimen regimen = RegimenBuilder.startRecording().withDefaults().build();
        HashSet<String> drugIds = new HashSet<String>();
        drugIds.add("drugId1");
        drugIds.add("drugId2");

        List<Drug> returnedDrugs = new ArrayList<Drug>();
        returnedDrugs.add(DrugBuilder.startRecording().withId("drugId1").withName("Drug1").build());
        returnedDrugs.add(DrugBuilder.startRecording().withId("drugId2").withName("Drug2").build());

        when(regimens.get(regimenId)).thenReturn(regimen);
        when(drugs.getDrugs(drugIds)).thenReturn(returnedDrugs);

        Set<ComboBoxView> regimenCompositions = controller.regimenCompositionsFor(regimenId);
        ComboBoxView regimenComposition = (ComboBoxView) CollectionUtils.get(regimenCompositions, 0);

        junit.framework.Assert.assertEquals(1, regimenCompositions.size());
        junit.framework.Assert.assertEquals("regimenCompositionId", regimenComposition.getId());
        junit.framework.Assert.assertEquals("drugDisplayName", regimenComposition.getDisplayName());
    }

    @Test
    public void shouldGetDrugDosagesForARegimenComposition() {
        String regimenId = "patientId";
        String regimenCompositionId = "regimenCompositionId";
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();

        Regimen regimen = RegimenBuilder.startRecording().withDefaults().build();
        HashSet<String> drugIds = new HashSet<String>();
        drugIds.add("drugId1");
        drugIds.add("drugId2");

        List<Drug> returnedDrugs = new ArrayList<Drug>();
        returnedDrugs.add(DrugBuilder.startRecording().withId("drugId1").withName("Drug1").build());
        returnedDrugs.add(DrugBuilder.startRecording().withId("drugId2").withName("Drug2").build());

        when(regimens.get(regimenId)).thenReturn(regimen);
        when(drugs.getDrugs(drugIds)).thenReturn(returnedDrugs);

        String redirectUrl = controller.drugDosagesFor(regimenId, regimenCompositionId, treatmentAdvice);

        junit.framework.Assert.assertEquals("treatmentadvices/drugdosages", redirectUrl);
        junit.framework.Assert.assertEquals(2, treatmentAdvice.getDrugDosages().size());
        junit.framework.Assert.assertEquals("drugId1", treatmentAdvice.getDrugDosages().get(0).getDrugId());
        junit.framework.Assert.assertEquals("drugId2", treatmentAdvice.getDrugDosages().get(1).getDrugId());
    }

    @Test
    public void shouldGetAllRegimens() {
        List<Regimen> returnedRegimens = new ArrayList<Regimen>();
        returnedRegimens.add(RegimenBuilder.startRecording().withDefaults().build());
        when(regimens.getAll()).thenReturn(returnedRegimens);

        List<ComboBoxView> viewRegimens = controller.regimens();
        junit.framework.Assert.assertEquals(1, viewRegimens.size());
        junit.framework.Assert.assertEquals("regimenId", viewRegimens.get(0).getId());
        junit.framework.Assert.assertEquals("regimenName", viewRegimens.get(0).getDisplayName());
    }

    @Test
    public void shouldGetAllRegimenCompositions() {
        List<String> viewRegimenCompositions = controller.regimenCompositions();
        junit.framework.Assert.assertEquals(1, viewRegimenCompositions.size());
    }

    @Test
    public void shouldGetAllMealAdviceTypes() {
        List<MealAdviceType> allMealAdviceTypes = new ArrayList<MealAdviceType>();
        allMealAdviceTypes.add(new MealAdviceType("Before Meal"));

        when(mealAdviceTypes.getAll()).thenReturn(allMealAdviceTypes);

        List<MealAdviceType> returnedMealAdviceTypes = controller.mealAdviceTypes();
        junit.framework.Assert.assertEquals(1, returnedMealAdviceTypes.size());
        junit.framework.Assert.assertEquals("Before Meal", returnedMealAdviceTypes.get(0).getType());
    }

    @Test
    public void shouldGetAllDosageTypes() {
        List<DosageType> allDosageTypes = new ArrayList<DosageType>();
        allDosageTypes.add(new DosageType("Once Daily"));

        when(dosageTypes.getAll()).thenReturn(allDosageTypes);

        List<DosageType> returnedDosageTypes = controller.dosageTypes();
        junit.framework.Assert.assertEquals(1, returnedDosageTypes.size());
        junit.framework.Assert.assertEquals("Once Daily", returnedDosageTypes.get(0).getType());
    }
}
