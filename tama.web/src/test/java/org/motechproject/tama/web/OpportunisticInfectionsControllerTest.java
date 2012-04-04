package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.ReportedOpportunisticInfections;
import org.motechproject.tama.patient.repository.AllReportedOpportunisticInfections;
import org.motechproject.tama.refdata.domain.OpportunisticInfection;
import org.motechproject.tama.refdata.objectcache.AllOpportunisticInfectionsCache;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.web.model.OIStatus;
import org.motechproject.tama.web.model.OpportunisticInfectionsUIModel;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(value = Suite.class)
@Suite.SuiteClasses({
        OpportunisticInfectionsControllerTest.CreateForm.class,
        OpportunisticInfectionsControllerTest.Create.class,
        OpportunisticInfectionsControllerTest.Show.class,
        OpportunisticInfectionsControllerTest.UpdateForm.class,
        OpportunisticInfectionsControllerTest.Update.class
})
public class OpportunisticInfectionsControllerTest {

    public static class SubjectUnderTest extends BaseUnitTest {

        public static final String PATIENT_ID = "patientId";
        public static final String USER_NAME = "userName";
        public static final String REPORT_OI_ID = "reportOIId";
        public static final String CLINIC_VISIT_ID = "clinicvisitid";
        public static final String ANEMIA = "Anemia";
        public static final String INFECTION_ID = "infectionId";

        @Mock
        protected AllOpportunisticInfectionsCache allOpportunisticInfections;
        @Mock
        protected AllReportedOpportunisticInfections allReportedOpportunisticInfections;
        @Mock
        HttpSession session;
        @Mock
        protected HttpServletRequest request;
        @Mock
        protected BindingResult bindingResult;
        @Mock
        protected Model uiModel;
        @Mock
        protected AllClinicVisits allClinicVisits;
        @Mock
        AuthenticatedUser user;

        protected OpportunisticInfectionsController opportunisticInfectionsController;

        protected OpportunisticInfection opportunisticInfection;
        protected OpportunisticInfection otherOpportunisticInfection;
        protected Patient patient;
        protected VisitResponse visit;
        protected ClinicVisit clinicVisit;
        protected ReportedOpportunisticInfections reportedOpportunisticInfections;

        @Before
        public void setUp() {
            initMocks(this);
            opportunisticInfectionsController = new OpportunisticInfectionsController(allClinicVisits, allReportedOpportunisticInfections, allOpportunisticInfections);

            opportunisticInfection = new OpportunisticInfection();
            opportunisticInfection.setName(ANEMIA);
            opportunisticInfection.setId(INFECTION_ID);

            otherOpportunisticInfection = new OpportunisticInfection();
            otherOpportunisticInfection.setName("Other");
            otherOpportunisticInfection.setId("otherInfectionId");

            ArrayList<OpportunisticInfection> opportunisticInfections = new ArrayList<OpportunisticInfection>();
            opportunisticInfections.add(opportunisticInfection);
            opportunisticInfections.add(otherOpportunisticInfection);
            when(allOpportunisticInfections.getAll()).thenReturn(opportunisticInfections);

            patient = new Patient();
            patient.setId(PATIENT_ID);
            visit = new VisitResponse();

            visit.setName(CLINIC_VISIT_ID).addVisitData(ClinicVisit.REPORTED_OPPORTUNISTIC_INFECTIONS, REPORT_OI_ID);
            clinicVisit = new ClinicVisit(patient, visit);
            when(allClinicVisits.get(PATIENT_ID, CLINIC_VISIT_ID)).thenReturn(clinicVisit);

            reportedOpportunisticInfections = new ReportedOpportunisticInfections();
            reportedOpportunisticInfections.addOpportunisticInfection(opportunisticInfection);
            when(allReportedOpportunisticInfections.get(REPORT_OI_ID)).thenReturn(reportedOpportunisticInfections);
            when(user.getUsername()).thenReturn(USER_NAME);
            when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
            when(request.getSession()).thenReturn(session);
        }

        protected void assertInfection(OpportunisticInfectionsUIModel opportunisticInfectionsUIModel, boolean isReported) {
            assertEquals(PATIENT_ID, opportunisticInfectionsUIModel.getPatientId());
            assertEquals(allOpportunisticInfections.getAll().size(), opportunisticInfectionsUIModel.getInfections().size());
            OIStatus firstOpportunisticInfection = opportunisticInfectionsUIModel.getInfections().get(0);
            assertEquals(opportunisticInfection.getName(), firstOpportunisticInfection.getOpportunisticInfection());
            assertEquals(isReported, firstOpportunisticInfection.getReported());
        }

        protected OpportunisticInfectionsUIModel buildModelWithInfectionReported(boolean otherDetailsPresent) {
            ReportedOpportunisticInfections reportedOpportunisticInfections = new ReportedOpportunisticInfections();
            reportedOpportunisticInfections.addOpportunisticInfection(opportunisticInfection);
            if (otherDetailsPresent)
                reportedOpportunisticInfections.addOpportunisticInfection(otherOpportunisticInfection);
            OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = OpportunisticInfectionsUIModel.create(clinicVisit, reportedOpportunisticInfections, allOpportunisticInfections.getAll());

            if (otherDetailsPresent)
                opportunisticInfectionsUIModel.setOtherDetails("otherDetails");
            else
                opportunisticInfectionsUIModel.setOtherDetails("");

            return opportunisticInfectionsUIModel;
        }

        protected OpportunisticInfectionsUIModel buildModelWithNoInfectionsReported() {
            return OpportunisticInfectionsUIModel.newDefault(clinicVisit, allOpportunisticInfections.getAll());
        }

    }

    public static class CreateForm extends SubjectUnderTest {

        @Test
        public void shouldPopulateUIModel() throws Exception {
            opportunisticInfectionsController.createForm(clinicVisit, uiModel);

            ArgumentCaptor<OpportunisticInfectionsUIModel> argumentCaptor = ArgumentCaptor.forClass(OpportunisticInfectionsUIModel.class);
            verify(uiModel).addAttribute(eq(OpportunisticInfectionsController.OPPORTUNISTIC_INFECTIONS_UIMODEL), argumentCaptor.capture());

            assertInfection(argumentCaptor.getValue(), false);
        }

    }

    public static class Create extends SubjectUnderTest {

        @Test
        public void shouldCreateReportedOpportunisticInfections() throws Exception {

            opportunisticInfectionsController.create(buildModelWithInfectionReported(true), bindingResult, uiModel, request);

            ArgumentCaptor<ReportedOpportunisticInfections> argumentCaptor = ArgumentCaptor.forClass(ReportedOpportunisticInfections.class);
            verify(allReportedOpportunisticInfections).add(argumentCaptor.capture(), eq(USER_NAME));
            ReportedOpportunisticInfections reportedOpportunisticInfections = argumentCaptor.getValue();
            assertEquals(DateUtil.today(), reportedOpportunisticInfections.getCaptureDate());
            assertEquals("patientId", reportedOpportunisticInfections.getPatientId());
            assertEquals(2, reportedOpportunisticInfections.getOpportunisticInfectionIds().size());
            assertEquals(INFECTION_ID, reportedOpportunisticInfections.getOpportunisticInfectionIds().get(0));
            assertEquals("otherInfectionId", reportedOpportunisticInfections.getOpportunisticInfectionIds().get(1));
            assertEquals("otherDetails", reportedOpportunisticInfections.getOtherOpportunisticInfectionDetails());
        }

        @Test
        public void shouldCreateReportedOpportunisticWithOutOtherDetailsIfNotPresent() throws Exception {

            opportunisticInfectionsController.create(buildModelWithInfectionReported(false), bindingResult, uiModel, request);

            ArgumentCaptor<ReportedOpportunisticInfections> argumentCaptor = ArgumentCaptor.forClass(ReportedOpportunisticInfections.class);
            verify(allReportedOpportunisticInfections).add(argumentCaptor.capture(), eq(USER_NAME));
            ReportedOpportunisticInfections reportedOpportunisticInfections = argumentCaptor.getValue();
            assertEquals(DateUtil.today(), reportedOpportunisticInfections.getCaptureDate());
            assertEquals("patientId", reportedOpportunisticInfections.getPatientId());
            assertEquals(1, reportedOpportunisticInfections.getOpportunisticInfectionIds().size());
            assertEquals(INFECTION_ID, reportedOpportunisticInfections.getOpportunisticInfectionIds().get(0));
            assertNull(reportedOpportunisticInfections.getOtherOpportunisticInfectionDetails());
        }

        @Test
        public void shouldNotCreateReportedOpportunisticInfectionsIfNoneAreReported() throws Exception {
            OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = buildModelWithNoInfectionsReported();

            opportunisticInfectionsController.create(opportunisticInfectionsUIModel, bindingResult, uiModel, request);

            verify(allReportedOpportunisticInfections, never()).add(Matchers.<ReportedOpportunisticInfections>any(), eq(USER_NAME));
        }

        @Test
        public void shouldNotSaveOpportunisticInfections_WhenFormHasErrors() {
            OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = new OpportunisticInfectionsUIModel();
            BindingResult bindingResult = mock(BindingResult.class);
            when(bindingResult.hasErrors()).thenReturn(true);
            Model uiModel = new ExtendedModelMap();

            opportunisticInfectionsController.create(opportunisticInfectionsUIModel, bindingResult, uiModel, request);

            assertEquals(opportunisticInfectionsUIModel, uiModel.asMap().get(OpportunisticInfectionsController.OPPORTUNISTIC_INFECTIONS_UIMODEL));
            verifyZeroInteractions(allOpportunisticInfections);
        }

        @Test
        public void shouldSetFlashErrorMessage_WhenCreateFails() {
            doThrow(new RuntimeException("Some error")).when(allReportedOpportunisticInfections).add(Matchers.<ReportedOpportunisticInfections>any(), eq(USER_NAME));
            String id = opportunisticInfectionsController.create(buildModelWithInfectionReported(true), bindingResult, uiModel, request);

            verify(request).setAttribute("flash.flashErrorOpportunisticInfections", "Error occurred while creating Opportunistic Infections: Some error");
            assertNull(id);
        }
    }

    public static class Show extends SubjectUnderTest {

        @Test
        public void shouldPopulateUIModel() throws Exception {
            opportunisticInfectionsController.show(clinicVisit, uiModel);

            ArgumentCaptor<OpportunisticInfectionsUIModel> argumentCaptor = ArgumentCaptor.forClass(OpportunisticInfectionsUIModel.class);

            verify(uiModel).addAttribute(eq(OpportunisticInfectionsController.OPPORTUNISTIC_INFECTIONS_UIMODEL), argumentCaptor.capture());

            assertInfection(argumentCaptor.getValue(), true);
        }
    }

    public static class UpdateForm extends SubjectUnderTest {

        @Test
        public void shouldPopulateUIModel_WhenInfectionIsReported() throws Exception {
            opportunisticInfectionsController.updateForm(PATIENT_ID, CLINIC_VISIT_ID, uiModel);

            ArgumentCaptor<OpportunisticInfectionsUIModel> argumentCaptor = ArgumentCaptor.forClass(OpportunisticInfectionsUIModel.class);
            verify(uiModel).addAttribute(eq(OpportunisticInfectionsController.OPPORTUNISTIC_INFECTIONS_UIMODEL), argumentCaptor.capture());

            assertInfection(argumentCaptor.getValue(), true);
        }

        @Test
        public void shouldPopulateUIModel_WhenNoInfectionIsReported() throws Exception {
            visit.setName("cvId").addVisitData(ClinicVisit.REPORTED_OPPORTUNISTIC_INFECTIONS, "reportId");
            ClinicVisit clinicVisitWithNoInfectionReported = new ClinicVisit(patient, visit);
            when(allClinicVisits.get(PATIENT_ID, "cvId")).thenReturn(clinicVisitWithNoInfectionReported);
            when(allReportedOpportunisticInfections.get("reportId")).thenReturn(new ReportedOpportunisticInfections());

            opportunisticInfectionsController.updateForm(PATIENT_ID, CLINIC_VISIT_ID, uiModel);

            ArgumentCaptor<OpportunisticInfectionsUIModel> argumentCaptor = ArgumentCaptor.forClass(OpportunisticInfectionsUIModel.class);
            verify(uiModel).addAttribute(eq(OpportunisticInfectionsController.OPPORTUNISTIC_INFECTIONS_UIMODEL), argumentCaptor.capture());

            assertInfection(argumentCaptor.getValue(), false);
        }
    }

    public static class Update extends SubjectUnderTest {

        @Test
        public void shouldRemoveOldData() {
            OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = OpportunisticInfectionsUIModel.newDefault(clinicVisit, allOpportunisticInfections.getAll());

            opportunisticInfectionsController.update(opportunisticInfectionsUIModel, request);

            verify(allReportedOpportunisticInfections).remove(reportedOpportunisticInfections, USER_NAME);
        }

        @Test
        public void shouldNotAddAnyDataIfNewDataIsEmpty() {

            ReportedOpportunisticInfections updatedOIData = new ReportedOpportunisticInfections();
            updatedOIData.setPatientId(PATIENT_ID);

            OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = OpportunisticInfectionsUIModel.create(clinicVisit, updatedOIData, allOpportunisticInfections.getAll());

            opportunisticInfectionsController.update(opportunisticInfectionsUIModel, request);

            verify(allReportedOpportunisticInfections, never()).add(Matchers.<ReportedOpportunisticInfections>any(), eq(USER_NAME));

            verify(allClinicVisits).updateOpportunisticInfections(PATIENT_ID, CLINIC_VISIT_ID, null);
        }

        @Test
        public void shouldAddNewData() {

            OpportunisticInfection anemia = OpportunisticInfection.newOpportunisticInfection("Hypertension");
            anemia.setId("hypertensionInfectionId");
            ArrayList<OpportunisticInfection> infections = new ArrayList<OpportunisticInfection>();
            infections.add(anemia);
            infections.add(opportunisticInfection);
            when(allOpportunisticInfections.getAll()).thenReturn(infections);

            ReportedOpportunisticInfections updatedOIData = new ReportedOpportunisticInfections();
            updatedOIData.setPatientId(PATIENT_ID);
            updatedOIData.addOpportunisticInfection(anemia);

            OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = OpportunisticInfectionsUIModel.create(clinicVisit, updatedOIData, allOpportunisticInfections.getAll());

            opportunisticInfectionsController.update(opportunisticInfectionsUIModel, request);

            ArgumentCaptor<ReportedOpportunisticInfections> argumentCaptor = ArgumentCaptor.forClass(ReportedOpportunisticInfections.class);
            verify(allReportedOpportunisticInfections).add(argumentCaptor.capture(), eq(USER_NAME));

            ReportedOpportunisticInfections opportunisticInfections = argumentCaptor.getValue();
            assertEquals(1, opportunisticInfections.getOpportunisticInfectionIds().size());
            assertEquals("hypertensionInfectionId", opportunisticInfections.getOpportunisticInfectionIds().get(0));
            assertEquals(PATIENT_ID, opportunisticInfections.getPatientId());

            verify(allClinicVisits).updateOpportunisticInfections(eq(PATIENT_ID), eq(CLINIC_VISIT_ID), Matchers.<String>any());
        }

        @Test
        public void shouldNotStoreOtherDetailsWhenOtherInfectionIsNotReported() throws Exception {

            OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = buildModelWithInfectionReported(false);
            opportunisticInfectionsUIModel.setOtherDetails("someDetails");
            opportunisticInfectionsController.update(opportunisticInfectionsUIModel, request);

            ArgumentCaptor<ReportedOpportunisticInfections> argumentCaptor = ArgumentCaptor.forClass(ReportedOpportunisticInfections.class);
            verify(allReportedOpportunisticInfections).add(argumentCaptor.capture(), eq(USER_NAME));
            ReportedOpportunisticInfections reportedOpportunisticInfections = argumentCaptor.getValue();
            assertEquals(DateUtil.today(), reportedOpportunisticInfections.getCaptureDate());
            assertEquals("patientId", reportedOpportunisticInfections.getPatientId());
            assertEquals(1, reportedOpportunisticInfections.getOpportunisticInfectionIds().size());
            assertEquals(INFECTION_ID, reportedOpportunisticInfections.getOpportunisticInfectionIds().get(0));
            assertNull(reportedOpportunisticInfections.getOtherOpportunisticInfectionDetails());
        }
    }

}
