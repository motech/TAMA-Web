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
        PowerMockito.spy(Patient.class);

        Patient dbPatient = mock(Patient.class);
        String id = "1234";
        when(Patient.patients()).thenReturn(patients);
        when(patients.get(id)).thenReturn(dbPatient);
        when(dbPatient.activate()).thenReturn(dbPatient);

        String nextPage = controller.activate(id, request);

        verify(dbPatient).activate();
        verify(dbPatient).merge();
        assertTrue(nextPage.contains("redirect:/patients/"));
        assertTrue(nextPage.contains("1234"));
    }


    @Test
    public void shouldActivatePatientsByGet() {
        PowerMockito.spy(Patient.class);
        Patient dbPatient = mock(Patient.class);
        String id = "1234";
        when(Patient.patients()).thenReturn(patients);
        when(patients.get(id)).thenReturn(dbPatient);
        when(dbPatient.activate()).thenReturn(dbPatient);

        String nextPage = controller.activate(id);

        verify(dbPatient).activate();
        verify(dbPatient).merge();
        assertEquals("redirect:/patients", nextPage);
    }

    @Test
    public void shouldReturnToTheShowPatientPageIfPatientIsFound() {
        String patientId = "123";
        Patient patientFromDb = mock(Patient.class);
        List<Patient> patientsFromDb = new ArrayList<Patient>();
        patientsFromDb.add(patientFromDb);

        when(patients.findByPatientId(patientId)).thenReturn(patientsFromDb);
        when(patientFromDb.getId()).thenReturn("couchDbId");

        String nextPage = controller.findByPatientId(patientId, uiModel, request);

        assertEquals("redirect:/patients/couchDbId", nextPage);
    }

    @Test
    public void shouldReturnToTheSamePageIfPatientIsNotFound_WithOnlyOneQueryParameter() {
        String patientId = "123";
        String expectedPreviousPageUrl = "http://localhost:8080/tama/patients";
        String previousPage = expectedPreviousPageUrl +"?patientIdNotFound=1_abcd";

        when(patients.findByPatientId(patientId)).thenReturn(new ArrayList<Patient>());
        when(request.getHeader("Referer")).thenReturn(previousPage);

        String nextPage = controller.findByPatientId(patientId, uiModel, request);

        verify(uiModel).addAttribute(PatientController.PATIENT_ID_NOT_FOUND, patientId);
        assertEquals("redirect:" + expectedPreviousPageUrl, nextPage);
    }

    @Test
    public void shouldReturnToTheSamePageIfPatientIsNotFound_WithMultipleQueryParameters() {
        String patientId = "123";
        String expectedPreviousPageUrl = "http://localhost:8080/tama/patients?page=1";
        String previousPage = expectedPreviousPageUrl +"&patientIdNotFound=abc_8";

        when(patients.findByPatientId(patientId)).thenReturn(new ArrayList<Patient>());
        when(request.getHeader("Referer")).thenReturn(previousPage);

        String nextPage = controller.findByPatientId(patientId, uiModel, request);

        verify(uiModel).addAttribute(PatientController.PATIENT_ID_NOT_FOUND, patientId);
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

        verify(patientFromUI).persist();
        assertTrue(modelMap.isEmpty());
        assertEquals("redirect:/patients/123", createPage);
    }

    @Test
    @Ignore("needs to be fixed")
    public void shouldCreateAPatientIfThereAreErrors() {
//        PowerMockito.spy(Gender.class);
        Patient patientFromUI = mock(Patient.class);
        BindingResult bindingResult = mock(BindingResult.class);
//        List<Gender> genders = new ArrayList<Gender>();
//        genders.add(new Gender("Male"));

        when(bindingResult.hasErrors()).thenReturn(true);
//        when(Gender.findAllGenders()).thenReturn(genders);

        String createPage = controller.create(patientFromUI, bindingResult, uiModel, request);

        verify(uiModel).addAttribute("patient", patientFromUI);
        verify(uiModel).addAttribute("patient_dateofbirth_date_format", "M/d/yy");
//        verify(uiModel).addAttribute("genders", genders);
        assertTrue(uiModel.containsAttribute("genders"));
        assertEquals("patients/create", createPage);
    }
}
