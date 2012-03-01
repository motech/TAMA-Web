package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.ReportedOpportunisticInfections;
import org.motechproject.tama.patient.repository.AllReportedOpportunisticInfections;
import org.motechproject.tama.refdata.domain.OpportunisticInfection;
import org.motechproject.tama.refdata.repository.AllOpportunisticInfections;
import org.motechproject.tama.web.model.OIStatus;
import org.motechproject.tama.web.model.OpportunisticInfectionsUIModel;
import org.motechproject.util.DateUtil;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpportunisticInfectionsControllerTest {

    public static final String PATIENT_ID = "patientId";
    public static final String INFECTION_ID = "infectionId";
    public static final String INFECTION_NAME = "Anemia";
    public static final String REPORT_OI_ID = "reportOIId";
    public static final String CLINIC_VISIT_ID = "clinicvisitid";
    private OpportunisticInfectionsController opportunisticInfectionsController;

    @Mock
    private AllOpportunisticInfections allOpportunisticInfections;

    @Mock
    private AllReportedOpportunisticInfections allReportedOpportunisticInfections;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model uiModel;

    @Mock
    private AllClinicVisits allClinicVisits;

    private OpportunisticInfection opportunisticInfection;
    private Patient patient;
    private Visit visit;
    private ClinicVisit clinicVisit;
    private ReportedOpportunisticInfections reportedOpportunisticInfections;

    @Before
    public void setUp() {
        initMocks(this);
        opportunisticInfectionsController = new OpportunisticInfectionsController(allClinicVisits, allReportedOpportunisticInfections, allOpportunisticInfections);

        opportunisticInfection = new OpportunisticInfection();
        opportunisticInfection.setName(INFECTION_NAME);
        opportunisticInfection.setId(INFECTION_ID);
        when(allOpportunisticInfections.getAll()).thenReturn(Arrays.asList(opportunisticInfection));
        
        patient = new Patient();
        patient.setId(PATIENT_ID);
        visit = new Visit();

        clinicVisit = new ClinicVisit(patient, visit);
        clinicVisit.setId(CLINIC_VISIT_ID);
        clinicVisit.setReportedOpportunisticInfectionsId(REPORT_OI_ID);
        when(allClinicVisits.get(PATIENT_ID, CLINIC_VISIT_ID)).thenReturn(clinicVisit);

        reportedOpportunisticInfections = new ReportedOpportunisticInfections();
        reportedOpportunisticInfections.addOpportunisticInfection(opportunisticInfection);
        when(allReportedOpportunisticInfections.get(REPORT_OI_ID)).thenReturn(reportedOpportunisticInfections);
    }

    @Test
    public void createFormShouldPopulateUIModel() throws Exception {
        opportunisticInfectionsController.createForm(clinicVisit, uiModel);

        ArgumentCaptor<OpportunisticInfectionsUIModel> argumentCaptor = ArgumentCaptor.forClass(OpportunisticInfectionsUIModel.class);
        verify(uiModel).addAttribute(eq(OpportunisticInfectionsController.OPPORTUNISTIC_INFECTIONS_UIMODEL), argumentCaptor.capture());

        assertInfection(argumentCaptor.getValue(), false);
    }

    @Test
    public void shouldCreateReportedOpportunisticInfections() throws Exception {

        opportunisticInfectionsController.create(buildModelWithInfectionReported(true), bindingResult, uiModel);

        ArgumentCaptor<ReportedOpportunisticInfections> argumentCaptor = ArgumentCaptor.forClass(ReportedOpportunisticInfections.class);
        verify(allReportedOpportunisticInfections).add(argumentCaptor.capture());
        ReportedOpportunisticInfections reportedOpportunisticInfections = argumentCaptor.getValue();
        assertEquals(DateUtil.today(), reportedOpportunisticInfections.getCaptureDate());
        assertEquals("patientId", reportedOpportunisticInfections.getPatientId());
        assertEquals("details", reportedOpportunisticInfections.getOtherOpportunisticInfectionDetails());
        assertEquals(1, reportedOpportunisticInfections.getOpportunisticInfectionIds().size());
        assertEquals("infectionId", reportedOpportunisticInfections.getOpportunisticInfectionIds().get(0));
    }
    
    @Test
    public void shouldCreateReportedOpportunisticWithOutOtherDetailsIfNotPresent() throws Exception {

        opportunisticInfectionsController.create(buildModelWithInfectionReported(false), bindingResult, uiModel);

        ArgumentCaptor<ReportedOpportunisticInfections> argumentCaptor = ArgumentCaptor.forClass(ReportedOpportunisticInfections.class);
        verify(allReportedOpportunisticInfections).add(argumentCaptor.capture());
        ReportedOpportunisticInfections reportedOpportunisticInfections = argumentCaptor.getValue();
        assertEquals(DateUtil.today(), reportedOpportunisticInfections.getCaptureDate());
        assertEquals("patientId", reportedOpportunisticInfections.getPatientId());
        assertEquals(1, reportedOpportunisticInfections.getOpportunisticInfectionIds().size());
        assertEquals("infectionId", reportedOpportunisticInfections.getOpportunisticInfectionIds().get(0));
        assertNull(reportedOpportunisticInfections.getOtherOpportunisticInfectionDetails());
    }

    private OpportunisticInfectionsUIModel buildModelWithInfectionReported(boolean otherDetailsPresent) {
        ReportedOpportunisticInfections reportedOpportunisticInfections = new ReportedOpportunisticInfections();
        reportedOpportunisticInfections.addOpportunisticInfection(opportunisticInfection);
        OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = OpportunisticInfectionsUIModel.create(clinicVisit, reportedOpportunisticInfections, allOpportunisticInfections.getAll());

        if(otherDetailsPresent)
            opportunisticInfectionsUIModel.setOtherDetails("details");
        else
            opportunisticInfectionsUIModel.setOtherDetails("");

        return opportunisticInfectionsUIModel;
    }

    @Test
    public void shouldNotCreateReportedOpportunisticInfectionsIfNoneAreReported() throws Exception {
        OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = buildModelWithNoInfectionsReported();

        opportunisticInfectionsController.create(opportunisticInfectionsUIModel, bindingResult, uiModel);

        verify(allReportedOpportunisticInfections, never()).add(Matchers.<ReportedOpportunisticInfections>any());
    }

    private OpportunisticInfectionsUIModel buildModelWithNoInfectionsReported() {
        return OpportunisticInfectionsUIModel.newDefault(clinicVisit, allOpportunisticInfections.getAll());
    }

    @Test
    public void shouldNotSaveOpportunisticInfections_WhenFormHasErrors() {
        OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = new OpportunisticInfectionsUIModel();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        Model uiModel = new ExtendedModelMap();

        opportunisticInfectionsController.create(opportunisticInfectionsUIModel, bindingResult, uiModel);

        assertEquals(opportunisticInfectionsUIModel, uiModel.asMap().get(OpportunisticInfectionsController.OPPORTUNISTIC_INFECTIONS_UIMODEL));
        verifyZeroInteractions(allOpportunisticInfections);
    }

    @Test
    public void showFormShouldPopulateUIModel() throws Exception {
        opportunisticInfectionsController.show(clinicVisit, uiModel);

        ArgumentCaptor<OpportunisticInfectionsUIModel> argumentCaptor = ArgumentCaptor.forClass(OpportunisticInfectionsUIModel.class);
        
        verify(uiModel).addAttribute(eq(OpportunisticInfectionsController.OPPORTUNISTIC_INFECTIONS_UIMODEL), argumentCaptor.capture());

        assertInfection(argumentCaptor.getValue(), true);
    }

    @Test
    public void updateFormShouldPopulateUIModel_WhenInfectionIsReported() throws Exception {
        opportunisticInfectionsController.updateForm(PATIENT_ID, CLINIC_VISIT_ID, uiModel);

        ArgumentCaptor<OpportunisticInfectionsUIModel> argumentCaptor = ArgumentCaptor.forClass(OpportunisticInfectionsUIModel.class);
        verify(uiModel).addAttribute(eq(OpportunisticInfectionsController.OPPORTUNISTIC_INFECTIONS_UIMODEL), argumentCaptor.capture());

        assertInfection(argumentCaptor.getValue(), true);
    }

    @Test
    public void updateFormShouldPopulateUIModel_WhenNoInfectionIsReported() throws Exception {
        ClinicVisit clinicVisitWithNoInfectionReported = new ClinicVisit(patient, visit);
        clinicVisitWithNoInfectionReported.setId("cvId");
        clinicVisitWithNoInfectionReported.setReportedOpportunisticInfectionsId("reportId");
        when(allClinicVisits.get(PATIENT_ID, "cvId")).thenReturn(clinicVisitWithNoInfectionReported);
        when(allReportedOpportunisticInfections.get("reportId")).thenReturn(new ReportedOpportunisticInfections());

        opportunisticInfectionsController.updateForm(PATIENT_ID, CLINIC_VISIT_ID, uiModel);

        ArgumentCaptor<OpportunisticInfectionsUIModel> argumentCaptor = ArgumentCaptor.forClass(OpportunisticInfectionsUIModel.class);
        verify(uiModel).addAttribute(eq(OpportunisticInfectionsController.OPPORTUNISTIC_INFECTIONS_UIMODEL), argumentCaptor.capture());

        assertInfection(argumentCaptor.getValue(), false);
    }

    private void assertInfection(OpportunisticInfectionsUIModel opportunisticInfectionsUIModel, boolean isReported) {
        assertEquals(PATIENT_ID, opportunisticInfectionsUIModel.getPatientId());
        assertEquals(allOpportunisticInfections.getAll().size(), opportunisticInfectionsUIModel.getInfections().size());
        OIStatus firstOpportunisticInfection = opportunisticInfectionsUIModel.getInfections().get(0);
        assertEquals(opportunisticInfection.getName(), firstOpportunisticInfection.getOpportunisticInfection());
        assertEquals(isReported, firstOpportunisticInfection.getReported());
    }

    @Test
    public void updateShouldRemoveOldData() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = OpportunisticInfectionsUIModel.newDefault(clinicVisit, allOpportunisticInfections.getAll());
        
        opportunisticInfectionsController.update(opportunisticInfectionsUIModel, httpServletRequest);
        
        verify(allReportedOpportunisticInfections).remove(reportedOpportunisticInfections);
    }
    
    @Test
    public void updateShouldNotAddAnyDataIfNewDataIsEmpty() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        
        ReportedOpportunisticInfections updatedOIData = new ReportedOpportunisticInfections();
        updatedOIData.setPatientId(PATIENT_ID);
        
        OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = OpportunisticInfectionsUIModel.create(clinicVisit, updatedOIData, allOpportunisticInfections.getAll());
        
        opportunisticInfectionsController.update(opportunisticInfectionsUIModel, httpServletRequest);

        verify(allReportedOpportunisticInfections, never()).add(Matchers.<ReportedOpportunisticInfections>any());

        verify(allClinicVisits).updateOpportunisticInfections(PATIENT_ID, CLINIC_VISIT_ID, null);
    }

    @Test
    public void updateShouldAddNewData() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        OpportunisticInfection anemia = OpportunisticInfection.newOpportunisticInfection("Anemia");
        anemia.setId("anemia");
        ArrayList<OpportunisticInfection> infections = new ArrayList<OpportunisticInfection>();
        infections.add(anemia);
        infections.add(opportunisticInfection);
        when(allOpportunisticInfections.getAll()).thenReturn(infections);

        ReportedOpportunisticInfections updatedOIData = new ReportedOpportunisticInfections();
        updatedOIData.setPatientId(PATIENT_ID);
        updatedOIData.addOpportunisticInfection(anemia);

        OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = OpportunisticInfectionsUIModel.create(clinicVisit, updatedOIData, allOpportunisticInfections.getAll());
        
        opportunisticInfectionsController.update(opportunisticInfectionsUIModel, httpServletRequest);

        ArgumentCaptor<ReportedOpportunisticInfections> argumentCaptor = ArgumentCaptor.forClass(ReportedOpportunisticInfections.class);
        verify(allReportedOpportunisticInfections).add(argumentCaptor.capture());

        ReportedOpportunisticInfections opportunisticInfections = argumentCaptor.getValue();
        assertEquals(1, opportunisticInfections.getOpportunisticInfectionIds().size());
        assertEquals("anemia", opportunisticInfections.getOpportunisticInfectionIds().get(0));
        assertEquals(PATIENT_ID, opportunisticInfections.getPatientId());

        verify(allClinicVisits).updateOpportunisticInfections(eq(PATIENT_ID), eq(CLINIC_VISIT_ID), Matchers.<String>any());
    }
    

}
