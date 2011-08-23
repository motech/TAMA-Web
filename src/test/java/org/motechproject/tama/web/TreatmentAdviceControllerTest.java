package org.motechproject.tama.web;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.builder.RegimenBuilder;
import org.motechproject.tama.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.mapper.PillRegimenRequestMapper;
import org.motechproject.tama.repository.*;
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
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class TreatmentAdviceControllerTest {

    private TreatmentAdviceController controller;

    @Mock
    private Model uiModel;
    @Mock
    private HttpServletRequest request;
    @Mock
    private TreatmentAdvices treatmentAdvices;
    @Mock
    private Patients patients;
    @Mock
    private Regimens regimens;
    @Mock
    private DosageTypes dosageTypes;
    @Mock
    private MealAdviceTypes mealAdviceTypes;
    @Mock
    private PillReminderService pillReminderService;
    @Mock
    private PillRegimenRequestMapper requestMapper;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new TreatmentAdviceController(treatmentAdvices, patients, regimens, null, dosageTypes, mealAdviceTypes, pillReminderService, requestMapper);
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

    @Test
    public void shouldCreateAChangeRegimenForm() {
        String patientId = "patientId";
        String treatmentAdviceId = "treatmentAdviceId";
        TreatmentAdvice treatmentAdviceAttr = new TreatmentAdvice();
        Patient patient = PatientBuilder.startRecording().withPatientId(patientId).build();

        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(new LocalDate(2012, 12, 12));

        when(patients.get(patientId)).thenReturn(patient);
        when(treatmentAdvices.findByPatientId(patientId)).thenReturn(null);
        String returnURL = controller.changeRegimenForm(treatmentAdviceId, patientId, uiModel);

        assertThat(returnURL, is("treatmentadvices/update"));
        verify(uiModel).addAttribute("adviceEndDate", "12/12/2012");
        verify(uiModel).addAttribute("existingTreatmentAdviceId", treatmentAdviceId);
        verify(uiModel).addAttribute("treatmentAdvice", treatmentAdviceAttr);
    }

    @Test
    public void changeRegimenShouldEndCurrentRegimenAndCreateANewRegimen() {
        String existingTreatmentAdviceId = "existingTreatmentAdviceId";
        String treatmentAdviceId = "treatmentAdviceId";
        String discontinuationReason = "bad medicine";
        TreatmentAdvice existingTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withId(existingTreatmentAdviceId).build();
        TreatmentAdvice newTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withId(treatmentAdviceId).build();

        when(treatmentAdvices.get(existingTreatmentAdviceId)).thenReturn(existingTreatmentAdvice);
        String redirectURL = controller.changeRegimen(existingTreatmentAdviceId, discontinuationReason, newTreatmentAdvice, uiModel, request);

        assertThat(redirectURL, is("redirect:/clinicvisits/treatmentAdviceId"));
        assertThat(existingTreatmentAdvice.getReasonForDiscontinuing(), is(discontinuationReason));
        verify(treatmentAdvices).update(existingTreatmentAdvice);
        verify(treatmentAdvices).add(newTreatmentAdvice);
        verify(pillReminderService).renew(any(PillRegimenRequest.class));
    }
}
