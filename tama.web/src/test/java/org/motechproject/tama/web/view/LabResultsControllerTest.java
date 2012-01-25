package org.motechproject.tama.web.view;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.refdata.builder.LabTestBuilder;
import org.motechproject.tama.refdata.domain.LabTest;
import org.motechproject.tama.refdata.repository.AllLabTests;
import org.motechproject.tama.web.LabResultsController;
import org.motechproject.tama.web.model.LabResultsUIModel;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.ExtendedModelMap;
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
    private AllLabResults allLabResults;
    @Mock
    private AllLabTests allLabTests;

    @Before
    public void setUp() {
        initMocks(this);
        labResultsController = new LabResultsController(allLabResults, allLabTests);
        LocalDate today = new LocalDate(2011, 12, 12);

        PowerMockito.mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(today);
    }

    @Test
    public void createFormShouldShowLabResultsForm() {
        Model model = new ExtendedModelMap();

        when(allLabTests.getAll()).thenReturn(Collections.<LabTest>emptyList());
        when(allLabResults.findLatestLabResultsByPatientId("patientId")).thenReturn(new LabResults());

        labResultsController.createForm("patientId", model);
        assertEquals(LabResultsUIModel.class, model.asMap().get("labResultsUIModel").getClass());
    }

    @Test
    public void createFormShouldPopulateUIModelWithOneLabResultForEachLabTestDefined() {
        String patientId = "patientId";
        LabTest labTest = LabTestBuilder.startRecording().withDefaults().withId("labTest").build();
        LabTest anotherLabTest = LabTestBuilder.startRecording().withDefaults().withId("anotherLabTest").build();

        Model model = mock(Model.class);
        when(allLabTests.getAll()).thenReturn(Arrays.asList(labTest, anotherLabTest));
        when(allLabResults.findLatestLabResultsByPatientId("patientId")).thenReturn(new LabResults());

        labResultsController.createForm(patientId, model);

        ArgumentCaptor<LabResults> labResultCapture = ArgumentCaptor.forClass(LabResults.class);
        verify(model).addAttribute(eq("labResults"), labResultCapture.capture());

        LabResults labResults = labResultCapture.getValue();
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
        when(allLabTests.getAll()).thenReturn(labTests);
        when(allLabResults.findLatestLabResultsByPatientId("somePatientId")).thenReturn(new LabResults());

        labResultsController.createForm(patientId, model);

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
        when(this.allLabTests.getAll()).thenReturn(labTests);
        when(allLabResults.findLatestLabResultsByPatientId("patientId")).thenReturn(new LabResults());

        labResultsController.createForm(patientId, model);

        ArgumentCaptor<List> labTestCapture = ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("labTests"), labTestCapture.capture());

        assertEquals(labTest, labTestCapture.getValue().get(0));
    }

    @Test
    public void createShouldSaveLabResultsForPatient() {
        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = mock(Model.class);

        LabResultsUIModel labResultsUIModel = new LabResultsUIModel();
        LabResult labResult = new LabResult() {{
            setResult("0");
        }};
        labResultsUIModel.setLabResults(new LabResults(Arrays.asList(labResult)));

        labResultsController.create(labResultsUIModel, bindingResult, uiModel);

        verify(allLabResults, times(1)).upsert(labResult);
    }

    @Test
    public void createShouldNotSaveLabResultsForPatient_WhenResultIsNotSet() {
        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = mock(Model.class);

        LabResultsUIModel labResultsUIModel = new LabResultsUIModel();
        labResultsUIModel.setLabResults(new LabResults(Arrays.asList(new LabResult())));

        labResultsController.create(labResultsUIModel, bindingResult, uiModel);

        verifyZeroInteractions(allLabResults);
    }

    @Test
    public void createShouldReturnCreateView_SubmittedDataHasErrors() {
        String patientId = "patientId";
        LabResult labResult = new LabResult();
        labResult.setPatientId(patientId);

        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = new ExtendedModelMap();

        LabResultsUIModel labResultsUIModel = new LabResultsUIModel();
        labResultsUIModel.setLabResults(new LabResults(Arrays.asList(labResult)));

        when(bindingResult.hasErrors()).thenReturn(true);
        labResultsController.create(labResultsUIModel, bindingResult, uiModel);

        verifyZeroInteractions(allLabResults);
        assertEquals(labResultsUIModel, uiModel.asMap().get("labResultUiModel"));
    }

    @Test
    public void showShouldAddLabResultsForPatientToUIModel() {
        LabResult labresult = new LabResult();
        Model uiModel = new ExtendedModelMap();
        String patientId = "patientId";
        LabResults labResultsForPatient = new LabResults(Arrays.asList(labresult));

        when(allLabResults.findLatestLabResultsByPatientId(patientId)).thenReturn(labResultsForPatient);

        labResultsController.show(patientId, uiModel);

        assertEquals(labResultsForPatient, uiModel.asMap().get("labResultsForPatient"));
        assertEquals(patientId, uiModel.asMap().get("patientId"));
    }

    @Test
    public void updateFormShouldShowLabResultsEditForm() {
        String patientId = "patientId";
        Model uiModel = new ExtendedModelMap();
        LabResult labresult = new LabResult();
        LabResults labResultsForPatient = new LabResults(Arrays.asList(labresult));
        when(allLabResults.findLatestLabResultsByPatientId(patientId)).thenReturn(labResultsForPatient);

        assertEquals("labresults/update", labResultsController.updateForm(patientId, uiModel));
        assertEquals(patientId, uiModel.asMap().get("patientId"));
        assertEquals(LabResultsUIModel.class, uiModel.asMap().get("labResultsUIModel").getClass());
        assertEquals(labResultsForPatient, ((LabResultsUIModel) uiModel.asMap().get("labResultsUIModel")).getLabResults());
    }

    @Test
    public void updateShouldReturnUpdateForm_SubmittedDataHasErrors() {
        LabResult labResult = new LabResult();
        String patientId = "somePatientId";
        labResult.setPatientId(patientId);
        LabResults labResultsForPatient = new LabResults(Arrays.asList(labResult));

        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = mock(Model.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        LabResultsUIModel labResultsUIModel = new LabResultsUIModel();
        labResultsUIModel.setLabResults(labResultsForPatient);

        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = labResultsController.update(labResultsUIModel, bindingResult, uiModel, httpServletRequest);

        verify(allLabResults, never()).upsert(labResult);
        assertEquals("labresults/update", viewName);
    }

    @Test
    public void updateShouldMergeAllLabResults() {
        String patientId = "patientId";
        LabResult labResult = new LabResult();
        labResult.setPatientId(patientId);
        LabResults labResultsForPatient = new LabResults(Arrays.asList(labResult));

        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = mock(Model.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        LabResultsUIModel labResultsUIModel = new LabResultsUIModel();
        labResultsUIModel.setLabResults(labResultsForPatient);

        labResultsController.update(labResultsUIModel, bindingResult, uiModel, httpServletRequest);

        verify(allLabResults).upsert(labResult);
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
        labResultsUIModel.setLabResults(new LabResults(Arrays.asList(labResult)));

        String redirectURL = labResultsController.update(labResultsUIModel, bindingResult, uiModel, httpServletRequest);

        assertEquals("redirect:/clinicvisits/" + patientId, redirectURL);
    }

}
