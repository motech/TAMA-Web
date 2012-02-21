package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.web.model.LabResultsUIModel;
import org.motechproject.util.DateUtil;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicVisitsControllerTest {

    public static final String PATIENT_ID = "patientId";
    public static final String VISIT_ID = "clinicVisitId";
    @Mock
    private TreatmentAdviceController treatmentAdviceController;
    @Mock
    private LabResultsController labResultsController;
    @Mock
    private VitalStatisticsController vitalStatisticsController;
    @Mock
    private HttpServletRequest request;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllClinicVisits allClinicVisits;

    private Model uiModel;
    private ClinicVisitsController clinicVisitsController;

    @Before
    public void setUp() {
        initMocks(this);
        uiModel = new ExtendedModelMap();
        clinicVisitsController = new ClinicVisitsController(treatmentAdviceController, allTreatmentAdvices, labResultsController, vitalStatisticsController, allClinicVisits);
    }

    @Test
    public void shouldRedirectToShowClinicVisits_WhenPatientHasATreatmentAdvice() {
        String patientId = "patientId";
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").build();
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().build();
        when(allClinicVisits.get(anyString(), anyString())).thenReturn(clinicVisit);
        when(allTreatmentAdvices.get(anyString())).thenReturn(treatmentAdvice);
        String redirectURL = clinicVisitsController.createForm(patientId, "", uiModel, request);

        assertEquals("clinicvisits/create" , redirectURL);
    }

    @Test
    public void shouldRedirectToCreateClinicVisits_WhenPatientDoesNotHaveATreatmentAdvice() {
        String patientId = "patientId";
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().build();
        when(allClinicVisits.get(anyString(), anyString())).thenReturn(clinicVisit);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(null);
        String redirectURL = clinicVisitsController.createForm(patientId, "", uiModel, request);

        assertEquals("clinicvisits/create", redirectURL);
        verify(treatmentAdviceController).createForm(patientId, uiModel);
        verify(labResultsController).createForm(patientId, uiModel);
        verify(vitalStatisticsController).createForm(patientId, uiModel);
    }

    @Test
    public void shouldCreateNewClinicVisitsFormGivenAPatientWithNoTreatmentAdvice() {
        String patientId = "patientId";
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().build();
        when(allClinicVisits.get(anyString(), anyString())).thenReturn(clinicVisit);
        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(null);
        String redirectURL = clinicVisitsController.createForm(patientId, "", uiModel, request);

        assertEquals("clinicvisits/create", redirectURL);
    }

    @Test
    public void shouldShowClinicVisitForm() {
        final String patientId = "patientId";
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
        when(allClinicVisits.get(anyString(), anyString())).thenReturn(clinicVisit);

        final String showUrl = clinicVisitsController.show(clinicVisit.getId(), patientId, uiModel);

        assertEquals("clinicvisits/show", showUrl);
        verify(treatmentAdviceController).show(clinicVisit.getTreatmentAdviceId(), uiModel);
        verify(labResultsController).show(patientId, clinicVisit.getId(), clinicVisit.getLabResultIds(), uiModel);
        verify(vitalStatisticsController).show(clinicVisit.getVitalStatisticsId(), uiModel);
    }

    @Test
    public void shouldCreateClinicVisit() {
        BindingResult bindingResult = mock(BindingResult.class);
        final TreatmentAdvice treatmentAdvice = new TreatmentAdvice() {{
            setPatientId("patientId");
        }};
        final LabResultsUIModel labResultsUIModel = new LabResultsUIModel();
        final VitalStatistics vitalStatistics = new VitalStatistics();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(treatmentAdviceController.create(bindingResult, uiModel, treatmentAdvice)).thenReturn("treatmentAdviceId");
        when(labResultsController.create(labResultsUIModel, bindingResult, uiModel)).thenReturn(new ArrayList<String>() {{
            add("labResultId");
        }});
        when(vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel)).thenReturn("vitalStatisticsId");
        final DateTime visitDate = DateUtil.now();
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().withVisitDate(visitDate).build();
        when(allClinicVisits.updateVisit(null, visitDate, "patientId", "treatmentAdviceId", Arrays.asList("labResultId"), "vitalStatisticsId")).thenReturn("clinicVisitId");
        String redirectURL = clinicVisitsController.create(null, clinicVisit,treatmentAdvice, labResultsUIModel, vitalStatistics, bindingResult, uiModel, request);

        assertEquals("redirect:/clinicvisits/clinicVisitId?patientId=patientId", redirectURL);
        verify(treatmentAdviceController).create(bindingResult, uiModel, treatmentAdvice);
        verify(labResultsController).create(labResultsUIModel, bindingResult, uiModel);
        verify(vitalStatisticsController).create(vitalStatistics, bindingResult, uiModel);
        verify(allClinicVisits).updateVisit(null, visitDate, "patientId", "treatmentAdviceId", Arrays.asList("labResultId"), "vitalStatisticsId");
    }

    @Test
    public void shouldReturnAllClinicVisitsForPatient() throws Exception {
        Model uiModel = mock(Model.class);
        final ClinicVisit visit1 = ClinicVisitBuilder.startRecording().withDefaults().build();
        final ClinicVisit visit2 = ClinicVisitBuilder.startRecording().withDefaults().build();
        when(allClinicVisits.clinicVisits(PATIENT_ID)).thenReturn(new ArrayList<ClinicVisit>() {{ add(visit1); add(visit2); }});

        final String listUrl = clinicVisitsController.list(PATIENT_ID, uiModel);

        assertEquals("clinicvisits/list", listUrl);
        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(uiModel).addAttribute(eq("clinicVisits"), listArgumentCaptor.capture());
        assertEquals(visit1, listArgumentCaptor.getValue().get(0));
        assertEquals(visit2, listArgumentCaptor.getValue().get(1));
    }

    @Test
    public void shouldUpdateConfirmVisitDate() throws Exception {
        final DateTime now = DateUtil.now();
        String jsonReturned = clinicVisitsController.confirmVisitDate(PATIENT_ID, VISIT_ID, now);

        verify(allClinicVisits).confirmVisitDate(PATIENT_ID, VISIT_ID, now);
        assertTrue(new JSONObject(jsonReturned).has("confirmedVisitDate"));
    }

    @Test
    public void shouldUpdateAdjustedDueDate() throws Exception {
        LocalDate today = DateUtil.today();
        String jsonReturned = clinicVisitsController.adjustDueDate(PATIENT_ID, VISIT_ID, today);
        verify(allClinicVisits).adjustDueDate(PATIENT_ID, VISIT_ID, today);
        assertTrue(new JSONObject(jsonReturned).has("adjustedDueDate"));
    }

    @Test
    public void shouldMarkClinicVisitAsMissed() throws Exception {
        String jsonReturned = clinicVisitsController.markAsMissed(PATIENT_ID, VISIT_ID);
        verify(allClinicVisits).markAsMissed(PATIENT_ID, VISIT_ID);
        assertTrue(new JSONObject(jsonReturned).has("missed"));
    }
}
