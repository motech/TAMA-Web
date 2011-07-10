package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.domain.Gender;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Patients;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Patient.class, Gender.class})
public class PatientControllerTest {

    private PatientController controller;
    private HttpServletRequest request;
    private Model uiModel;
    private Patients patients;

    @Before
    public void setUp() {
        patients = mock(Patients.class);
        controller = new PatientController(patients);
        request = mock(HttpServletRequest.class);
        uiModel = mock(Model.class);
    }

    @Test
    public void shouldActivatePatientsByPost() {
        String id = "1234";
        String nextPage = controller.activate(id, request);

        verify(patients).activate(id);
        assertTrue(nextPage.contains("redirect:/patients/"));
        assertTrue(nextPage.contains("1234"));
    }


    @Test
    public void shouldActivatePatientsByGet() {
        String id = "1234";
        String nextPage = controller.activate(id);

        verify(patients).activate(id);
        assertEquals("redirect:/patients", nextPage);
    }

    @Test
    public void shouldReturnToTheShowPatientPageIfPatientIsFound() {
        String patientId = "123";
        Patient patientFromDb = mock(Patient.class);
        List<Patient> patientsFromDb = new ArrayList<Patient>();
        patientsFromDb.add(patientFromDb);

        when(patients.findById(patientId)).thenReturn(patientsFromDb);
        when(patientFromDb.getId()).thenReturn("couchDbId");

        String nextPage = controller.findByPatientId(patientId, uiModel, request);

        assertEquals("redirect:/patients/couchDbId", nextPage);
    }

    @Test
    public void shouldReturnToTheSamePageIfPatientIsNotFound_WithOnlyOneQueryParameter() {
        String patientId = "123";
        String expectedPreviousPageUrl = "http://localhost:8080/tama/patients";
        String previousPage = expectedPreviousPageUrl +"?patientIdNotFound=1_abcd";

        when(patients.findById(patientId)).thenReturn(new ArrayList<Patient>());
        when(request.getHeader("Referer")).thenReturn(previousPage);

        String nextPage = controller.findByPatientId(patientId, uiModel, request);

        verify(uiModel).addAttribute(PatientController.PATIENT_ID_NOT_FOUND_ATTR, patientId);
        assertEquals("redirect:" + expectedPreviousPageUrl, nextPage);
    }

    @Test
    public void shouldReturnToTheSamePageIfPatientIsNotFound_WithMultipleQueryParameters() {
        String patientId = "123";
        String expectedPreviousPageUrl = "http://localhost:8080/tama/patients?page=1";
        String previousPage = expectedPreviousPageUrl +"&patientIdNotFound=abc_8";

        when(patients.findById(patientId)).thenReturn(new ArrayList<Patient>());
        when(request.getHeader("Referer")).thenReturn(previousPage);

        String nextPage = controller.findByPatientId(patientId, uiModel, request);

        verify(uiModel).addAttribute(PatientController.PATIENT_ID_NOT_FOUND_ATTR, patientId);
        assertEquals("redirect:" + expectedPreviousPageUrl, nextPage);
    }

    @Test
    public void shouldCreateAPatientIfThereAreNoErrors() {
        Patient patientFromUI = mock(Patient.class);
        BindingResult bindingResult = mock(BindingResult.class);
        Map<String, Object> modelMap = new HashMap<String, Object>();
        modelMap.put("dummyKey", "dummyValue");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(patientFromUI.getId()).thenReturn("123");
        when(uiModel.asMap()).thenReturn(modelMap);

        String createPage = controller.create(patientFromUI, bindingResult, uiModel, request);

        verify(patients).addToClinic(patientFromUI);
        assertTrue(modelMap.isEmpty());
        assertEquals("redirect:/patients/123", createPage);
    }

}
