package org.motechproject.tama.web;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.DrugBuilder;
import org.motechproject.tama.builder.RegimenBuilder;
import org.motechproject.tama.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.*;
import org.motechproject.tama.web.mapper.TreatmentAdviceViewMapper;
import org.motechproject.tama.web.model.ComboBoxView;
import org.motechproject.tama.web.model.TreatmentAdviceView;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.mockito.Mockito.*;

public class TreatmentAdviceControllerTest {

    private TreatmentAdviceController controller;
    private HttpServletRequest request;
    private Model uiModel;

    private MealAdviceTypes mealAdviceTypes;
    private DosageTypes dosageTypes;
    private Drugs drugs;
    private Regimens regimens;
    private TreatmentAdvices treatmentAdvices;
    private TreatmentAdviceViewMapper treatmentAdviceViewMapper;

    @Before
    public void setUp() {
        mealAdviceTypes = mock(MealAdviceTypes.class);
        dosageTypes = mock(DosageTypes.class);
        drugs = mock(Drugs.class);
        regimens = mock(Regimens.class);
        treatmentAdvices = mock(TreatmentAdvices.class);
        treatmentAdviceViewMapper = mock(TreatmentAdviceViewMapper.class);

        controller = new TreatmentAdviceController(mealAdviceTypes, dosageTypes, drugs, regimens, treatmentAdvices, treatmentAdviceViewMapper);
        request = mock(HttpServletRequest.class);
        uiModel = mock(Model.class);
    }

    @Test
    public void shouldCreateNewTreatmentAdviceFormGivenAPatientWithNoTreatmentAdvice() {
        String patientId = "patientId";
        TreatmentAdvice treatmentAdviceAttr = new TreatmentAdvice();

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
    public void shouldShowTreatmentAdvice() {
        TreatmentAdviceView treatmentAdviceView = new TreatmentAdviceView();

        when(treatmentAdviceViewMapper.map("treatmentAdviceId")).thenReturn(treatmentAdviceView);
        String url = controller.show("treatmentAdviceId", uiModel);

        junit.framework.Assert.assertEquals("treatmentadvices/show", url);
        verify(uiModel).addAttribute("treatmentAdvice", treatmentAdviceView);
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
        junit.framework.Assert.assertEquals("Drug1 / Drug2", regimenComposition.getDisplayName());
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
