package org.motechproject.tama.web.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.domain.LabResult;
import org.motechproject.tama.repository.LabResults;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class LabResultsControllerTest {

    private LabResultsController labResultsController;

    @Mock
    private LabResults labResults;

    @Before
    public void setUp() {
        initMocks(this);
        labResultsController = new LabResultsController(labResults);
    }

    @Test
    public void testCreateFormShouldReturnLabResultsForm() {
        Model model = mock(Model.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        assertEquals("labresults/create", labResultsController.createForm("patientId", model, servletRequest));
    }

    @Test
    public void testCreateFormAddsNewLabResultToModel() {
        String patientId = "patientId";

        Model model = mock(Model.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        labResultsController.createForm(patientId, model, servletRequest);

        ArgumentCaptor<Object> labResultCapture = ArgumentCaptor.forClass(Object.class);
        verify(model).addAttribute(eq("labResult"), labResultCapture.capture());

        LabResult labResult = (LabResult) labResultCapture.getValue();
        assertEquals(labResult.getPatientId(), patientId);
    }

    @Test
    public void shouldCreateLabResults_ValidFormSubmitted() {
        String patientId = "patientId";
        LabResult labResult = new LabResult();
        labResult.setPatientId(patientId);

        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = mock(Model.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        labResultsController.create(labResult, bindingResult, uiModel, httpServletRequest);

        verify(labResults, times(1)).add(labResult);
    }

    @Test
    public void shouldRedirectToShowLabResultsPage_ValidFormSubmitted() {
        String patientId = "patientId";
        LabResult labResult = new LabResult();
        labResult.setPatientId(patientId);

        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = mock(Model.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        String redirectURL = labResultsController.create(labResult, bindingResult, uiModel, httpServletRequest);

        assertEquals("redirect:/labresults/" + patientId, redirectURL);
    }

    @Test
    public void shouldShowLabResultsPage() {
        LabResult labresult = new LabResult();
        Model uiModel = mock(Model.class);
        String patientId = "patientId";
        List<LabResult> labResultsForPatient = Arrays.asList(labresult);

        when(labResults.findByPatientId(patientId)).thenReturn(labResultsForPatient);

        String showURL = labResultsController.show(patientId, uiModel);

        assertEquals("labresults/show", showURL);
        verify(uiModel).addAttribute("labresults", labResultsForPatient);
    }

}
