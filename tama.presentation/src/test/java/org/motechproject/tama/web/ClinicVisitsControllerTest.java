package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicVisitsControllerTest {

    @Mock
    private TreatmentAdviceController treatmentAdviceController;
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
        controller = new ClinicVisitsController(treatmentAdviceController, allTreatmentAdvices);
    }

    @Test
    public void shouldRedirectToShowClinicVisits_WhenPatientHasATreatmentAdvice() {
        String patientId = "patientId";
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").build();

        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        String redirectURL = controller.createForm(patientId, uiModel, request);

        junit.framework.Assert.assertEquals("redirect:/clinicvisits/treatmentAdviceId", redirectURL);
    }

    @Test
    public void shouldRedirectToCreateClinicVisits_WhenPatientDoesNotHaveATreatmentAdvice() {
        String patientId = "patientId";

        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(null);
        String redirectURL = controller.createForm(patientId, uiModel, request);

        junit.framework.Assert.assertEquals("clinicvisits/create", redirectURL);
        verify(treatmentAdviceController).createForm(patientId, uiModel);
    }

    @Test
    public void shouldCreateNewClinicVisitsFormGivenAPatientWithNoTreatmentAdvice() {
        String patientId = "patientId";

        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(null);
        String redirectURL = controller.createForm(patientId, uiModel, request);

        junit.framework.Assert.assertEquals("clinicvisits/create", redirectURL);
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
        verify(treatmentAdviceController).create(treatmentAdvice, uiModel);
    }
}
