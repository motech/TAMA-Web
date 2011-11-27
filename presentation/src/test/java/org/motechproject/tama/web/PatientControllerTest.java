package org.motechproject.tama.web;

import org.ektorp.UpdateConflictException;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.motechproject.tama.TamaException;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.platform.service.TamaSchedulerService;
import org.motechproject.tama.repository.*;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.service.PatientService;
import org.motechproject.tama.web.view.SuspendedAdherenceData;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

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
    @Mock
	private TamaSchedulerService schedulerService;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new PatientController(allPatients, allClinics, allGenders, allIVRLanguages, allTestReasons, allModesOfTransmission, schedulerService, patientService);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
    }

    @Test
    public void shouldRenderShowPage() {
        when(request.getSession()).thenReturn(session);
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").build();
        when(allPatients.findByIdAndClinicId("patient_id", patient.getClinic_id())).thenReturn(patient);

        String returnPage = controller.show("patient_id", uiModel, request);

        assertEquals("patients/show", returnPage);

        verify(uiModel).addAttribute(PatientController.DATE_OF_BIRTH_FORMAT, DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
        verify(uiModel).addAttribute(PatientController.PATIENT, patient);
        verify(uiModel).addAttribute(PatientController.ITEM_ID, "patient_id");
        verify(uiModel).addAttribute(PatientController.DEACTIVATION_STATUSES, org.motechproject.tama.domain.Status.deactivationStatuses());
    }

    @Test
    public void shouldActivatePatientsByPost() {    // by mailing a letter?
        String id = "1234";
        String nextPage = controller.activate(id, request);

        verify(allPatients).activate(id);
        assertTrue(nextPage.contains("redirect:/patients/"));
        assertTrue(nextPage.contains("1234"));
    }

    @Test
    public void shouldActivatePatientsByGet() {     // wtf!
        String id = "1234";
        String nextPage = controller.activate(id);

        verify(allPatients).activate(id);
        assertEquals("redirect:/patients", nextPage);
    }

    @Test
    public void shouldDeactivatePatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").build();
        when(allPatients.get("patient_id")).thenReturn(patient);

        String nextPage = controller.deactivate("patient_id", org.motechproject.tama.domain.Status.Patient_Withdraws_Consent, request);

        assertEquals(org.motechproject.tama.domain.Status.Patient_Withdraws_Consent, patient.getStatus());
        assertEquals("redirect:/patients/patient_id", nextPage);
        verify(patientService).update(patient);
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

        verify(schedulerService, new Times(1)).scheduleJobForOutboxCall(patient);
    }


    @Test
    public void shouldUpdatePatient() {
        Patient patientFromUI = mock(Patient.class);
        when(patientFromUI.getPatientPreferences()).thenReturn(new PatientPreferences() {{
                    setCallPreference(CallPreference.DailyPillReminder);
                }});
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

    @Test
    public void shouldReactivatePatient(){
        String patientId = "patientId";
        SuspendedAdherenceData suspendedAdherenceData = new SuspendedAdherenceData();
        suspendedAdherenceData.setAdherenceDataWhenPatientWasSuspended(SuspendedAdherenceData.DosageStatusWhenSuspended.DOSE_NOT_TAKEN);

        controller.reactivatePatient(patientId, suspendedAdherenceData,uiModel, request);
        verify(patientService, times(1)).reActivate(patientId, suspendedAdherenceData);
    }

}
