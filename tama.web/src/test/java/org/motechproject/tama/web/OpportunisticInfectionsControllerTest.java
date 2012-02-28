package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.domain.OpportunisticInfections;
import org.motechproject.tama.patient.repository.AllOpportunisticInfections;
import org.motechproject.tama.web.model.OpportunisticInfectionsUIModel;
import org.motechproject.util.DateUtil;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpportunisticInfectionsControllerTest {

    public static final String PATIENT_ID = "patientId";
    private OpportunisticInfectionsController opportunisticInfectionsController;

    @Mock
    private AllOpportunisticInfections allOpportunisticInfections;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model uiModel;
    @Mock
    private AllClinicVisits allClinicVisits;

    private OpportunisticInfections opportunisticInfections;
    private OpportunisticInfectionsUIModel opportunisticInfectionsUIModel;

    @Before
    public void setUp() {
        initMocks(this);
        opportunisticInfectionsController = new OpportunisticInfectionsController(allClinicVisits, allOpportunisticInfections);
        opportunisticInfections = new OpportunisticInfections(PATIENT_ID);
        opportunisticInfectionsUIModel = new OpportunisticInfectionsUIModel(PATIENT_ID);
        opportunisticInfectionsUIModel.setOpportunisticInfections(opportunisticInfections);
    }

    @Test
    public void createFormShouldPopulateUIModel() throws Exception {
        opportunisticInfectionsController.createForm(PATIENT_ID, uiModel);

        ArgumentCaptor<OpportunisticInfectionsUIModel> argumentCaptor = ArgumentCaptor.forClass(OpportunisticInfectionsUIModel.class);
        verify(uiModel).addAttribute(eq(OpportunisticInfectionsController.OPPORTUNISTIC_INFECTIONS_UIMODEL), argumentCaptor.capture());
        assertEquals(PATIENT_ID, argumentCaptor.getValue().getPatientId());

    }

    @Test
    public void createShouldAddOpportunisticInfections() throws Exception {
        opportunisticInfectionsController.create(opportunisticInfectionsUIModel, bindingResult, uiModel);

        verify(allOpportunisticInfections).add(opportunisticInfections);
        assertEquals(DateUtil.today(), opportunisticInfections.getCaptureDate());

    }

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

        when(allOpportunisticInfections.get(opportunisticInfectionId)).thenReturn(new OpportunisticInfections());

        final String returnUrl = opportunisticInfectionsController.update(opportunisticInfectionsUIModel, httpServletRequest);

        ArgumentCaptor<OpportunisticInfections> opportunisticInfectionsArgumentCaptor = ArgumentCaptor.forClass(OpportunisticInfections.class);
        verify(allOpportunisticInfections, times(1)).update(opportunisticInfectionsArgumentCaptor.capture());
        assertEquals(DateUtil.today(), opportunisticInfectionsArgumentCaptor.getValue().getCaptureDate());
        assertEquals(PATIENT_ID, opportunisticInfectionsArgumentCaptor.getValue().getPatientId());
        assertTrue(opportunisticInfectionsArgumentCaptor.getValue().getAnemia());
        assertTrue(opportunisticInfectionsArgumentCaptor.getValue().getAddisonsDisease());
        verify(allClinicVisits, times(1)).updateOpportunisticInfections(eq(PATIENT_ID), eq(clinicVisit.getId()), eq("opportunisticInfectionId"));
        assertEquals("redirect:/clinicvisits/" + clinicVisit.getId() + "?patientId=" + PATIENT_ID, returnUrl);
    }

}
