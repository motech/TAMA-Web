package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.builder.RegimenBuilder;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.mapper.PillRegimenRequestMapper;
import org.motechproject.tama.repository.*;
import org.motechproject.tama.web.model.ComboBoxView;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;

public class TreatmentAdviceControllerTest {

    private TreatmentAdviceController controller;
    private Model uiModel;

    private TreatmentAdvices treatmentAdvices;
    private Patients patients;
    private Regimens regimens;
    private DosageTypes dosageTypes;
    private MealAdviceTypes mealAdviceTypes;
    private PillReminderService pillReminderService;
    private PillRegimenRequestMapper requestMapper;

    @Before
    public void setUp() {
        treatmentAdvices = mock(TreatmentAdvices.class);
        patients = mock(Patients.class);
        regimens = mock(Regimens.class);
        dosageTypes = mock(DosageTypes.class);
        mealAdviceTypes = mock(MealAdviceTypes.class);
        pillReminderService = mock(PillReminderService.class);
        requestMapper = mock(PillRegimenRequestMapper.class);

        controller = new TreatmentAdviceController(treatmentAdvices, patients, regimens, null, dosageTypes, mealAdviceTypes, pillReminderService, requestMapper);

        uiModel = mock(Model.class);
    }

    @Test
    public void shouldCreatePillRegimenRequest() {
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId("1234");

        controller.create(treatmentAdvice, uiModel);
        verify(requestMapper).map(treatmentAdvice);
        verify(pillReminderService).createNew(any(PillRegimenRequest.class));
    }

    @Test
    public void shouldCreateNewTreatmentAdviceFormGivenAPatientWithNoTreatmentAdvice() {
        String patientId = "patientId";
        TreatmentAdvice treatmentAdviceAttr = new TreatmentAdvice();
        Patient patient = PatientBuilder.startRecording().withPatientId("patientId").build();

        when(patients.get("patientId")).thenReturn(patient);
        when(treatmentAdvices.findByPatientId(patientId)).thenReturn(null);
        controller.createForm(patientId, uiModel);

        verify(uiModel).addAttribute("treatmentAdvice", treatmentAdviceAttr);
    }

    @Test
    public void shouldCreateNewTreatmentAdvice() {
        BindingResult bindingResult = mock(BindingResult.class);
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId("patientId");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(uiModel.asMap()).thenReturn(new HashMap<String, Object>());

        controller.create(treatmentAdvice, uiModel);

        verify(treatmentAdvices).add(treatmentAdvice);
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
        List<String> viewRegimenCompositions = controller.drugCompositions();
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
