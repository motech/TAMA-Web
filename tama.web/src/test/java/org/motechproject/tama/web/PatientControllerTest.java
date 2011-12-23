package org.motechproject.tama.web;

import org.ektorp.UpdateConflictException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.common.TamaException;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.fourdayrecall.service.ResumeFourDayRecallService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.refdata.domain.Gender;
import org.motechproject.tama.refdata.repository.AllGenders;
import org.motechproject.tama.refdata.repository.AllHIVTestReasons;
import org.motechproject.tama.refdata.repository.AllIVRLanguages;
import org.motechproject.tama.refdata.repository.AllModesOfTransmission;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.web.model.DoseStatus;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private AllVitalStatistics allVitalStatistics;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllLabResults allLabResults;
    @Mock
    private PatientService patientService;
    @Mock
    private DailyPillReminderAdherenceService dailyPillReminderAdherenceService;
    @Mock
    private ResumeFourDayRecallService resumeFourDayRecallService;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new PatientController(allPatients, allClinics, allGenders, allIVRLanguages, allTestReasons, allModesOfTransmission, allTreatmentAdvices, allVitalStatistics, allLabResults, patientService, dailyPillReminderAdherenceService, resumeFourDayRecallService);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
    }

    @Test
    public void shouldRenderShowPage() {
        when(request.getSession()).thenReturn(session);
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patient_id").withStatus(Status.Active).build();
        when(allPatients.findByIdAndClinicId("patient_id", patient.getClinic_id())).thenReturn(patient);
        when(allVitalStatistics.findByPatientId("patient_id")).thenReturn(null);
        when(allTreatmentAdvices.currentTreatmentAdvice("patient_id")).thenReturn(null);
        when(allLabResults.findByPatientId("patient_id")).thenReturn(new LabResults());

        String returnPage = controller.show("patient_id", uiModel, request);

        assertEquals("patients/show", returnPage);

        verify(uiModel).addAttribute(PatientController.DATE_OF_BIRTH_FORMAT, DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
        verify(uiModel).addAttribute(PatientController.PATIENT, patient);
        verify(uiModel).addAttribute(PatientController.ITEM_ID, "patient_id");
        verify(uiModel).addAttribute(PatientController.DEACTIVATION_STATUSES, Status.deactivationStatuses());
        verify(uiModel).addAttribute(PatientController.WARNING, "The Vital Statistics, Regimen details, Lab Results needs to be filled for the patient");
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
        patient.setPatientPreferences(new PatientPreferences() {{
            setCallPreference(CallPreference.DailyPillReminder);
            setBestCallTime(new TimeOfDay());
        }});
        when(allPatients.get("patient_id")).thenReturn(patient);

        String nextPage = controller.deactivate("patient_id", Status.Patient_Withdraws_Consent, request);

        assertEquals(Status.Patient_Withdraws_Consent, patient.getStatus());
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
        when(patientFromUI.getPatientPreferences()).thenReturn(new PatientPreferences() {{
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
    public void shouldUpdatePatient() {
        Patient patientFromUI = mock(Patient.class);
        when(patientFromUI.getPatientPreferences()).thenReturn(new PatientPreferences() {{
            setCallPreference(CallPreference.DailyPillReminder);
            setBestCallTime(new TimeOfDay());
        }});
        when(patientFromUI.getId()).thenReturn("123");
        BindingResult bindingResult = mock(BindingResult.class);
        Map<String, Object> modelMap = new HashMap<String, Object>();
        modelMap.put("dummyKey", "dummyValue");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(uiModel.asMap()).thenReturn(modelMap);
        when(allPatients.get("123")).thenReturn(patientFromUI);

        String updatePage = controller.update(patientFromUI, bindingResult, uiModel, request);

        assertEquals("redirect:/patients/123", updatePage);
        verify(patientService).update(patientFromUI);
    }

    @Test
    public void shouldReactivatePatient_AndDoseNotTaken() {
        String patientId = "id";
        Patient patientFromUI = PatientBuilder.startRecording().withDefaults().withId(patientId).withCallPreference(CallPreference.DailyPillReminder).withLastSuspendedDate(DateUtil.now().minusDays(2)).build();

        when(allPatients.get(patientId)).thenReturn(patientFromUI);
        controller.reactivatePatient(patientId, DoseStatus.NOT_TAKEN, request);
        ArgumentCaptor<DateTime> dateTimeArgumentCaptor = ArgumentCaptor.forClass(DateTime.class);
        verify(dailyPillReminderAdherenceService, times(1)).backFillAdherence(eq(patientId), eq(false), eq(patientFromUI.getLastSuspendedDate()), dateTimeArgumentCaptor.capture());
        assertTimeIsNow(dateTimeArgumentCaptor.getValue());
    }

    private void assertTimeIsNow(DateTime endTime) {
        assertTrue(DateUtil.now().isAfter(endTime));
        assertTrue(DateUtil.now().minusSeconds(1).isBefore(endTime));
    }
}
