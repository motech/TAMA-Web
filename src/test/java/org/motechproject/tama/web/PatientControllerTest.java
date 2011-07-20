package org.motechproject.tama.web;

import org.ektorp.UpdateConflictException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.domain.Gender;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.repository.Genders;
import org.motechproject.tama.repository.IVRLanguages;
import org.motechproject.tama.repository.Patients;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    private AuthenticatedUser user;
    private HttpSession session;
    private Patients patients;
    private Clinics clinics;
    private Genders genders;
    private IVRLanguages ivrLanguages;

    @Before
    public void setUp() {
        patients = mock(Patients.class);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        uiModel = mock(Model.class);
        user = mock(AuthenticatedUser.class);
        clinics = mock(Clinics.class);
        genders = mock(Genders.class);
        ivrLanguages = mock(IVRLanguages.class);
        controller = new PatientController(patients, clinics, genders, ivrLanguages);
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
        String clinicId = "456";
        Patient patientFromDb = mock(Patient.class);
        List<Patient> patientsFromDb = new ArrayList<Patient>();
        patientsFromDb.add(patientFromDb);

        when(patients.findByPatientIdAndClinicId(patientId, clinicId)).thenReturn(patientsFromDb);
        when(patientFromDb.getId()).thenReturn("couchDbId");
        when(user.getClinicId()).thenReturn(clinicId);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);

        String nextPage = controller.findByPatientId(patientId, uiModel, request);

        assertEquals("redirect:/patients/couchDbId", nextPage);
    }

    @Test
    public void shouldReturnToTheSamePageIfPatientIsNotFound_WithOnlyOneQueryParameter() {
        String patientId = "123";
        String clinicId = "456";
        String expectedPreviousPageUrl = "http://localhost:8080/tama/patients";
        String previousPage = expectedPreviousPageUrl + "?patientIdNotFound=1_abcd";

        when(patients.findByPatientIdAndClinicId(patientId, clinicId)).thenReturn(new ArrayList<Patient>());
        when(request.getHeader("Referer")).thenReturn(previousPage);
        when(user.getClinicId()).thenReturn(clinicId);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);

        String nextPage = controller.findByPatientId(patientId, uiModel, request);

        verify(uiModel).addAttribute(PatientController.PATIENT_ID, patientId);
        assertEquals("redirect:" + expectedPreviousPageUrl, nextPage);
    }

    @Test
    public void shouldReturnToTheSamePageIfPatientIsNotFound_WithMultipleQueryParameters() {
        String patientId = "123";
        String clinicId = "456";
        String expectedPreviousPageUrl = "http://localhost:8080/tama/patients?page=1";
        String previousPage = expectedPreviousPageUrl + "&patientIdNotFound=abc_8";

        when(patients.findByPatientIdAndClinicId(patientId, clinicId)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn(previousPage);
        when(user.getClinicId()).thenReturn(clinicId);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);

        String nextPage = controller.findByPatientId(patientId, uiModel, request);

        verify(uiModel).addAttribute(PatientController.PATIENT_ID, patientId);
        assertEquals("redirect:" + expectedPreviousPageUrl, nextPage);
    }

    @Test
    public void shouldCreateAPatientIfThereAreNoErrors() {
        String clinicId = "456";
        Patient patientFromUI = mock(Patient.class);
        BindingResult bindingResult = mock(BindingResult.class);
        Map<String, Object> modelMap = new HashMap<String, Object>();
        modelMap.put("dummyKey", "dummyValue");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(patientFromUI.getId()).thenReturn("123");
        when(uiModel.asMap()).thenReturn(modelMap);
        when(user.getClinicId()).thenReturn(clinicId);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);


        String createPage = controller.create(patientFromUI, bindingResult, uiModel, request);

        verify(patients).addToClinic(patientFromUI, clinicId);
        assertTrue(modelMap.isEmpty());
        assertEquals("redirect:/patients/123", createPage);
    }

    @Test
    public void shouldNotCreateAPatientIfThePatientIdIsNotUniqueWithTheClinic() {
        Patient patientFromUI = mock(Patient.class);
        BindingResult bindingResult = mock(BindingResult.class);
        Map<String, Object> modelMap = new HashMap<String, Object>();
        modelMap.put("dummyKey", "dummyValue");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(patientFromUI.getId()).thenReturn("123");
        when(uiModel.asMap()).thenReturn(modelMap);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
        when(user.getClinicId()).thenThrow(new UpdateConflictException());

        String createPage = controller.create(patientFromUI, bindingResult, uiModel, request);

        verify(bindingResult).addError(new FieldError("Patient", "patientId", "patient id provided is already in use."));
        assertEquals("patients/create", createPage);
    }

}
