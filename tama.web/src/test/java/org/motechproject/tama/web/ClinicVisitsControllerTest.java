package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisits;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.refdata.domain.Gender;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.web.model.ClinicVisitUIModel;
import org.motechproject.tama.web.model.LabResultsUIModel;
import org.motechproject.tama.web.model.OpportunisticInfectionsUIModel;
import org.motechproject.tama.web.service.PatientDetailsService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@PrepareForTest({Patient.class, Gender.class})
@RunWith(value = Suite.class)
@Suite.SuiteClasses({
        ClinicVisitsControllerTest.CreateForm.WhenPatientHasTreatmentAdvice.class,
        ClinicVisitsControllerTest.CreateForm.WhenPatientDoesNotHaveTreatmentAdvice.class,
        ClinicVisitsControllerTest.Create.class,
        ClinicVisitsControllerTest.Show.class,
        ClinicVisitsControllerTest.ListAction.class,
        ClinicVisitsControllerTest.ConfirmVisitDate.class,
        ClinicVisitsControllerTest.AdjustDueDate.class,
        ClinicVisitsControllerTest.MarkAsMissed.class,
        ClinicVisitsControllerTest.NewVisit.class,
        ClinicVisitsControllerTest.NewAppointment.class
})
public class ClinicVisitsControllerTest {

    public static class SubjectUnderTest extends BaseUnitTest {

        public static final String PATIENT_ID = "patientId";
        public static final String VISIT_ID = "clinicVisitId";
        static final String USER_NAME = "userName";

        @Mock
        protected TreatmentAdviceController treatmentAdviceController;
        @Mock
        protected LabResultsController labResultsController;
        @Mock
        protected VitalStatisticsController vitalStatisticsController;
        @Mock
        protected OpportunisticInfectionsController opportunisticInfectionsController;
        @Mock
        protected AllTreatmentAdvices allTreatmentAdvices;
        @Mock
        protected AllVitalStatistics allVitalStatistics;
        @Mock
        protected AllLabResults allLabResults;
        @Mock
        protected AllClinicVisits allClinicVisits;
        @Mock
        protected Model uiModel;
        @Mock
        protected PatientService patientService;
        @Mock
        AuthenticatedUser user;
        @Mock
        HttpServletRequest request;
        @Mock
        HttpSession session;
        @Mock
        private PatientDetailsService patientDetailsService;

        protected ClinicVisitsController clinicVisitsController;
        protected DateTime now;

        @Before
        public void setUp() {
            initMocks(this);
            now = DateUtil.now();
            mockCurrentDate(now);
            clinicVisitsController = new ClinicVisitsController(treatmentAdviceController, allTreatmentAdvices, allVitalStatistics, allLabResults, labResultsController, vitalStatisticsController, opportunisticInfectionsController, allClinicVisits, patientService, patientDetailsService);
            when(allVitalStatistics.findLatestVitalStatisticByPatientId(PATIENT_ID)).thenReturn(null);
            when(allTreatmentAdvices.currentTreatmentAdvice(PATIENT_ID)).thenReturn(null);
            when(allLabResults.allLabResults(PATIENT_ID)).thenReturn(new LabResults());
            when(user.getUsername()).thenReturn(USER_NAME);
            when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
            when(request.getSession()).thenReturn(session);
        }
    }

    public static class CreateForm extends SubjectUnderTest {

        public static class WhenPatientHasTreatmentAdvice extends CreateForm {
            @Test
            public void shouldRedirectToShowClinicVisits_WhenVisitDateIsSet() {
                TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").build();
                String clinicVisitId = "clinicVisitId";

                ClinicVisit clinicVisit = mock(ClinicVisit.class);
                when(clinicVisit.getTreatmentAdviceId()).thenReturn(treatmentAdvice.getId());
                when(clinicVisit.getVisitDate()).thenReturn(DateUtil.now());

                when(allClinicVisits.get(PATIENT_ID, clinicVisitId)).thenReturn(clinicVisit);
                when(allTreatmentAdvices.get(treatmentAdvice.getId())).thenReturn(treatmentAdvice);

                String redirectURL = clinicVisitsController.createForm(PATIENT_ID, clinicVisitId, uiModel, request);

                assertEquals("redirect:/clinicvisits/" + clinicVisitId + "?patientId=" + PATIENT_ID, redirectURL);
                verify(treatmentAdviceController).show(treatmentAdvice.getId(), uiModel);
            }

            @Test
            public void shouldRedirectToCreateClinicVisits_WhenVisitDetailsWasNotEdited() {
                TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").build();
                String clinicVisitId = "clinicVisitId";

                ClinicVisit clinicVisit = mock(ClinicVisit.class);
                Patient patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).withStatus(Status.Active).build();
                when(clinicVisit.getTreatmentAdviceId()).thenReturn(null);
                when(clinicVisit.getVisitDate()).thenReturn(null);
                when(clinicVisit.getPatient()).thenReturn(patient);


                when(allClinicVisits.get(PATIENT_ID, clinicVisitId)).thenReturn(clinicVisit);
                when(allTreatmentAdvices.get(treatmentAdvice.getId())).thenReturn(null);
                when(allTreatmentAdvices.currentTreatmentAdvice(PATIENT_ID)).thenReturn(treatmentAdvice);

                String redirectURL = clinicVisitsController.createForm(PATIENT_ID, clinicVisitId, uiModel, request);

                assertEquals("clinicvisits/create", redirectURL);
                verify(treatmentAdviceController).show(treatmentAdvice.getId(), uiModel);
                verify(labResultsController).createForm(PATIENT_ID, uiModel);
                verify(opportunisticInfectionsController).createForm(clinicVisit, uiModel);
                verify(vitalStatisticsController).createForm(PATIENT_ID, uiModel);
            }

        }

        public static class WhenPatientDoesNotHaveTreatmentAdvice extends CreateForm {
            @Test
            public void shouldRedirectToCreateClinicVisits() {
                ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withTreatmentAdviceId("treatmentAdviceId").build();
                when(allClinicVisits.get(anyString(), anyString())).thenReturn(clinicVisit);
                when(allTreatmentAdvices.get("treatmentAdviceId")).thenReturn(null);
                when(allTreatmentAdvices.currentTreatmentAdvice(PATIENT_ID)).thenReturn(null);

                String redirectURL = clinicVisitsController.createForm(PATIENT_ID, "clinicVisitId", uiModel, request);

                assertEquals("clinicvisits/create", redirectURL);
                verify(treatmentAdviceController).createForm(PATIENT_ID, uiModel);
                verify(labResultsController).createForm(PATIENT_ID, uiModel);
                verify(vitalStatisticsController).createForm(PATIENT_ID, uiModel);
            }
        }
    }

    public static class Create extends SubjectUnderTest {

        private VitalStatistics vitalStatistics;
        private BindingResult bindingResult;
        private LabResultsUIModel labResultsUIModel;
        private OpportunisticInfectionsUIModel opportunisticInfectionsUIModel;
        private TreatmentAdvice treatmentAdvice;
        private ClinicVisitUIModel clinicVisitUIModel;
        private DateTime visitDate;

        @Before
        public void setUp() {
            super.setUp();
            bindingResult = mock(BindingResult.class);
            visitDate = DateUtil.now();
            treatmentAdvice = new TreatmentAdvice() {{
                setRegimenId("regimenId");
                setPatientId(PATIENT_ID);
                setId("treatmentAdviceId");
            }};
            labResultsUIModel = new LabResultsUIModel();
            opportunisticInfectionsUIModel = new OpportunisticInfectionsUIModel();
            opportunisticInfectionsUIModel.setPatientId(PATIENT_ID);
            vitalStatistics = new VitalStatistics();
            ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().withVisitDate(visitDate).build();
            clinicVisitUIModel = new ClinicVisitUIModel(clinicVisit);
        }

        @Test
        public void shouldCreateClinicVisit_AndRedirectToShowClinicVisitPage() {
            when(bindingResult.hasErrors()).thenReturn(false);
            when(treatmentAdviceController.create(bindingResult, uiModel, treatmentAdvice, USER_NAME)).thenReturn("treatmentAdviceId");
            when(labResultsController.create(labResultsUIModel, bindingResult, uiModel, request)).thenReturn(new ArrayList<String>() {{
                add("labResultId");
            }});
            when(vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel, request)).thenReturn("vitalStatisticsId");
            when(opportunisticInfectionsController.create(opportunisticInfectionsUIModel, bindingResult, uiModel, request)).thenReturn("opportunisticInfectionsId");
            when(allClinicVisits.updateVisitDetails(null, visitDate, "patientId", "treatmentAdviceId", Arrays.asList("labResultId"), "vitalStatisticsId", "opportunisticInfectionsId", USER_NAME)).thenReturn(VISIT_ID);

            String redirectURL = clinicVisitsController.create(VISIT_ID, clinicVisitUIModel, treatmentAdvice, labResultsUIModel, vitalStatistics, opportunisticInfectionsUIModel, bindingResult, uiModel, request);

            assertEquals("redirect:/clinicvisits/" + VISIT_ID + "?patientId=" + PATIENT_ID, redirectURL);
            verify(treatmentAdviceController).create(bindingResult, uiModel, treatmentAdvice, USER_NAME);
            verify(labResultsController).create(labResultsUIModel, bindingResult, uiModel, request);
            verify(vitalStatisticsController).create(vitalStatistics, bindingResult, uiModel, request);
            verify(opportunisticInfectionsController).create(opportunisticInfectionsUIModel, bindingResult, uiModel, request);
            verify(allClinicVisits).updateVisitDetails(VISIT_ID, visitDate, "patientId", "treatmentAdviceId", Arrays.asList("labResultId"), "vitalStatisticsId", "opportunisticInfectionsId", USER_NAME);
        }

        @Test
        public void shouldRedirectToCreateForm_WhenTreatmentAdviceCreateErrorsOut() {
            when(bindingResult.hasErrors()).thenReturn(false);
            doThrow(new RuntimeException("Some Error")).when(treatmentAdviceController).create(bindingResult, uiModel, treatmentAdvice, USER_NAME);

            String redirectURL = clinicVisitsController.create(VISIT_ID, clinicVisitUIModel, treatmentAdvice, labResultsUIModel, vitalStatistics, opportunisticInfectionsUIModel, bindingResult, uiModel, request);

            assertEquals("redirect:/clinicvisits?form&patientId=" + PATIENT_ID + "&clinicVisitId=" + VISIT_ID, redirectURL);
            verify(labResultsController, never()).create(labResultsUIModel, bindingResult, uiModel, request);
            verify(vitalStatisticsController, never()).create(vitalStatistics, bindingResult, uiModel, request);
            verify(allClinicVisits, never()).updateVisitDetails(anyString(), Matchers.<DateTime>any(), anyString(), anyString(), anyList(), anyString(), eq("opportunisticInfectionsId"), eq(USER_NAME));
        }
    }

    public static class Show extends SubjectUnderTest {

        @Test
        public void shouldShowClinicVisitForm() {
            final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
            when(allClinicVisits.get(anyString(), anyString())).thenReturn(clinicVisit);

            final String showUrl = clinicVisitsController.show(clinicVisit.getId(), PATIENT_ID, uiModel);

            assertEquals("clinicvisits/show", showUrl);
            verify(treatmentAdviceController).show(clinicVisit.getTreatmentAdviceId(), uiModel);
            verify(labResultsController).show(PATIENT_ID, clinicVisit.getId(), clinicVisit.getLabResultIds(), uiModel);
            verify(vitalStatisticsController).show(clinicVisit.getVitalStatisticsId(), uiModel);
        }
    }

    public static class ListAction extends SubjectUnderTest {
        @Test
        public void shouldListAllClinicVisitsForPatient() {
            Model uiModel = mock(Model.class);
            DateTime visit1Date = DateUtil.now().plusDays(1);
            DateTime visit2Date = DateUtil.now().plusDays(2);
            final ClinicVisit visit1 = ClinicVisitBuilder.startRecording().withDefaults().withId("visit1").withAppointmentDueDate(visit1Date).withAppointmentAdjustedDate(visit1Date).build();
            final ClinicVisit visit2 = ClinicVisitBuilder.startRecording().withDefaults().withId("visit2").withAppointmentDueDate(visit2Date).withAppointmentAdjustedDate(visit2Date).build();

            visit1.getPatient().setStatus(Status.Active);

            when(allClinicVisits.clinicVisits(PATIENT_ID)).thenReturn(new ClinicVisits() {{
                add(visit1);
                add(visit2);
            }});

            final String listUrl = clinicVisitsController.list(PATIENT_ID, uiModel);

            assertEquals("clinicvisits/manage_list", listUrl);
            ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
            verify(uiModel).addAttribute(eq("clinicVisits"), listArgumentCaptor.capture());
            assertEquals(visit1.getId(), ((ClinicVisitUIModel) listArgumentCaptor.getValue().get(0)).getId());
            assertEquals(visit2.getId(), ((ClinicVisitUIModel) listArgumentCaptor.getValue().get(1)).getId());
        }

        @Test
        public void shouldShowReadOnlyListWhenPatientIsNotActive() {
            Patient inactivePatient = PatientBuilder
                    .startRecording()
                    .withDefaults()
                    .withStatus(Status.Inactive)
                    .withId(PATIENT_ID)
                    .build();

            ClinicVisits clinicVisits = new ClinicVisits();
            clinicVisits.add(new ClinicVisit(inactivePatient, null));

            when(allClinicVisits.clinicVisits(PATIENT_ID)).thenReturn(clinicVisits);
            assertEquals("clinicvisits/view_list", clinicVisitsController.list(PATIENT_ID, uiModel));
        }
    }

    public static class DownloadList extends SubjectUnderTest {

        @Test
        public void shouldCreateExcelDocumentWithClinicVisitsAndPatientReport() {
            String patientDocId = "patientDocId";
            HttpServletResponse response = mock(HttpServletResponse.class);
            PatientReport patientReport = mock(PatientReport.class);

            when(allClinicVisits.clinicVisits(patientDocId)).thenReturn(new ClinicVisits());
            when(patientService.getPatientReport(patientDocId)).thenReturn(patientReport);

            clinicVisitsController.downloadList(patientDocId, response);

            verify(allClinicVisits).clinicVisits(patientDocId);
            verify(patientService).getPatientReport(patientDocId);
        }
    }

    public static class ConfirmVisitDate extends SubjectUnderTest {
        @Test
        public void shouldUpdateConfirmVisitDate() throws Exception {
            final DateTime now = DateUtil.now();

            String jsonReturned = clinicVisitsController.confirmVisitDate(PATIENT_ID, VISIT_ID, now, request);

            verify(allClinicVisits).confirmAppointmentDate(PATIENT_ID, VISIT_ID, now, USER_NAME);
            assertTrue(new JSONObject(jsonReturned).has("confirmedAppointmentDate"));
        }
    }

    public static class AdjustDueDate extends SubjectUnderTest {
        @Test
        public void shouldUpdateAdjustedDueDate() throws Exception {
            LocalDate today = DateUtil.today();
            String jsonReturned = clinicVisitsController.adjustDueDate(PATIENT_ID, VISIT_ID, today, request);
            verify(allClinicVisits).adjustDueDate(PATIENT_ID, VISIT_ID, today, USER_NAME);
            assertTrue(new JSONObject(jsonReturned).has("adjustedDueDate"));
        }
    }

    public static class MarkAsMissed extends SubjectUnderTest {
        @Test
        public void shouldMarkClinicVisitAsMissed() throws Exception {
            String jsonReturned = clinicVisitsController.markAsMissed(PATIENT_ID, VISIT_ID, request);
            verify(allClinicVisits).markAsMissed(PATIENT_ID, VISIT_ID, USER_NAME);
            assertTrue(new JSONObject(jsonReturned).has("missed"));
        }
    }

    public static class NewVisit extends SubjectUnderTest {

        @Before
        public void setup() {
            super.setUp();
            ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withTreatmentAdviceId("treatmentAdviceId").build();
            when(allClinicVisits.get(eq(PATIENT_ID), anyString())).thenReturn(clinicVisit);
        }

        @Test
        public void shouldCreateVisit() {
            clinicVisitsController.newVisit(PATIENT_ID, uiModel, request);
            verify(allClinicVisits).createUnscheduledVisit(PATIENT_ID, now, TypeOfVisit.Unscheduled, USER_NAME);
        }

        @Test
        public void shouldRedirectToCreate() {
            String responseURL = clinicVisitsController.newVisit(PATIENT_ID, uiModel, request);
            assertEquals("clinicvisits/create", responseURL);
        }
    }

    public static class NewAppointment extends SubjectUnderTest {

        @Before
        public void setup() {
            super.setUp();
        }

        @Test
        public void shouldSetDueDateWhenCreatingAppointment() {
            ArgumentCaptor<DateTime> argumentCaptor = ArgumentCaptor.forClass(DateTime.class);

            clinicVisitsController.createAppointment(PATIENT_ID, now.plusDays(1), TypeOfVisit.Unscheduled.name(), request);
            clinicVisitsController.createAppointment(PATIENT_ID, now, TypeOfVisit.Unscheduled.name(), request);

            verify(allClinicVisits, times(2)).createUnScheduledAppointment(eq(PATIENT_ID), argumentCaptor.capture(), same(TypeOfVisit.Unscheduled), eq(USER_NAME));
            assertNotSame(argumentCaptor.getAllValues().get(0), argumentCaptor.getAllValues().get(1));
        }

        @Test
        public void shouldSetTypeOfVisitWhenCreatingAppointment() {
            ArgumentCaptor<TypeOfVisit> typeCaptor = ArgumentCaptor.forClass(TypeOfVisit.class);

            clinicVisitsController.createAppointment(PATIENT_ID, now, TypeOfVisit.Unscheduled.name(), request);
            clinicVisitsController.createAppointment(PATIENT_ID, now, TypeOfVisit.Scheduled.name(), request);

            verify(allClinicVisits, times(2)).createUnScheduledAppointment(eq(PATIENT_ID), eq(now), typeCaptor.capture(), eq(USER_NAME));
            assertNotSame(typeCaptor.getAllValues().get(0), typeCaptor.getAllValues().get(1));
        }

        @Test
        public void shouldReturnSuccessCodeAfterCreatingAppointment() {
            String successCode = "{'result':'success'}";
            assertEquals(successCode, clinicVisitsController.createAppointment(PATIENT_ID,
                    now, TypeOfVisit.Unscheduled.name(), request));
        }
    }
}
