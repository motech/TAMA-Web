package org.motechproject.tama.web;

import org.ektorp.UpdateConflictException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.verification.VerificationMode;
import org.motechproject.tama.TamaException;
import org.motechproject.tama.domain.CallPreference;
import org.motechproject.tama.domain.Gender;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.PatientPreferences;
import org.motechproject.tama.domain.TimeMeridiem;
import org.motechproject.tama.domain.TimeOfDay;
import org.motechproject.tama.platform.service.TamaSchedulerService;
import org.motechproject.tama.repository.*;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.service.PatientService;
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
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Patient.class, Gender.class})
public class PatientControllerTest {

    private PatientController controller;
    @Mock
    private Model uiModel;
    @Mock
    private AuthenticatedUser user;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllClinics allClinics;
    @Mock
    private AllGenders allGenders;
    @Mock
    private AllIVRLanguages allIVRLanguages;
    @Mock
    private AllHIVTestReasons allTestReasons;
    @Mock
    private AllModesOfTransmission allModesOfTransmission;
    @Mock
    private PatientService patientService;
	private TamaSchedulerService schedulerService;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new PatientController(allPatients, allClinics, allGenders, allIVRLanguages, allTestReasons, allModesOfTransmission, schedulerService, patientService);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
    }


    @Test
    public void shouldActivatePatientsByPost() {
        String id = "1234";
        String nextPage = controller.activate(id, request);

        verify(allPatients).activate(id);
        assertTrue(nextPage.contains("redirect:/patients/"));
        assertTrue(nextPage.contains("1234"));
    }

    @Test
    public void shouldActivatePatientsByGet() {
        String id = "1234";
        String nextPage = controller.activate(id);

        verify(allPatients).activate(id);
        assertEquals("redirect:/patients", nextPage);
    }

    @Test
    public void shouldReturnToTheShowPatientPageIfPatientIsFound() {
        String patientId = "123";
        String clinicId = "456";
        Patient patientFromDb = mock(Patient.class);
        List<Patient> patientsFromDb = new ArrayList<Patient>();
        patientsFromDb.add(patientFromDb);

        when(allPatients.findByPatientIdAndClinicId(patientId, clinicId)).thenReturn(patientsFromDb);
        when(patientFromDb.getId()).thenReturn("couchDbId");
        when(user.getClinicId()).thenReturn(clinicId);
        when(request.getSession()).thenReturn(session);


        String nextPage = controller.findByPatientId(patientId, uiModel, request);

        assertEquals("redirect:/patients/couchDbId", nextPage);
    }

    @Test
    public void shouldReturnToTheSamePageIfPatientIsNotFound_WithOnlyOneQueryParameter() {
        String patientId = "123";
        String clinicId = "456";
        String expectedPreviousPageUrl = "http://localhost:8080/tama/patients";
        String previousPage = expectedPreviousPageUrl + "?patientIdNotFound=1_abcd";

        when(allPatients.findByPatientIdAndClinicId(patientId, clinicId)).thenReturn(new ArrayList<Patient>());
        when(request.getHeader("Referer")).thenReturn(previousPage);
        when(user.getClinicId()).thenReturn(clinicId);
        when(request.getSession()).thenReturn(session);


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

        when(allPatients.findByPatientIdAndClinicId(patientId, clinicId)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn(previousPage);
        when(user.getClinicId()).thenReturn(clinicId);
        when(request.getSession()).thenReturn(session);


        String nextPage = controller.findByPatientId(patientId, uiModel, request);

        verify(uiModel).addAttribute(PatientController.PATIENT_ID, patientId);
        assertEquals("redirect:" + expectedPreviousPageUrl, nextPage);
    }

    @Test
    public void shouldCreateAPatientIfThereAreNoErrors() {
        String clinicId = "456";
        Patient patientFromUI = mock(Patient.class);
        when(patientFromUI.getPatientPreferences()).thenReturn(new PatientPreferences(){{
                                                        setCallPreference(CallPreference.DailyPillReminder);
                                                    }});
        BindingResult bindingResult = mock(BindingResult.class);
        Map<String, Object> modelMap = new HashMap<String, Object>();
        modelMap.put("dummyKey", "dummyValue");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(patientFromUI.getId()).thenReturn("123");
        when(uiModel.asMap()).thenReturn(modelMap);
        when(user.getClinicId()).thenReturn(clinicId);
        when(request.getSession()).thenReturn(session);


        String createPage = controller.create(patientFromUI, bindingResult, uiModel, request);

        verify(allPatients).addToClinic(patientFromUI, clinicId);
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

        when(user.getClinicId()).thenThrow(new TamaException(Patient.CLINIC_AND_PATIENT_ID_UNIQUE_CONSTRAINT + "some STUFF", new UpdateConflictException()));

        String createPage = controller.create(patientFromUI, bindingResult, uiModel, request);

        verify(bindingResult).addError(new FieldError("Patient", "patientId", patientFromUI.getPatientId(), false,
                new String[]{"clinic_and_patient_id_not_unique"}, new Object[]{}, PatientController.CLINIC_AND_PATIENT_ID_ALREADY_IN_USE));
        assertEquals("patients/create", createPage);
    }

    @Test
    public void shouldNeverScheduleJobForOutbox_whenPatientHasNotAgreedToBeCalledAtBestCallTime() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getSession()).thenReturn(session);
        Patient patient = new Patient();
        patient.setId("patientId");
        patient.setPatientPreferences(new PatientPreferences() {{
            setCallPreference(CallPreference.DailyPillReminder);
        }});
        controller.create(patient, bindingResult, uiModel, request);
        controller.update(patient, bindingResult, uiModel, request);
        verify(schedulerService, never()).scheduleJobForOutboxCall(patient);
    }

    @Test
    public void shouldScheduleJobForOutbox_whenPatientHasAgreedToBeCalledAtBestCallTime() {
        final TimeOfDay bestCallTime = new TimeOfDay(10, 30, TimeMeridiem.AM);

        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getSession()).thenReturn(session);

        Patient patient = new Patient();
        patient.setId("patientId");
        patient.setPatientPreferences(new PatientPreferences() {{
            setBestCallTime(bestCallTime);
            setCallPreference(CallPreference.DailyPillReminder);
        }});
        controller.create(patient, bindingResult, uiModel, request);

        controller.update(patient, bindingResult, uiModel, request);

        verify(schedulerService, new Times(2)).scheduleJobForOutboxCall(patient);
    }


    @Test
    public void shouldUpdatePatient() {
        Patient patientFromUI = mock(Patient.class);
        when(patientFromUI.getId()).thenReturn("123");
        BindingResult bindingResult = mock(BindingResult.class);
        Map<String, Object> modelMap = new HashMap<String, Object>();
        modelMap.put("dummyKey", "dummyValue");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(uiModel.asMap()).thenReturn(modelMap);

        String updatePage = controller.update(patientFromUI, bindingResult, uiModel, request);

        assertEquals("redirect:/patients/123", updatePage);
        verify(patientService).update(patientFromUI);
    }

}
