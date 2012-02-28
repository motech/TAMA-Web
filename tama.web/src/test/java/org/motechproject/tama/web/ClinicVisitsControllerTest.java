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
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.refdata.domain.Gender;
import org.motechproject.tama.web.model.LabResultsUIModel;
import org.motechproject.tama.web.model.OpportunisticInfectionsUIModel;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
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
        ClinicVisitsControllerTest.MarkAsMissed.class
})
public class ClinicVisitsControllerTest {

    public static class SubjectUnderTest {

        public static final String PATIENT_ID = "patientId";
        public static final String VISIT_ID = "clinicVisitId";
        @Mock
        protected TreatmentAdviceController treatmentAdviceController;
        @Mock
        protected LabResultsController labResultsController;
        @Mock
        protected VitalStatisticsController vitalStatisticsController;
        @Mock
        protected OpportunisticInfectionsController opportunisticInfectionsController;
        @Mock
        protected HttpServletRequest request;
        @Mock
        protected AllTreatmentAdvices allTreatmentAdvices;
        @Mock
        protected AllClinicVisits allClinicVisits;
        @Mock
        protected Model uiModel;

        protected ClinicVisitsController clinicVisitsController;

        @Before
        public void setUp() {
            initMocks(this);
            clinicVisitsController = new ClinicVisitsController(treatmentAdviceController, allTreatmentAdvices, labResultsController, vitalStatisticsController, opportunisticInfectionsController, allClinicVisits);
        }
    }

    public static class CreateForm extends SubjectUnderTest {

        public static class WhenPatientHasTreatmentAdvice extends CreateForm {
            @Test
            public void shouldRedirectToShowClinicVisits_WhenVisitDetailsWasEdited() {
                String patientId = "patientId";
                TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").build();
                String clinicVisitId = "clinicVisitId";

                ClinicVisit clinicVisit = mock(ClinicVisit.class);
                when(clinicVisit.getTreatmentAdviceId()).thenReturn(treatmentAdvice.getId());

                when(allClinicVisits.get(patientId, clinicVisitId)).thenReturn(clinicVisit);
                when(allTreatmentAdvices.get(treatmentAdvice.getId())).thenReturn(treatmentAdvice);

                String redirectURL = clinicVisitsController.createForm(patientId, clinicVisitId, uiModel, request);

                assertEquals("redirect:/clinicvisits/" + clinicVisitId + "?patientId=" + patientId, redirectURL);
                verify(treatmentAdviceController).show(treatmentAdvice.getId(), uiModel);
            }

            @Test
            public void shouldRedirectToCreateClinicVisits_WhenVisitDetailsWasNotEdited() {
                String patientId = "patientId";
                TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").build();
                String clinicVisitId = "clinicVisitId";

                ClinicVisit clinicVisit = mock(ClinicVisit.class);
                when(clinicVisit.getTreatmentAdviceId()).thenReturn(null);
                when(clinicVisit.getLabResultIds()).thenReturn(new ArrayList<String>());
                when(clinicVisit.getVitalStatisticsId()).thenReturn(null);

                when(allClinicVisits.get(patientId, clinicVisitId)).thenReturn(clinicVisit);
                when(allTreatmentAdvices.get(treatmentAdvice.getId())).thenReturn(null);
                when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);

                String redirectURL = clinicVisitsController.createForm(patientId, clinicVisitId, uiModel, request);

                assertEquals("clinicvisits/create", redirectURL);
                verify(treatmentAdviceController).show(treatmentAdvice.getId(), uiModel);
                verify(labResultsController).createForm(patientId, uiModel);
                verify(opportunisticInfectionsController).createForm(patientId, uiModel);
                verify(vitalStatisticsController).createForm(patientId, uiModel);
            }
        }

        public static class WhenPatientDoesNotHaveTreatmentAdvice extends CreateForm {
            @Test
            public void shouldRedirectToCreateClinicVisits() {
                String patientId = "patientId";
                ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withTreatmentAdviceId("treatmentAdviceId").build();
                when(allClinicVisits.get(anyString(), anyString())).thenReturn(clinicVisit);
                when(allTreatmentAdvices.get("treatmentAdviceId")).thenReturn(null);
                when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(null);

                String redirectURL = clinicVisitsController.createForm(patientId, "clinicVisitId", uiModel, request);

                assertEquals("clinicvisits/create", redirectURL);
                verify(treatmentAdviceController).createForm(patientId, uiModel);
                verify(labResultsController).createForm(patientId, uiModel);
                verify(vitalStatisticsController).createForm(patientId, uiModel);
            }
        }
    }

    public static class Create extends SubjectUnderTest {

        private VitalStatistics vitalStatistics;
        private BindingResult bindingResult;
        private LabResultsUIModel labResultsUIModel;
        private OpportunisticInfectionsUIModel opportunisticInfectionsUIModel;
        private TreatmentAdvice treatmentAdvice;
        private ClinicVisit clinicVisit;
        private DateTime visitDate;
        private String clinicVisitId;

        @Before
        public void setUp() {
            super.setUp();
            bindingResult = mock(BindingResult.class);
            visitDate = DateUtil.now();
            clinicVisitId = "clinicVisitId";
            treatmentAdvice = new TreatmentAdvice() {{
                setRegimenId("regimenId");
                setPatientId("patientId");
                setId("treatmentAdviceId");
            }};
            labResultsUIModel = new LabResultsUIModel();
            opportunisticInfectionsUIModel = new OpportunisticInfectionsUIModel();
            vitalStatistics = new VitalStatistics();
            clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().withVisitDate(visitDate).build();
        }

        @Test
        public void shouldCreateClinicVisit_AndRedirectToShowClinicVisitPage() {
            when(bindingResult.hasErrors()).thenReturn(false);
            when(treatmentAdviceController.create(bindingResult, uiModel, treatmentAdvice)).thenReturn("treatmentAdviceId");
            when(labResultsController.create(labResultsUIModel, bindingResult, uiModel)).thenReturn(new ArrayList<String>() {{
                add("labResultId");
            }});
            when(vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel)).thenReturn("vitalStatisticsId");
            when(opportunisticInfectionsController.create(opportunisticInfectionsUIModel, bindingResult, uiModel)).thenReturn("opportunisticInfectionsId");
            when(allClinicVisits.updateVisit(null, visitDate, "patientId", "treatmentAdviceId", Arrays.asList("labResultId"), "vitalStatisticsId", "opportunisticInfectionsId")).thenReturn(clinicVisitId);

            String redirectURL = clinicVisitsController.create(clinicVisitId, clinicVisit, treatmentAdvice, labResultsUIModel, vitalStatistics, opportunisticInfectionsUIModel, bindingResult, uiModel, request);

            assertEquals("redirect:/clinicvisits/clinicVisitId?patientId=patientId", redirectURL);
            verify(treatmentAdviceController).create(bindingResult, uiModel, treatmentAdvice);
            verify(labResultsController).create(labResultsUIModel, bindingResult, uiModel);
            verify(vitalStatisticsController).create(vitalStatistics, bindingResult, uiModel);
            verify(opportunisticInfectionsController).create(opportunisticInfectionsUIModel, bindingResult, uiModel);
            verify(allClinicVisits).updateVisit(clinicVisitId, visitDate, "patientId", "treatmentAdviceId", Arrays.asList("labResultId"), "vitalStatisticsId", "opportunisticInfectionsId");
        }

        @Test
        public void shouldRedirectToCreateForm_WhenTreatmentAdviceCreateErrorsOut() {
            when(bindingResult.hasErrors()).thenReturn(false);
            doThrow(new RuntimeException("Some Error")).when(treatmentAdviceController).create(bindingResult, uiModel, treatmentAdvice);

            String redirectURL = clinicVisitsController.create(clinicVisitId, clinicVisit, treatmentAdvice, labResultsUIModel, vitalStatistics, opportunisticInfectionsUIModel, bindingResult, uiModel, request);

            String patientId = treatmentAdvice.getPatientId();
            assertEquals("redirect:/clinicvisits?form&patientId=" + patientId + "&clinicVisitId=" + clinicVisitId, redirectURL);
            verify(labResultsController, never()).create(labResultsUIModel, bindingResult, uiModel);
            verify(vitalStatisticsController, never()).create(vitalStatistics, bindingResult, uiModel);
            verify(allClinicVisits, never()).updateVisit(anyString(), Matchers.<DateTime>any(), anyString(), anyString(), anyList(), anyString(), eq("opportunisticInfectionsId"));
        }

        @Test
        public void shouldRedirectToClinicVisitShowPage_WhenLabResultCreateErrorsOut() {
            String patientId = treatmentAdvice.getPatientId();
            when(bindingResult.hasErrors()).thenReturn(false);
            when(treatmentAdviceController.create(bindingResult, uiModel, treatmentAdvice)).thenReturn(treatmentAdvice.getId());
            doThrow(new RuntimeException("Some Error")).when(labResultsController).create(labResultsUIModel, bindingResult, uiModel);

            String redirectURL = clinicVisitsController.create(clinicVisitId, clinicVisit, treatmentAdvice, labResultsUIModel, vitalStatistics, opportunisticInfectionsUIModel, bindingResult, uiModel, request);

            assertEquals("redirect:/clinicvisits/clinicVisitId?patientId=patientId", redirectURL);
            verify(uiModel).addAttribute("errorLabResults", "Error occurred while creating Lab Results: Some Error");
        }

        @Test
        public void shouldStillPersistTreatmentAdviceInClinicVisit_WhenLabResultCreateErrorsOut() {
            String patientId = treatmentAdvice.getPatientId();
            when(bindingResult.hasErrors()).thenReturn(false);
            when(treatmentAdviceController.create(bindingResult, uiModel, treatmentAdvice)).thenReturn(treatmentAdvice.getId());
            doThrow(new RuntimeException("Some Error")).when(labResultsController).create(labResultsUIModel, bindingResult, uiModel);

            clinicVisitsController.create(clinicVisitId, clinicVisit, treatmentAdvice, labResultsUIModel, vitalStatistics, opportunisticInfectionsUIModel, bindingResult, uiModel, request);

            verify(allClinicVisits).updateVisit(eq(clinicVisitId), Matchers.<DateTime>any(), eq(patientId), eq(treatmentAdvice.getId()), anyList(), Matchers.<String>eq(null), Matchers.<String>eq(null));
        }

        @Test
        public void shouldStillPersistVitalStatistics_whenLabResultCreateErrorsOut() {
            String patientId = treatmentAdvice.getPatientId();
            when(bindingResult.hasErrors()).thenReturn(false);
            when(treatmentAdviceController.create(bindingResult, uiModel, treatmentAdvice)).thenReturn(treatmentAdvice.getId());
            when(vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel)).thenReturn("vitalStatisticsId");
            when(opportunisticInfectionsController.create(opportunisticInfectionsUIModel, bindingResult, uiModel)).thenReturn("opportunisticInfectionsId");
            doThrow(new RuntimeException("Some Error")).when(labResultsController).create(labResultsUIModel, bindingResult, uiModel);

            clinicVisitsController.create(clinicVisitId, clinicVisit, treatmentAdvice, labResultsUIModel, vitalStatistics, opportunisticInfectionsUIModel, bindingResult, uiModel, request);

            verify(vitalStatisticsController).create(vitalStatistics, bindingResult, uiModel);
            verify(allClinicVisits).updateVisit(eq(clinicVisitId), Matchers.<DateTime>any(), eq(patientId), eq(treatmentAdvice.getId()), anyList(), eq("vitalStatisticsId"), eq("opportunisticInfectionsId"));
        }

        @Test
        public void shouldRedirectToClinicVisitShowPage_WhenVitalStatisticsCreateErrorsOut() {
            String patientId = treatmentAdvice.getPatientId();
            List<String> labResultIds = new ArrayList<String>();
            when(bindingResult.hasErrors()).thenReturn(false);
            when(treatmentAdviceController.create(bindingResult, uiModel, treatmentAdvice)).thenReturn(treatmentAdvice.getId());
            when(labResultsController.create(labResultsUIModel, bindingResult, uiModel)).thenReturn(labResultIds);
            doThrow(new RuntimeException("Some Error")).when(vitalStatisticsController).create(vitalStatistics, bindingResult, uiModel);

            String redirectURL = clinicVisitsController.create(clinicVisitId, clinicVisit, treatmentAdvice, labResultsUIModel, vitalStatistics, opportunisticInfectionsUIModel, bindingResult, uiModel, request);

            assertEquals("redirect:/clinicvisits/clinicVisitId?patientId=patientId", redirectURL);
            verify(uiModel).addAttribute("errorVitalStatistics", "Error occurred while creating Vital Statistics: Some Error");
        }

        @Test
        public void shouldStillPersistTreatmentAdviceAndLabResultsInClinicVisit_WhenVitalStatisticsCreateErrorsOut() {
            String patientId = treatmentAdvice.getPatientId();
            List<String> labResultIds = new ArrayList<String>();
            when(bindingResult.hasErrors()).thenReturn(false);
            when(treatmentAdviceController.create(bindingResult, uiModel, treatmentAdvice)).thenReturn(treatmentAdvice.getId());
            when(labResultsController.create(labResultsUIModel, bindingResult, uiModel)).thenReturn(labResultIds);
            when(opportunisticInfectionsController.create(opportunisticInfectionsUIModel, bindingResult, uiModel)).thenReturn("opportunisticInfectionsId");
            doThrow(new RuntimeException("Some Error")).when(vitalStatisticsController).create(vitalStatistics, bindingResult, uiModel);

            clinicVisitsController.create(clinicVisitId, clinicVisit, treatmentAdvice, labResultsUIModel, vitalStatistics, opportunisticInfectionsUIModel, bindingResult, uiModel, request);

            verify(allClinicVisits).updateVisit(eq(clinicVisitId), Matchers.<DateTime>any(), eq(patientId), eq(treatmentAdvice.getId()), eq(labResultIds), Matchers.<String>eq(null), eq("opportunisticInfectionsId"));
        }

        @Test
        public void shouldRedirectToCreateForm_WhenUpdatingClinicVisitErrorsOut() {
            String patientId = treatmentAdvice.getPatientId();
            ArrayList<String> labResultIds = new ArrayList<String>();
            when(bindingResult.hasErrors()).thenReturn(false);
            when(treatmentAdviceController.create(bindingResult, uiModel, treatmentAdvice)).thenReturn(treatmentAdvice.getId());
            when(labResultsController.create(labResultsUIModel, bindingResult, uiModel)).thenReturn(labResultIds);
            when(vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel)).thenReturn("vitalStatisticsId");
            when(opportunisticInfectionsController.create(opportunisticInfectionsUIModel, bindingResult, uiModel)).thenReturn("opportunisticInfectionsId");
            doThrow(new RuntimeException("Some error")).when(allClinicVisits).updateVisit(eq(clinicVisitId), Matchers.<DateTime>any(), eq(patientId),
                    eq(treatmentAdvice.getId()), eq(labResultIds), eq("vitalStatisticsId"), eq("opportunisticInfectionsId"));

            String redirectURL = clinicVisitsController.create(clinicVisitId, clinicVisit, treatmentAdvice, labResultsUIModel, vitalStatistics, opportunisticInfectionsUIModel, bindingResult, uiModel, request);

            assertEquals("redirect:/clinicvisits?form&patientId=" + patientId + "&clinicVisitId=" + clinicVisitId, redirectURL);
            verify(uiModel).addAttribute("error", "Error occurred while creating clinic visit. Please try again: Some error");
        }

    }

    public static class Show extends SubjectUnderTest {

        @Test
        public void shouldShowClinicVisitForm() {
            final String patientId = "patientId";
            final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
            when(allClinicVisits.get(anyString(), anyString())).thenReturn(clinicVisit);

            final String showUrl = clinicVisitsController.show(clinicVisit.getId(), patientId, uiModel);

            assertEquals("clinicvisits/show", showUrl);
            verify(treatmentAdviceController).show(clinicVisit.getTreatmentAdviceId(), uiModel);
            verify(labResultsController).show(patientId, clinicVisit.getId(), clinicVisit.getLabResultIds(), uiModel);
            verify(vitalStatisticsController).show(clinicVisit.getVitalStatisticsId(), uiModel);
        }
    }

    public static class ListAction extends SubjectUnderTest {
        @Test
        public void shouldReturnAllClinicVisitsForPatient() throws Exception {
            Model uiModel = mock(Model.class);
            final ClinicVisit visit1 = ClinicVisitBuilder.startRecording().withDefaults().build();
            final ClinicVisit visit2 = ClinicVisitBuilder.startRecording().withDefaults().build();
            when(allClinicVisits.clinicVisits(PATIENT_ID)).thenReturn(new ClinicVisits() {{
                add(visit1);
                add(visit2);
            }});

            final String listUrl = clinicVisitsController.list(PATIENT_ID, uiModel);

            assertEquals("clinicvisits/list", listUrl);
            ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
            verify(uiModel).addAttribute(eq("clinicVisits"), listArgumentCaptor.capture());
            assertEquals(visit1, listArgumentCaptor.getValue().get(0));
            assertEquals(visit2, listArgumentCaptor.getValue().get(1));
        }
    }

    public static class ConfirmVisitDate extends SubjectUnderTest {
        @Test
        public void shouldUpdateConfirmVisitDate() throws Exception {
            final DateTime now = DateUtil.now();

            String jsonReturned = clinicVisitsController.confirmVisitDate(PATIENT_ID, VISIT_ID, now);

            verify(allClinicVisits).confirmVisitDate(PATIENT_ID, VISIT_ID, now);
            assertTrue(new JSONObject(jsonReturned).has("confirmedVisitDate"));
        }
    }

    public static class AdjustDueDate extends SubjectUnderTest {
        @Test
        public void shouldUpdateAdjustedDueDate() throws Exception {
            LocalDate today = DateUtil.today();
            String jsonReturned = clinicVisitsController.adjustDueDate(PATIENT_ID, VISIT_ID, today);
            verify(allClinicVisits).adjustDueDate(PATIENT_ID, VISIT_ID, today);
            assertTrue(new JSONObject(jsonReturned).has("adjustedDueDate"));
        }
    }

    public static class MarkAsMissed extends SubjectUnderTest {
        @Test
        public void shouldMarkClinicVisitAsMissed() throws Exception {
            String jsonReturned = clinicVisitsController.markAsMissed(PATIENT_ID, VISIT_ID);
            verify(allClinicVisits).markAsMissed(PATIENT_ID, VISIT_ID);
            assertTrue(new JSONObject(jsonReturned).has("missed"));
        }
    }
}
