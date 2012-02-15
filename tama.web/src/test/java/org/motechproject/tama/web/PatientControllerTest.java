package org.motechproject.tama.web;

import org.ektorp.UpdateConflictException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.service.ClinicVisitService;
import org.motechproject.tama.clinicvisits.service.TAMAAppointmentsService;
import org.motechproject.tama.common.TamaException;
import org.motechproject.tama.common.domain.TimeOfDay;
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Patient.class, Gender.class})
public class PatientControllerTest {

    public static final String PATIENT_ID = "patient_id";
    public static final String CLINIC_ID = "456";
    public static final String CLINIC_VISIT_ID = "clinicVisitId";
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
    @Mock
    private TAMAAppointmentsService tamaAppointmentsService;
    @Mock
    private ClinicVisitService clinicVisitService;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new PatientController(allPatients, allClinics, allGenders, allIVRLanguages, allTestReasons, allModesOfTransmission, allTreatmentAdvices, allVitalStatistics, allLabResults, patientService, dailyPillReminderAdherenceService, resumeFourDayRecallService, tamaAppointmentsService, 28, clinicVisitService);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
    }

    @Test
    public void shouldRenderShowPage() {
        when(request.getSession()).thenReturn(session);
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).withStatus(Status.Active).build();
        when(allPatients.findByIdAndClinicId(PATIENT_ID, patient.getClinic_id())).thenReturn(patient);
        when(allVitalStatistics.findLatestVitalStatisticByPatientId(PATIENT_ID)).thenReturn(null);
        when(allTreatmentAdvices.currentTreatmentAdvice(PATIENT_ID)).thenReturn(null);
        when(allLabResults.findLatestLabResultsByPatientId(PATIENT_ID)).thenReturn(new LabResults());

        String returnPage = controller.show(PATIENT_ID, uiModel, request);

        assertEquals("patients/show", returnPage);

        verify(uiModel).addAttribute(PatientController.DATE_OF_BIRTH_FORMAT, DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
        verify(uiModel).addAttribute(PatientController.PATIENT, patient);
        verify(uiModel).addAttribute(PatientController.ITEM_ID, PATIENT_ID);
        verify(uiModel).addAttribute(PatientController.DEACTIVATION_STATUSES, Status.deactivationStatuses());
        verify(uiModel).addAttribute(PatientController.WARNING, "The Vital Statistics, Regimen details, Lab Results need to be filled so that the patient can access Symptoms Reporting and Health Tips");
    }

    @Test
    public void shouldReturnAuthorizationFailureView_WhenPatientDoesNotBelongToClinic_ForShowAction() {
        when(request.getSession()).thenReturn(session);
        when(user.getClinicId()).thenReturn(CLINIC_ID);
        when(allPatients.findByIdAndClinicId(PATIENT_ID, CLINIC_ID)).thenReturn(null);

        assertEquals("authorizationFailure", controller.show(PATIENT_ID, uiModel, request));
        verify(allPatients).findByIdAndClinicId(PATIENT_ID, CLINIC_ID);
    }

    @Test
    public void shouldActivatePatientsAndRedirectToPatientViewPage() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).
                withCallPreference(CallPreference.DailyPillReminder).
                withActivationDate(DateUtil.now().minusDays(3)).build();

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        doNothing().when(tamaAppointmentsService).scheduleAppointments(PATIENT_ID);
        String nextPage = controller.activate(PATIENT_ID, request);

        verify(patientService).activate(PATIENT_ID);
        verify(tamaAppointmentsService, never()).scheduleAppointments(eq(PATIENT_ID));
        assertTrue(nextPage.contains("redirect:/patients/" + PATIENT_ID));
    }

    @Test
    public void shouldRedirectToClinicVisitPageAfterFirstActivation() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build();
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().withId(CLINIC_VISIT_ID).build();
        when(clinicVisitService.baselineVisit(PATIENT_ID)).thenReturn(clinicVisit);
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        doNothing().when(tamaAppointmentsService).scheduleAppointments(PATIENT_ID);
        String nextPage = controller.activateAndRedirectToListPatient(PATIENT_ID, request);

        verify(patientService).activate(PATIENT_ID);
        verify(tamaAppointmentsService).scheduleAppointments(PATIENT_ID);
        assertEquals("redirect:/clinicvisits?form&clinicVisitId=" + CLINIC_VISIT_ID, nextPage);
    }

    @Test
    public void shouldActivatePatientAndRedirectToPatientListPage() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).withCallPreference(CallPreference.DailyPillReminder).
                withActivationDate(DateUtil.now()).build();
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().withId(CLINIC_VISIT_ID).build();
        when(clinicVisitService.baselineVisit(PATIENT_ID)).thenReturn(clinicVisit);
        when(request.getCharacterEncoding()).thenReturn("utf8");
        String nextPage = controller.activateAndRedirectToListPatient(PATIENT_ID, request);

        verify(patientService).activate(PATIENT_ID);
        assertEquals("redirect:/patients", nextPage);
    }

    @Test
    public void shouldDeactivatePatientAndRedirectToPatientViewPage() {
        String id = PATIENT_ID;
        String nextPage = controller.deactivate(id, Status.Patient_Withdraws_Consent, request);

        verify(patientService).deactivate(id, Status.Patient_Withdraws_Consent);
        assertEquals("redirect:/patients/patient_id", nextPage);
    }

    @Test
    public void shouldReturnToTheShowPatientPageIfPatientIsFound() {
        Patient patientFromDb = mock(Patient.class);

        when(allPatients.findByPatientIdAndClinicId(PATIENT_ID, CLINIC_ID)).thenReturn(patientFromDb);
        when(patientFromDb.getId()).thenReturn("couchDbId");
        when(user.getClinicId()).thenReturn(CLINIC_ID);
        when(request.getSession()).thenReturn(session);


        String nextPage = controller.findByPatientId(PATIENT_ID, uiModel, request);

        assertEquals("redirect:/patients/couchDbId", nextPage);
    }

    @Test
    public void shouldReturnToTheSamePageIfPatientIsNotFound_WithOnlyOneQueryParameter() {
        String expectedPreviousPageUrl = "http://localhost:8080/tama/patients";
        String previousPage = expectedPreviousPageUrl + "?patientIdNotFound=1_abcd";

        when(allPatients.findByPatientIdAndClinicId(PATIENT_ID, CLINIC_ID)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn(previousPage);
        when(user.getClinicId()).thenReturn(CLINIC_ID);
        when(request.getSession()).thenReturn(session);


        String nextPage = controller.findByPatientId(PATIENT_ID, uiModel, request);

        verify(uiModel).addAttribute(PatientController.PATIENT_ID, PATIENT_ID);
        assertEquals("redirect:" + expectedPreviousPageUrl, nextPage);
    }

    @Test
    public void shouldReturnToTheSamePageIfPatientIsNotFound_WithMultipleQueryParameters() {
        String expectedPreviousPageUrl = "http://localhost:8080/tama/patients?page=1";
        String previousPage = expectedPreviousPageUrl + "&patientIdNotFound=abc_8";

        when(allPatients.findByPatientIdAndClinicId(PATIENT_ID, CLINIC_ID)).thenReturn(null);
        when(request.getHeader("Referer")).thenReturn(previousPage);
        when(user.getClinicId()).thenReturn(CLINIC_ID);
        when(request.getSession()).thenReturn(session);


        String nextPage = controller.findByPatientId(PATIENT_ID, uiModel, request);

        verify(uiModel).addAttribute(PatientController.PATIENT_ID, PATIENT_ID);
        assertEquals("redirect:" + expectedPreviousPageUrl, nextPage);
    }

    @Test
    public void shouldCreateAPatientIfThereAreNoErrors() {
        Patient patientFromUI = mock(Patient.class);
        when(patientFromUI.getPatientPreferences()).thenReturn(new PatientPreferences() {{
            setCallPreference(CallPreference.DailyPillReminder);
        }});
        BindingResult bindingResult = mock(BindingResult.class);
        Map<String, Object> modelMap = new HashMap<String, Object>();
        modelMap.put("dummyKey", "dummyValue");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(patientFromUI.getId()).thenReturn(PATIENT_ID);
        when(uiModel.asMap()).thenReturn(modelMap);
        when(user.getClinicId()).thenReturn(CLINIC_ID);
        when(request.getSession()).thenReturn(session);

        String createPage = controller.create(patientFromUI, bindingResult, uiModel, request);

        assertTrue(modelMap.isEmpty());
        assertEquals("redirect:/patients/" + PATIENT_ID, createPage);
    }

    @Test
    public void shouldNotCreateAPatientIfThePatientIdIsNotUniqueWithTheClinic() {
        Patient patientFromUI = mock(Patient.class);
        BindingResult bindingResult = mock(BindingResult.class);
        Map<String, Object> modelMap = new HashMap<String, Object>();
        modelMap.put("dummyKey", "dummyValue");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(patientFromUI.getId()).thenReturn(PATIENT_ID);
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
        when(patientFromUI.getId()).thenReturn(PATIENT_ID);
        BindingResult bindingResult = mock(BindingResult.class);
        Map<String, Object> modelMap = new HashMap<String, Object>();
        modelMap.put("dummyKey", "dummyValue");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(uiModel.asMap()).thenReturn(modelMap);
        when(allPatients.get(PATIENT_ID)).thenReturn(patientFromUI);

        String updatePage = controller.update(patientFromUI, bindingResult, uiModel, request);

        assertEquals("redirect:/patients/" + PATIENT_ID, updatePage);
        verify(patientService).update(patientFromUI);
    }

    @Test
    public void shouldReactivatePatient_AndDoseNotTaken() {
        Patient patientFromUI = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).withCallPreference(CallPreference.DailyPillReminder).withLastSuspendedDate(DateUtil.now().minusDays(2)).build();

        when(allPatients.get(PATIENT_ID)).thenReturn(patientFromUI);
        controller.reactivatePatient(PATIENT_ID, DoseStatus.NOT_TAKEN, request);
        ArgumentCaptor<DateTime> dateTimeArgumentCaptor = ArgumentCaptor.forClass(DateTime.class);
        verify(dailyPillReminderAdherenceService, times(1)).backFillAdherence(eq(PATIENT_ID), eq(patientFromUI.getLastSuspendedDate()), dateTimeArgumentCaptor.capture(), eq(false));
        assertTimeIsNow(dateTimeArgumentCaptor.getValue());
    }


    @Test
    public void shouldReturnAuthorizationFailureView_WhenPatientDoesNotBelongToClinic_ForUpdateFormAction() {
        when(request.getSession()).thenReturn(session);
        when(user.getClinicId()).thenReturn(CLINIC_ID);
        when(allPatients.findByIdAndClinicId(PATIENT_ID, CLINIC_ID)).thenReturn(null);

        assertEquals("authorizationFailure", controller.updateForm(PATIENT_ID, uiModel, request));
        verify(allPatients).findByIdAndClinicId(PATIENT_ID, CLINIC_ID);
    }

    private void assertTimeIsNow(DateTime endTime) {
        assertTrue(DateUtil.now().isAfter(endTime));
        assertTrue(DateUtil.now().minusSeconds(1).isBefore(endTime));
    }
}
