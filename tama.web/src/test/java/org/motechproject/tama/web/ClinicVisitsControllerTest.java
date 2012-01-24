package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.web.model.LabResultsUIModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicVisitsControllerTest {

    @Mock
    private TreatmentAdviceController treatmentAdviceController;
    @Mock
    private LabResultsController labResultsController;
    @Mock
    private VitalStatisticsController vitalStatisticsController;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Model uiModel;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;

    private ClinicVisitsController controller;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new ClinicVisitsController(treatmentAdviceController, allTreatmentAdvices, labResultsController, vitalStatisticsController);
    }

    @Test
    public void shouldRedirectToShowClinicVisits_WhenPatientHasATreatmentAdvice() {
        String patientId = "patientId";
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").build();

        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        String redirectURL = controller.createForm(patientId, uiModel, request);

        assertEquals("redirect:/clinicvisits/" + patientId, redirectURL);
    }

    @Test
    public void shouldRedirectToCreateClinicVisits_WhenPatientDoesNotHaveATreatmentAdvice() {
        String patientId = "patientId";

        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(null);
        String redirectURL = controller.createForm(patientId, uiModel, request);

        assertEquals("clinicvisits/create", redirectURL);
        verify(treatmentAdviceController).createForm(patientId, uiModel);
        verify(labResultsController).createForm(patientId, uiModel);
        verify(vitalStatisticsController).createForm(patientId, uiModel);
    }

    @Test
    public void shouldCreateNewClinicVisitsFormGivenAPatientWithNoTreatmentAdvice() {
        String patientId = "patientId";

        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(null);
        String redirectURL = controller.createForm(patientId, uiModel, request);

        assertEquals("clinicvisits/create", redirectURL);
    }

    @Test
    public void shouldCreateClinicVisit() {
        BindingResult bindingResult = mock(BindingResult.class);
        final TreatmentAdvice treatmentAdvice = new TreatmentAdvice() {{ setPatientId("patientId"); }};
        final LabResultsUIModel labResultsUiModel = new LabResultsUIModel();
        final VitalStatistics vitalStatistics = new VitalStatistics();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(uiModel.asMap()).thenReturn(new HashMap<String, Object>());

        String redirectURL = controller.create(treatmentAdvice, labResultsUiModel, vitalStatistics, bindingResult, uiModel, request);

        assertEquals("redirect:/patients/patientId", redirectURL);
        verify(treatmentAdviceController).create(bindingResult, uiModel, treatmentAdvice);
        verify(labResultsController).create(labResultsUiModel, bindingResult, uiModel);
        verify(vitalStatisticsController).create(vitalStatistics, bindingResult, uiModel);
    }
}
