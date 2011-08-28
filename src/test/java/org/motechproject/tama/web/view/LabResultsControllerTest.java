package org.motechproject.tama.web.view;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.builder.LabTestBuilder;
import org.motechproject.tama.domain.LabResult;
import org.motechproject.tama.domain.LabTest;
import org.motechproject.tama.repository.LabResults;
import org.motechproject.tama.repository.LabTests;
import org.motechproject.tama.web.model.LabResultsUIModel;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class LabResultsControllerTest {

    private LabResultsController labResultsController;

    @Mock
    private LabResults labResults;

    @Mock
    private LabTests labTests;

    private LocalDate today;

    @Before
    public void setUp() {
        initMocks(this);
        labResultsController = new LabResultsController(labResults, labTests);
        today = new LocalDate(2011, 12, 12);

        PowerMockito.mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(today);

    }

    @Test
    public void createFormShouldShowLabResultsForm_WhenNoLabResultRecordedForPatient() {
        Model model = mock(Model.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        when(labTests.getAll()).thenReturn(Collections.<LabTest>emptyList());

        assertEquals("labresults/create", labResultsController.createForm("patientId", model, servletRequest));
    }

    @Test
    public void createFormShouldShowAllLabResultsForPatient__WhenAnyLabResultRecordedForPatient() {
        String patientId = "patientId";
        LabResult labresult = new LabResult();
        List<LabResult> labResultsForPatient = Arrays.asList(labresult);

        Model model = mock(Model.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        when(labResults.findByPatientId(patientId)).thenReturn(labResultsForPatient);

        assertEquals("redirect:/labresults/" + "patientId", labResultsController.createForm("patientId", model, servletRequest));
    }

    @Test
    public void createFormShouldPopulateUIModelWithOneLabResultForEachLabTestDefined() {
        String patientId = "patientId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId("labTest").build();
        LabTest anotherLabTest = LabTestBuilder.startRecording().withDefaults().withId("anotherLabTest").build();

        Model model = mock(Model.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(labTests.getAll()).thenReturn(Arrays.asList(labTest, anotherLabTest));

        labResultsController.createForm(patientId, model, servletRequest);

        ArgumentCaptor<List> labResultCapture = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("labResults"), labResultCapture.capture());

        List<LabResult> labResults = labResultCapture.getValue();
        assertEquals(2, labResults.size());
        assertEquals("labTest", labResults.get(0).getLabTest_id());
        assertEquals("anotherLabTest", labResults.get(1).getLabTest_id());
    }


    @Test
    public void createFormShouldAddPatientIdToUIModel() {
        String patientId = "somePatientId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().build();
        List<LabTest> labTests = Arrays.asList(labTest);

        Model model = mock(Model.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(this.labTests.getAll()).thenReturn(labTests);

        labResultsController.createForm(patientId, model, servletRequest);

        ArgumentCaptor<String> labTestCapture = ArgumentCaptor.forClass(String.class);
        verify(model).addAttribute(eq("patientId"), labTestCapture.capture());

        assertEquals("somePatientId", labTestCapture.getValue());
    }


    @Test
    public void createFormShouldAddAllLabTestsDefinedToUIModel() {
        String patientId = "patientId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().build();
        List<LabTest> labTests = Arrays.asList(labTest);

        Model model = mock(Model.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(this.labTests.getAll()).thenReturn(labTests);

        labResultsController.createForm(patientId, model, servletRequest);

        ArgumentCaptor<List> labTestCapture = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("labTests"), labTestCapture.capture());

        assertEquals(labTest, labTestCapture.getValue().get(0));
    }


    @Test
    public void createShouldSaveLabResultsForPatient() {
        String patientId = "patientId";
        LabResult labResult = new LabResult();
        labResult.setPatientId(patientId);

        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = mock(Model.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        LabResultsUIModel labResultsUIModel = new LabResultsUIModel();
        labResultsUIModel.setLabResults(Arrays.asList(labResult));

        labResultsController.create(labResultsUIModel, bindingResult, uiModel, httpServletRequest);


        verify(labResults, times(1)).add(labResult);
    }

    @Test
    public void createShouldShowAllLabResults_AfterSavingLabResults() {
        String patientId = "patientId";
        LabResult labResult = new LabResult();
        labResult.setPatientId(patientId);

        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = mock(Model.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        LabResultsUIModel labResultsUIModel = new LabResultsUIModel();
        labResultsUIModel.setLabResults(Arrays.asList(labResult));

        String redirectURL = labResultsController.create(labResultsUIModel, bindingResult, uiModel, httpServletRequest);

        assertEquals("redirect:/labresults/" + patientId, redirectURL);
    }

    @Test
    public void showShouldAddLabResultsForPatientToUIModel() {
        LabResult labresult = new LabResult();
        Model uiModel = mock(Model.class);
        String patientId = "patientId";
        List<LabResult> labResultsForPatient = Arrays.asList(labresult);

        when(labResults.findByPatientId(patientId)).thenReturn(labResultsForPatient);

        String showURL = labResultsController.show(patientId, uiModel);

        assertEquals("labresults/show", showURL);
    }

    @Test
    public void showShouldAddPatientIdToUIModel() {
        LabResult labresult = new LabResult();
        Model uiModel = mock(Model.class);
        String patientId = "somePatientId";
        List<LabResult> labResultsForPatient = Arrays.asList(labresult);

        when(labResults.findByPatientId(patientId)).thenReturn(labResultsForPatient);

        labResultsController.show(patientId, uiModel);

        verify(uiModel).addAttribute("labResultsForPatient", labResultsForPatient);
        verify(uiModel).addAttribute("patientId", patientId);
    }

    @Test
    public void updateFormShouldShowLabResultsEditForm() {
        String patientId = "patientId";
        Model uiModel = mock(Model.class);

        assertEquals("labresults/update", labResultsController.updateForm(patientId, uiModel));
    }

    @Test
    public void updateFormShouldAddPatientIdToUIModel() {
        Model uiModel = mock(Model.class);
        String patientId = "somePatientId";

        labResultsController.updateForm(patientId, uiModel);

        verify(uiModel).addAttribute("patientId", patientId);
    }

    @Test
    public void updateFormShouldPopulateUIModel() {
        LabResult labresult = new LabResult();
        Model uiModel = mock(Model.class);
        String patientId = "somePatientId";
        List<LabResult> labResultsForPatient = Arrays.asList(labresult);

        when(labResults.findByPatientId(patientId)).thenReturn(labResultsForPatient);

        labResultsController.updateForm(patientId, uiModel);

        ArgumentCaptor<LabResultsUIModel> labResults = ArgumentCaptor.forClass(LabResultsUIModel.class);

        verify(uiModel).addAttribute(eq("labResultsUIModel"), labResults.capture());
        verify(uiModel).addAttribute("patientId", patientId);
        assertEquals(labResultsForPatient, labResults.getValue().getLabResults());
    }

    @Test
    public void updateShouldMergeAllLabResults() {
        String patientId = "patientId";
        LabResult labResult = new LabResult();
        labResult.setPatientId(patientId);
        List<LabResult> labResultsForPatient = Arrays.asList(labResult);

        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = mock(Model.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        LabResultsUIModel labResultsUIModel = new LabResultsUIModel();
        labResultsUIModel.setLabResults(labResultsForPatient);

        labResultsController.update(labResultsUIModel, bindingResult, uiModel, httpServletRequest);

        verify(labResults).merge(labResultsForPatient);
    }

    @Test
    public void updateShouldShowAllLabResults_AfterSavingLabResults() {
        String patientId = "patientId";
        LabResult labResult = new LabResult();
        labResult.setPatientId(patientId);

        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = mock(Model.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        LabResultsUIModel labResultsUIModel = new LabResultsUIModel();
        labResultsUIModel.setLabResults(Arrays.asList(labResult));

        String redirectURL = labResultsController.update(labResultsUIModel, bindingResult, uiModel, httpServletRequest);

        assertEquals("redirect:/labresults/" + patientId, redirectURL);
    }

}
