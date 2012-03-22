package org.motechproject.tama.web.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.builder.LabResultBuilder;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.refdata.builder.LabTestBuilder;
import org.motechproject.tama.refdata.domain.LabTest;
import org.motechproject.tama.refdata.repository.AllLabTests;
import org.motechproject.tama.web.LabResultsController;
import org.motechproject.tama.web.model.LabResultsUIModel;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class LabResultsControllerTest {
    public static final String PATIENT_ID = "patientId";
    public static final String USER_ID = "userId";

    private LabResultsController labResultsController;
    @Mock
    private AllLabResults allLabResults;
    @Mock
    private AllLabTests allLabTests;
    @Mock
    private AllClinicVisits allClinicVisits;
    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        initMocks(this);
        labResultsController = new LabResultsController(allLabResults, allLabTests, allClinicVisits);
    }

    @Test
    public void createFormShouldShowLabResultsForm() {
        Model model = new ExtendedModelMap();

        when(allLabTests.getAll()).thenReturn(Collections.<LabTest>emptyList());

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

        labResultsController.create(labResultsUIModel, bindingResult, uiModel, request);

        verify(allLabResults, times(1)).upsert(labResult, USER_ID);
    }

    @Test
    public void createShouldNotSaveLabResultsForPatient_WhenResultIsNotSet() {
        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = mock(Model.class);

        LabResultsUIModel labResultsUIModel = new LabResultsUIModel();
        labResultsUIModel.setLabResults(new LabResults(Arrays.asList(new LabResult())));

        labResultsController.create(labResultsUIModel, bindingResult, uiModel, request);

        verifyZeroInteractions(allLabResults);
    }

    @Test
    public void createPopulateLabResultsToUIModel_WhenSubmittedDataHasErrors() {
        String patientId = "patientId";
        LabResult labResult = new LabResult();
        labResult.setPatientId(patientId);

        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = new ExtendedModelMap();

        LabResultsUIModel labResultsUIModel = new LabResultsUIModel();
        labResultsUIModel.setLabResults(new LabResults(Arrays.asList(labResult)));

        when(bindingResult.hasErrors()).thenReturn(true);
        labResultsController.create(labResultsUIModel, bindingResult, uiModel, request);

        verifyZeroInteractions(allLabResults);
        assertEquals(labResultsUIModel, uiModel.asMap().get("labResultUiModel"));
    }

    @Test
    public void shouldAddFlashErrorMessage_WhenLabResultCreateErrorsOut() {
        LabResult labResult = LabResultBuilder.startRecording().withDefaults().build();
        BindingResult bindingResult = mock(BindingResult.class);
        LabResultsUIModel labResultsUIModel = new LabResultsUIModel();
        labResultsUIModel.setLabResults(new LabResults(Arrays.asList(labResult)));

        doThrow(new RuntimeException("Some error")).when(allLabResults).upsert(labResult, USER_ID);

        labResultsController.create(labResultsUIModel, bindingResult, new ExtendedModelMap(), request);

        verify(request).setAttribute("flash.flashErrorLabResults", "Error occurred while creating Lab Results: Some error");
    }

    @Test
    public void showShouldAddLabResultsForPatientToUIModel() {
        final String patientId = "patientId";
        final String labResultId = "labResultId";
        final String clinicVisitId = "clinicVisitId";
        LabResult labResult = new LabResult() {{
            setId(labResultId);
        }};
        Model uiModel = new ExtendedModelMap();
        LabResults labResultsForPatient = new LabResults(Arrays.asList(labResult));

        when(allLabResults.get(labResultId)).thenReturn(labResult);

        labResultsController.show(patientId, clinicVisitId, new ArrayList<String>() {{
            add(labResultId);
        }}, uiModel);

        assertEquals(labResultsForPatient, uiModel.asMap().get("labResultsForPatient"));
        assertEquals(patientId, uiModel.asMap().get("patientId"));
        assertEquals(clinicVisitId, uiModel.asMap().get("clinicVisitId"));
    }

    @Test
    public void updateFormShouldShowLabResultsSavedAgainstClinicVisit() {
        Model uiModel = new ExtendedModelMap();
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
        when(allClinicVisits.get(PATIENT_ID, clinicVisit.getId())).thenReturn(clinicVisit);
        final LabTest cd4Test = LabTestBuilder.startRecording().withType(TAMAConstants.LabTestType.CD4).withId("CD4").build();
        final LabTest pvlTest = LabTestBuilder.startRecording().withType(TAMAConstants.LabTestType.PVL).withId("PVL").build();
        when(allLabTests.getAll()).thenReturn(new ArrayList<LabTest>() {{
            add(cd4Test);
            add(pvlTest);
        }});
        LabResult labresult = LabResultBuilder.startRecording().withLabTest(cd4Test).withLabTestId("CD4").withResult("100").build();
        when(allLabResults.get("labResultId")).thenReturn(labresult);

        assertEquals("labresults/update", labResultsController.updateForm(PATIENT_ID, clinicVisit.getId(), uiModel));
        final LabResultsUIModel labResultsUIModel = (LabResultsUIModel) uiModel.asMap().get("labResultsUIModel");
        assertEquals(2, labResultsUIModel.getLabResults().size());
        assertEquals("PVL", labResultsUIModel.getLabResults().get(0).getLabTest().getId());
        assertEquals(null, labResultsUIModel.getLabResults().get(0).getResult());
        assertEquals("CD4", labResultsUIModel.getLabResults().get(1).getLabTest().getId());
        assertEquals("100", labResultsUIModel.getLabResults().get(1).getResult());
        assertEquals(clinicVisit.getId(), uiModel.asMap().get("clinicVisitId"));
    }

    @Test
    public void updateFormShouldShowEmptyLabResultsWhenNoLabResultsAreSavedAgainstClinicVisit() {
        Model uiModel = new ExtendedModelMap();
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().withLabResultIds(new ArrayList<String>()).build();
        when(allClinicVisits.get(PATIENT_ID, clinicVisit.getId())).thenReturn(clinicVisit);
        final LabTest cd4Test = LabTestBuilder.startRecording().withType(TAMAConstants.LabTestType.CD4).withId("CD4").build();
        final LabTest pvlTest = LabTestBuilder.startRecording().withType(TAMAConstants.LabTestType.PVL).withId("PVL").build();
        when(allLabTests.getAll()).thenReturn(new ArrayList<LabTest>() {{
            add(cd4Test);
            add(pvlTest);
        }});

        assertEquals("labresults/update", labResultsController.updateForm(PATIENT_ID, clinicVisit.getId(), uiModel));
        final LabResultsUIModel labResultsUIModel = (LabResultsUIModel) uiModel.asMap().get("labResultsUIModel");
        assertEquals(2, labResultsUIModel.getLabResults().size());
        assertEquals("PVL", labResultsUIModel.getLabResults().get(0).getLabTest().getId());
        assertEquals(null, labResultsUIModel.getLabResults().get(0).getResult());
        assertEquals("CD4", labResultsUIModel.getLabResults().get(1).getLabTest().getId());
        assertEquals(null, labResultsUIModel.getLabResults().get(1).getResult());
        assertEquals(clinicVisit.getId(), uiModel.asMap().get("clinicVisitId"));
    }

    @Test
    public void updateShouldReturnUpdateForm_SubmittedDataHasErrors() {
        Model uiModel = new ExtendedModelMap();
        BindingResult bindingResult = mock(BindingResult.class);
        LabResultsUIModel labResultsUIModel = mock(LabResultsUIModel.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = labResultsController.update(labResultsUIModel, bindingResult, uiModel, request);

        verifyZeroInteractions(allLabResults);
        assertEquals("labresults/update", viewName);
        assertEquals(labResultsUIModel, uiModel.asMap().get("labResultsUIModel"));
    }

    @Test
    public void updateShouldAddNewlyAddedLabResultsToClinicVisit() {
        LabResultsUIModel labResultsUIModel = new LabResultsUIModel();
        final LabResults labResults = new LabResults() {{
            add(LabResultBuilder.startRecording().withDefaults().build());
            add(LabResultBuilder.startRecording().withDefaults().build());
        }};
        labResultsUIModel.setLabResults(labResults);
        labResultsUIModel.setClinicVisitId("clinicVisitId");
        when(allLabResults.upsert(Matchers.<LabResult>any(), USER_ID)).thenReturn("labResultId1").thenReturn("labResultId2");

        String redirectURL = labResultsController.update(labResultsUIModel, mock(BindingResult.class), new ExtendedModelMap(), request);

        final ArgumentCaptor<ArrayList> labResultIdsArgumentCaptor = ArgumentCaptor.forClass(ArrayList.class);
        verify(allClinicVisits).updateLabResults(eq(PATIENT_ID), eq("clinicVisitId"), labResultIdsArgumentCaptor.capture());
        final List<String> expectedLabResults = new ArrayList<String>() {{
            add("labResultId1");
            add("labResultId2");
        }};
        assertEquals(expectedLabResults, labResultIdsArgumentCaptor.getValue());
        assertEquals("redirect:/clinicvisits/clinicVisitId?patientId=patientId", redirectURL);
    }
}
