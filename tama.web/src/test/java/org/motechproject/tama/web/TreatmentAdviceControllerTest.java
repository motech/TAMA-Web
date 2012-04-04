package org.motechproject.tama.web;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.CallTimeSlotService;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.tama.refdata.builder.RegimenBuilder;
import org.motechproject.tama.refdata.domain.DosageType;
import org.motechproject.tama.refdata.domain.MealAdviceType;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.objectcache.AllDosageTypesCache;
import org.motechproject.tama.refdata.objectcache.AllMealAdviceTypesCache;
import org.motechproject.tama.refdata.objectcache.AllRegimensCache;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.web.mapper.TreatmentAdviceViewMapper;
import org.motechproject.tama.web.model.ComboBoxView;
import org.motechproject.tama.web.model.TreatmentAdviceView;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.when;

public class TreatmentAdviceControllerTest extends BaseUnitTest {
    public static final String USER_NAME = "userName";
    @Mock
    private Model uiModel;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllRegimensCache allRegimens;
    @Mock
    private AllDosageTypesCache allDosageTypes;
    @Mock
    private AllMealAdviceTypesCache allMealAdviceTypes;
    @Mock
    private TreatmentAdviceService treatmentAdviceService;
    @Mock
    private TreatmentAdviceViewMapper treatmentAdviceViewMapper;
    @Mock
    private AllClinicVisits allClinicVisits;
    @Mock
    private CallTimeSlotService dosageTimeSlotService;
    @Mock
    AuthenticatedUser user;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpSession session;

    private TreatmentAdviceController controller;
    private TreatmentAdvice treatmentAdvice;
    private static String PATIENT_ID = "patientId";

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        treatmentAdvice = getTreatmentAdvice();
        Patient patient = new Patient();
        patient.getPatientPreferences().setCallPreference(CallPreference.DailyPillReminder);
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(user.getUsername()).thenReturn(USER_NAME);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
        when(request.getSession()).thenReturn(session);
        controller = new TreatmentAdviceController(allPatients, allRegimens, allDosageTypes, allMealAdviceTypes, treatmentAdviceService, treatmentAdviceViewMapper, allClinicVisits, dosageTimeSlotService);
    }

    @Test
    public void shouldCreateNewTreatmentAdviceFormGivenAPatientWithNoTreatmentAdvice() {
        TreatmentAdvice treatmentAdviceAttr = TreatmentAdvice.newDefault();
        treatmentAdviceAttr.setPatientId(PATIENT_ID);
        treatmentAdviceAttr.setDrugCompositionGroupId("");

        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build();

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(PATIENT_ID)).thenReturn(null);
        final ArrayList<String> morningTimeSlots = new ArrayList<String>() {{
            add("10:00");
        }};
        final ArrayList<String> eveningTimeSlots = new ArrayList<String>() {{
            add("09:00");
        }};
        when(dosageTimeSlotService.availableMorningSlots()).thenReturn(morningTimeSlots);
        when(dosageTimeSlotService.availableEveningSlots()).thenReturn(eveningTimeSlots);
        controller.createForm(PATIENT_ID, uiModel);

        verify(uiModel).addAttribute("treatmentAdvice", treatmentAdviceAttr);
        verify(uiModel).addAttribute("patientIdentifier", patient.getPatientId());
        verify(uiModel).addAttribute("callPlan", patient.getPatientPreferences().getCallPreference());
        verify(uiModel).addAttribute("morningTimeSlots", morningTimeSlots);
        verify(uiModel).addAttribute("eveningTimeSlots", eveningTimeSlots);
    }

    @Test
    public void shouldCreateNewTreatmentAdvice() {
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(uiModel.asMap()).thenReturn(new HashMap<String, Object>());

        controller.create(bindingResult, uiModel, treatmentAdvice, USER_NAME);

        verify(treatmentAdviceService).createRegimen(treatmentAdvice, USER_NAME);
    }

    @Test
    public void shouldShowTreatmentAdvice() {
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
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2012, 12, 12), 10, 0, 0));
        String treatmentAdviceId = "treatmentAdviceId";
        String clinicVisitId = "clinicVisitId";
        Patient patient = PatientBuilder.startRecording().withPatientId(PATIENT_ID).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(allTreatmentAdvices.currentTreatmentAdvice(PATIENT_ID)).thenReturn(null);

        String returnURL = controller.changeRegimenForm(treatmentAdviceId, PATIENT_ID, clinicVisitId, uiModel);

        assertThat(returnURL, is("treatmentadvices/update"));
        verify(uiModel).addAttribute("clinicVisitId", clinicVisitId);
        verify(uiModel).addAttribute("adviceEndDate", "12/12/2012");
        verify(uiModel).addAttribute("existingTreatmentAdviceId", treatmentAdviceId);
    }

    @Test
    public void shouldEndCurrentRegimenAndCreateANewRegimen_AndRedirectsToCreateClinicVisitUrl() {
        String existingTreatmentAdviceId = "existingTreatmentAdviceId";
        String discontinuationReason = "bad medicine";
        String clinicVisitId = "clinicVisitId";
        when(treatmentAdviceService.changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice, USER_NAME)).thenReturn(treatmentAdvice.getId());

        String redirectURL = controller.changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice, clinicVisitId, uiModel, request);

        assertThat(redirectURL, is("redirect:/clinicvisits?form&patientId=" + PATIENT_ID + "&clinicVisitId=" + clinicVisitId));
        verify(treatmentAdviceService).changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice, USER_NAME);
        verify(allClinicVisits).changeRegimen(PATIENT_ID, clinicVisitId, treatmentAdvice.getId());
    }

    @Test
    public void shouldRedirectToChangeRegimenForm_whenChangeRegimenErrorsOut() {
        String existingTreatmentAdviceId = "existingTreatmentAdviceId";
        String discontinuationReason = "bad medicine";
        String clinicVisitId = "clinicVisitId";
        doThrow(new RuntimeException("Some error")).when(treatmentAdviceService).changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice, USER_NAME);

        String redirectURL = controller.changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice, clinicVisitId, uiModel, request);

        assertThat(redirectURL, is("redirect:/treatmentadvices/changeRegimen?id=" + existingTreatmentAdviceId + "&clinicVisitId=" + clinicVisitId + "&patientId=" + PATIENT_ID));
    }

    @Test
    public void shouldRedirectToChangeRegimenFormWithNewTreatmentAdviceId_whenClinicVisitChangeRegimenErrorsOut() {
        String existingTreatmentAdviceId = "existingTreatmentAdviceId";
        String discontinuationReason = "bad medicine";
        String clinicVisitId = "clinicVisitId";
        String newTreatmentAdviceId = "newTreatmentAdviceId";
        when(treatmentAdviceService.changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice, USER_NAME)).thenReturn(newTreatmentAdviceId);
        doThrow(new RuntimeException("Some error")).when(allClinicVisits).changeRegimen(PATIENT_ID, clinicVisitId, newTreatmentAdviceId);

        String redirectURL = controller.changeRegimen(existingTreatmentAdviceId, discontinuationReason, treatmentAdvice, clinicVisitId, uiModel, request);

        assertThat(redirectURL, is("redirect:/treatmentadvices/changeRegimen?id=" + newTreatmentAdviceId + "&clinicVisitId=" + clinicVisitId + "&patientId=" + PATIENT_ID));
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
