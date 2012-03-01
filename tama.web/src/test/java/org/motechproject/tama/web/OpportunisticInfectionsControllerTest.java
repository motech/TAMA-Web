package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.domain.ReportedOpportunisticInfections;
import org.motechproject.tama.patient.repository.AllReportedOpportunisticInfections;
import org.motechproject.tama.refdata.domain.OpportunisticInfection;
import org.motechproject.tama.refdata.repository.AllOpportunisticInfections;
import org.motechproject.tama.web.model.OIStatus;
import org.motechproject.tama.web.model.OpportunisticInfectionsUIModel;
import org.motechproject.util.DateUtil;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpportunisticInfectionsControllerTest {

    public static final String PATIENT_ID = "patientId";
    public static final String INFECTION_ID = "infectionId";
    public static final String INFECTION_NAME = "Anemia";
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

    @Before
    public void setUp() {
        initMocks(this);
        opportunisticInfectionsController = new OpportunisticInfectionsController(allClinicVisits, allReportedOpportunisticInfections, allOpportunisticInfections);

        opportunisticInfection = new OpportunisticInfection();
        opportunisticInfection.setName(INFECTION_NAME);
        opportunisticInfection.setId(INFECTION_ID);
        when(allOpportunisticInfections.getAll()).thenReturn(Arrays.asList(opportunisticInfection));

    }

    @Test
    public void createFormShouldPopulateUIModel() throws Exception {
        opportunisticInfectionsController.createForm(PATIENT_ID, uiModel);

        ArgumentCaptor<OpportunisticInfectionsUIModel> argumentCaptor = ArgumentCaptor.forClass(OpportunisticInfectionsUIModel.class);
        verify(uiModel).addAttribute(eq(OpportunisticInfectionsController.OPPORTUNISTIC_INFECTIONS_UIMODEL), argumentCaptor.capture());

        OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = argumentCaptor.getValue();
        assertEquals(PATIENT_ID, opportunisticInfectionsUIModel.getPatientId());
        assertEquals(allOpportunisticInfections.getAll().size(), opportunisticInfectionsUIModel.getInfections().size());
        OIStatus firstOpportunisticInfection = opportunisticInfectionsUIModel.getInfections().get(0);
        assertEquals(opportunisticInfection.getName(), firstOpportunisticInfection.getOpportunisticInfection());
        assertFalse(firstOpportunisticInfection.getReported());
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
        OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = new OpportunisticInfectionsUIModel();
        opportunisticInfectionsUIModel.setPatientId("patientId");

        OIStatus oiStatus = new OIStatus();
        oiStatus.setOpportunisticInfection("Anemia");
        oiStatus.setReported(true);
        opportunisticInfectionsUIModel.setInfections(Arrays.asList(oiStatus));

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
        OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = new OpportunisticInfectionsUIModel();
        opportunisticInfectionsUIModel.setPatientId("patientId");

        OIStatus opportunisticInfectionUIModel = new OIStatus();
        opportunisticInfectionUIModel.setOpportunisticInfection(opportunisticInfection.getName());
        opportunisticInfectionUIModel.setReported(false);
        opportunisticInfectionsUIModel.setInfections(Arrays.asList(opportunisticInfectionUIModel));

        return opportunisticInfectionsUIModel;
    }

     /*
    @Test
    public void shouldNotSaveOpportunisticInfections_WhenFormHasErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        Model uiModel = new ExtendedModelMap();

        opportunisticInfectionsController.create(opportunisticInfectionsUIModel, bindingResult, uiModel);

        assertEquals(opportunisticInfectionsUIModel, uiModel.asMap().get(OpportunisticInfectionsController.OPPORTUNISTIC_INFECTIONS_UIMODEL));
        verifyZeroInteractions(allOpportunisticInfections);
    }

    @Test
    public void shouldUpdateOpportunisticInfections_WhenClinicVisitHasOne() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        final String opportunisticInfectionId = "opportunisticInfectionId";
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
        final OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = OpportunisticInfectionsUIModel.get(clinicVisit, opportunisticInfections);

        opportunisticInfections.setId(opportunisticInfectionId);
        opportunisticInfections.setAnemia(true);
        opportunisticInfections.setAddisonsDisease(true);

        when(allOpportunisticInfections.get(opportunisticInfectionId)).thenReturn(new ReportedOpportunisticInfections());

        final String returnUrl = opportunisticInfectionsController.update(opportunisticInfectionsUIModel, httpServletRequest);

        ArgumentCaptor<ReportedOpportunisticInfections> opportunisticInfectionsArgumentCaptor = ArgumentCaptor.forClass(ReportedOpportunisticInfections.class);
        verify(allOpportunisticInfections, times(1)).update(opportunisticInfectionsArgumentCaptor.capture());
        assertEquals(DateUtil.today(), opportunisticInfectionsArgumentCaptor.getValue().getCaptureDate());
        assertEquals(PATIENT_ID, opportunisticInfectionsArgumentCaptor.getValue().getPatientId());
        assertTrue(opportunisticInfectionsArgumentCaptor.getValue().getAnemia());
        assertTrue(opportunisticInfectionsArgumentCaptor.getValue().getAddisonsDisease());
        verify(allClinicVisits, times(1)).updateOpportunisticInfections(eq(PATIENT_ID), eq(clinicVisit.getId()), eq("opportunisticInfectionId"));
        assertEquals("redirect:/clinicvisits/" + clinicVisit.getId() + "?patientId=" + PATIENT_ID, returnUrl);
    }*/

}
