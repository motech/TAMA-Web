package org.motechproject.tama.web;

import org.ektorp.UpdateConflictException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
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
import org.motechproject.tama.refdata.repository.*;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.web.model.DoseStatus;
import org.motechproject.tama.web.model.PatientSummary;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@PrepareForTest({Patient.class, Gender.class})
@RunWith(value = Suite.class)
@Suite.SuiteClasses({
        PatientControllerTest.Activate.class,
        PatientControllerTest.ActivateAndRedirectToListPatient.class,
        PatientControllerTest.Deactivate.class,
        PatientControllerTest.ReactivatePatient.class,
        PatientControllerTest.Show.class,
        PatientControllerTest.ShowSummary.class,
        PatientControllerTest.Create.class,
        PatientControllerTest.FindByPatientId.class,
        PatientControllerTest.Update.class,
        PatientControllerTest.UpdateForm.class
})
public class PatientControllerTest {

    public static class SubjectUnderTest {
        static final String PATIENT_ID = "patient_id";
        static final String CLINIC_ID = "456";
        PatientController controller;
        @Mock
        Model uiModel;
        @Mock
        AuthenticatedUser user;
        @Mock
        HttpServletRequest request;
        @Mock
        HttpSession session;
        @Mock
        AllPatients allPatients;
        @Mock
        AllClinics allClinics;
        @Mock
        AllGenders allGenders;
        @Mock
        AllIVRLanguages allIVRLanguages;
        @Mock
        AllHIVTestReasons allTestReasons;
        @Mock
        AllModesOfTransmission allModesOfTransmission;
        @Mock
        AllVitalStatistics allVitalStatistics;
        @Mock
        AllTreatmentAdvices allTreatmentAdvices;
        @Mock
        AllLabResults allLabResults;
        @Mock
        AllRegimens allRegimens;
        @Mock
        PatientService patientService;
        @Mock
        DailyPillReminderAdherenceService dailyPillReminderAdherenceService;
        @Mock
        ResumeFourDayRecallService resumeFourDayRecallService;
        @Mock
        AllClinicVisits allClinicVisits;

        @Before
        public void setUp() {
            initMocks(this);
            controller = new PatientController(allPatients, allClinics, allGenders, allIVRLanguages, allTestReasons, allModesOfTransmission, allTreatmentAdvices, allVitalStatistics, allLabResults, allRegimens, patientService, dailyPillReminderAdherenceService, resumeFourDayRecallService, 28, allClinicVisits);
            when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
        }
    }

    public static class Activate extends SubjectUnderTest {
        @Test
        public void shouldActivateAndRedirectToClinicVisitPage_whenActivatedForFirstTime() {
            Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build();
            ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
            when(allClinicVisits.getBaselineVisit(PATIENT_ID)).thenReturn(clinicVisit);
            when(allPatients.get(PATIENT_ID)).thenReturn(patient);
            doNothing().when(allClinicVisits).addAppointmentCalendar(PATIENT_ID);
            String nextPage = controller.activate(PATIENT_ID, uiModel, request);

            verify(patientService).activate(PATIENT_ID);
            verify(allClinicVisits).addAppointmentCalendar(PATIENT_ID);
            assertEquals("redirect:/clinicvisits?form&patientId=patient_id&clinicVisitId=" + clinicVisit.getId(), nextPage);
        }

        @Test
        public void shouldActivatePatientsAndRedirectToPatientViewPage_whenHadBeenActivatedPreviouslyAtLeastOnce() {
            Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).
                    withCallPreference(CallPreference.DailyPillReminder).
                    withActivationDate(DateUtil.now().minusDays(3)).build();

            when(allPatients.get(PATIENT_ID)).thenReturn(patient);
            doNothing().when(allClinicVisits).addAppointmentCalendar(PATIENT_ID);
            String nextPage = controller.activate(PATIENT_ID, uiModel, request);

            verify(patientService).activate(PATIENT_ID);
            verify(allClinicVisits, never()).addAppointmentCalendar(eq(PATIENT_ID));
            assertTrue(nextPage.contains("redirect:/patients/" + PATIENT_ID));
        }

        @Test
        public void shouldRedirectToPatientViewPage_AddErrorMessageToUiModel_onError(){
            Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build();
            ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
            when(allClinicVisits.getBaselineVisit(PATIENT_ID)).thenReturn(clinicVisit);
            when(allPatients.get(PATIENT_ID)).thenReturn(patient);
            doNothing().when(allClinicVisits).addAppointmentCalendar(PATIENT_ID);
            doThrow(new RuntimeException("Some Exception")).when(patientService).activate(PATIENT_ID);

            String nextPage = controller.activate(PATIENT_ID, uiModel, request);

            verify(allClinicVisits).addAppointmentCalendar(PATIENT_ID);
            verify(request).setAttribute("flash.flashError", "Error occurred while activating patient: Some Exception");
            assertTrue(nextPage.contains("redirect:/patients/" + PATIENT_ID));
        }
    }

    public static class ActivateAndRedirectToListPatient extends SubjectUnderTest {
        @Test
        public void shouldActivateAndRedirectToClinicVisitPage_whenActivatedForFirstTime() {
            Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build();
            ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
            when(allClinicVisits.getBaselineVisit(PATIENT_ID)).thenReturn(clinicVisit);
            when(allPatients.get(PATIENT_ID)).thenReturn(patient);
            doNothing().when(allClinicVisits).addAppointmentCalendar(PATIENT_ID);

            String nextPage = controller.activateAndRedirectToListPatient(PATIENT_ID, uiModel, request);

            verify(patientService).activate(PATIENT_ID);
            verify(allClinicVisits).addAppointmentCalendar(PATIENT_ID);
            assertEquals("redirect:/clinicvisits?form&patientId=patient_id&clinicVisitId=" + clinicVisit.getId(), nextPage);
        }

        @Test
        public void shouldActivatePatientAndRedirectToPatientListPage_whenHadBeenActivatedPreviouslyAtLeastOnce() {
            Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).withCallPreference(CallPreference.DailyPillReminder).
                    withActivationDate(DateUtil.now()).build();
            when(allPatients.get(PATIENT_ID)).thenReturn(patient);
            ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
            when(allClinicVisits.getBaselineVisit(PATIENT_ID)).thenReturn(clinicVisit);
            when(request.getCharacterEncoding()).thenReturn("utf8");

            String nextPage = controller.activateAndRedirectToListPatient(PATIENT_ID, uiModel, request);

            verify(patientService).activate(PATIENT_ID);
            assertEquals("redirect:/patients", nextPage);
        }

        @Test
        public void shouldRedirectToPatientListPage_AddErrorMessageToUiModel_onError(){
            Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).build();
            ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
            when(allClinicVisits.getBaselineVisit(PATIENT_ID)).thenReturn(clinicVisit);
            when(allPatients.get(PATIENT_ID)).thenReturn(patient);
            doThrow(new RuntimeException("Some Exception")).when(allClinicVisits).addAppointmentCalendar(PATIENT_ID);

            String nextPage = controller.activateAndRedirectToListPatient(PATIENT_ID, uiModel, request);

            verify(patientService, never()).activate(PATIENT_ID);
            verify(allClinicVisits).addAppointmentCalendar(PATIENT_ID);
            verify(request).setAttribute("flash.flashError", "Error occurred while activating patient: Some Exception");
            assertEquals("redirect:/patients", nextPage);
        }
    }

    public static class Deactivate extends SubjectUnderTest {
        @Test
        public void shouldDeactivatePatientAndRedirectToPatientViewPage() {
            String id = PATIENT_ID;
            String nextPage = controller.deactivate(id, Status.Patient_Withdraws_Consent, uiModel, request);

            verify(patientService).deactivate(id, Status.Patient_Withdraws_Consent);
            assertEquals("redirect:/patients/patient_id", nextPage);
        }

        @Test
        public void shouldRedirectToPatientViewPage_AddErrorMessage_onError() {
            String id = PATIENT_ID;
            doThrow(new RuntimeException("Some exception")).when(patientService).deactivate(id, Status.Patient_Withdraws_Consent);

            String nextPage = controller.deactivate(id, Status.Patient_Withdraws_Consent, uiModel, request);

            assertEquals("redirect:/patients/patient_id", nextPage);
            verify(request).setAttribute("flash.flashError", "Error occured while deactivating patient: Some exception");
        }
    }

    public static class ReactivatePatient extends SubjectUnderTest {
        @Test
        public void shouldBackFill_whenPatientIsOnDailyPillReminder() {
            DateTime now = DateUtil.now();
            Patient patientFromUI = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).withCallPreference(CallPreference.DailyPillReminder).withLastSuspendedDate(now.minusDays(2)).build();
            when(allPatients.get(PATIENT_ID)).thenReturn(patientFromUI);

            controller.reactivatePatient(PATIENT_ID, DoseStatus.NOT_TAKEN, uiModel, request);

            ArgumentCaptor<DateTime> dateTimeArgumentCaptor = ArgumentCaptor.forClass(DateTime.class);
            verify(dailyPillReminderAdherenceService, times(1)).backFillAdherence(eq(PATIENT_ID), eq(patientFromUI.getLastSuspendedDate()), dateTimeArgumentCaptor.capture(), eq(false));
            assertEquals(now.toLocalDate(), dateTimeArgumentCaptor.getValue().toLocalDate());
        }

        @Test
        public void shouldBackFill_whenPatientIsOnFourDayRecall() {
            DateTime now = DateUtil.now();
            Patient patientFromUI = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).withCallPreference(CallPreference.FourDayRecall).withLastSuspendedDate(now.minusDays(2)).build();
            when(allPatients.get(PATIENT_ID)).thenReturn(patientFromUI);

            controller.reactivatePatient(PATIENT_ID, DoseStatus.TAKEN, uiModel, request);

            ArgumentCaptor<DateTime> dateTimeArgumentCaptor = ArgumentCaptor.forClass(DateTime.class);
            verify(resumeFourDayRecallService, times(1)).backFillAdherence(eq(patientFromUI), eq(patientFromUI.getLastSuspendedDate()), dateTimeArgumentCaptor.capture(), eq(true));
            assertEquals(now.toLocalDate(), dateTimeArgumentCaptor.getValue().toLocalDate());
        }

        @Test
        public void shouldActivatePatientAndRedirectToShowPage(){
            Patient patientFromUI = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).withCallPreference(CallPreference.FourDayRecall).withLastSuspendedDate(DateUtil.now().minusDays(2)).build();
            when(allPatients.get(PATIENT_ID)).thenReturn(patientFromUI);

            String nextPage = controller.reactivatePatient(PATIENT_ID, DoseStatus.TAKEN, uiModel, request);

            verify(patientService).activate(PATIENT_ID);
            assertTrue(nextPage.contains("redirect:/patients/" + PATIENT_ID));
        }

        @Test
        public void shouldRedirectToShowPage_addErrorMessage_onErrorWhileActivating() {
            Patient patientFromUI = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).withCallPreference(CallPreference.FourDayRecall).withLastSuspendedDate(DateUtil.now().minusDays(2)).build();
            when(allPatients.get(PATIENT_ID)).thenReturn(patientFromUI);
            doThrow(new RuntimeException("Some error")).when(patientService).activate(PATIENT_ID);

            String nextPage = controller.reactivatePatient(PATIENT_ID, DoseStatus.TAKEN, uiModel, request);

            assertTrue(nextPage.contains("redirect:/patients/" + PATIENT_ID));
            verify(request).setAttribute("flash.flashError", "Error occurred while reactivating patient: Some error");
        }

        @Test
        public void shouldRedirectToShowPage_addErrorMessage_onErrorWhileBackFillingAdherence() {
            Patient patientFromUI = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).withCallPreference(CallPreference.FourDayRecall).withLastSuspendedDate(DateUtil.now().minusDays(2)).build();
            when(allPatients.get(PATIENT_ID)).thenReturn(patientFromUI);
            doThrow(new RuntimeException("Some error")).when(resumeFourDayRecallService).backFillAdherence(eq(patientFromUI), eq(patientFromUI.getLastSuspendedDate()), Matchers.any(DateTime.class), eq(true));

            String nextPage = controller.reactivatePatient(PATIENT_ID, DoseStatus.TAKEN, uiModel, request);

            assertTrue(nextPage.contains("redirect:/patients/" + PATIENT_ID));
            verify(patientService, never()).activate(PATIENT_ID);
            verify(request).setAttribute("flash.flashError", "Error occurred while reactivating patient: Some error");
        }
    }

    public static class Show extends SubjectUnderTest {
        @Test
        public void shouldRenderShowPage_WhenPatientBelongsToClinic() {
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
        public void shouldReturnAuthorizationFailureView_WhenPatientDoesNotBelongToClinic() {
            when(request.getSession()).thenReturn(session);
            when(user.getClinicId()).thenReturn(CLINIC_ID);
            when(allPatients.findByIdAndClinicId(PATIENT_ID, CLINIC_ID)).thenReturn(null);

            assertEquals("authorizationFailure", controller.show(PATIENT_ID, uiModel, request));
            verify(allPatients).findByIdAndClinicId(PATIENT_ID, CLINIC_ID);
        }
    }

    public static class ShowSummary extends SubjectUnderTest {
        @Test
        public void shouldGoToPatientSummaryPage_WhenPatientViewedFromListPatientPage(){
            Patient patient = mock(Patient.class);

            when(request.getSession()).thenReturn(session);
            when(user.getClinicId()).thenReturn(CLINIC_ID);
            when(patient.getId()).thenReturn(PATIENT_ID);
            when(patient.getStatus()).thenReturn(Status.Inactive);
            when(allVitalStatistics.findLatestVitalStatisticByPatientId(PATIENT_ID)).thenReturn(null);
            when(allTreatmentAdvices.currentTreatmentAdvice(PATIENT_ID)).thenReturn(null);
            when(allTreatmentAdvices.earliestTreatmentAdvice(PATIENT_ID)).thenReturn(null);
            when(allLabResults.findLatestLabResultsByPatientId(PATIENT_ID)).thenReturn(new LabResults());
            when(allPatients.findByIdAndClinicId(PATIENT_ID, CLINIC_ID)).thenReturn(patient);

            ModelAndView modelAndView = controller.showSummary(PATIENT_ID, uiModel, request);

            PatientSummary patientSummary = (PatientSummary) modelAndView.getModel().get("patient");
            assertEquals("patients/summary", modelAndView.getViewName());
            assertNotNull(patientSummary);
        }
    }

    public static class Create extends SubjectUnderTest {
        @Test
        public void shouldCreateAPatient_WhenThereAreNoErrors() {
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
        public void shouldNotCreateAPatient_WhenThePatientIdIsNotUniqueWithTheClinic_AndRedirectToCreatePage() {
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
    }

    public static class FindByPatientId extends SubjectUnderTest {
        @Test
        public void shouldReturnToTheShowPatientPage_whenPatientIsFound() {
            Patient patientFromDb = mock(Patient.class);

            when(allPatients.findByPatientIdAndClinicId(PATIENT_ID, CLINIC_ID)).thenReturn(patientFromDb);
            when(patientFromDb.getId()).thenReturn("couchDbId");
            when(user.getClinicId()).thenReturn(CLINIC_ID);
            when(request.getSession()).thenReturn(session);

            String nextPage = controller.findByPatientId(PATIENT_ID, uiModel, request);

            assertEquals("redirect:/patients/summary/couchDbId", nextPage);
        }

        @Test
        public void shouldReturnToTheSamePage_whenPatientIsNotFound_WithOnlyOneQueryParameter() {
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
        public void shouldReturnToTheSamePage_whenPatientIsNotFound_WithMultipleQueryParameters() {
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
    }

    public static class Update extends SubjectUnderTest {
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
    }

    public static class UpdateForm extends SubjectUnderTest {
        @Test
        public void shouldReturnAuthorizationFailureView_WhenPatientDoesNotBelongToClinic_ForUpdateFormAction() {
            when(request.getSession()).thenReturn(session);
            when(user.getClinicId()).thenReturn(CLINIC_ID);
            when(allPatients.findByIdAndClinicId(PATIENT_ID, CLINIC_ID)).thenReturn(null);

            assertEquals("authorizationFailure", controller.updateForm(PATIENT_ID, uiModel, request));
            verify(allPatients).findByIdAndClinicId(PATIENT_ID, CLINIC_ID);
        }
    }
}
