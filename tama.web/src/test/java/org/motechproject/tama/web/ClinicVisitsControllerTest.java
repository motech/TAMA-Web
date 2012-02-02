package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.ClinicVisitBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.ClinicVisit;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.ClinicVisitService;
import org.motechproject.tama.web.model.LabResultsUIModel;
import org.motechproject.util.DateUtil;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicVisitsControllerTest {

    @Mock
    private TreatmentAdviceController treatmentAdviceController;
    @Mock
    private LabResultsController labResultsController;
    @Mock
    private VitalStatisticsController vitalStatisticsController;
    @Mock
    private HttpServletRequest request;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private ClinicVisitService clinicVisitService;

    private Model uiModel;
    private ClinicVisitsController controller;

    @Before
    public void setUp() {
        initMocks(this);
        uiModel = new ExtendedModelMap();
        controller = new ClinicVisitsController(treatmentAdviceController, allTreatmentAdvices, labResultsController, vitalStatisticsController, clinicVisitService);
    }

    @Test
    public void shouldRedirectToShowClinicVisits_WhenPatientHasATreatmentAdvice() {
        String patientId = "patientId";
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").build();

        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(treatmentAdvice);
        String redirectURL = controller.createForm(patientId, uiModel, request);

        assertEquals("redirect:/clinicvisits/" + patientId, redirectURL);
    }

    @Test
    public void shouldRedirectToCreateClinicVisits_WhenPatientDoesNotHaveATreatmentAdvice() {
        String patientId = "patientId";

        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(null);
        String redirectURL = controller.createForm(patientId, uiModel, request);

        assertEquals("clinicvisits/create", redirectURL);
        verify(treatmentAdviceController).createForm(patientId, uiModel);
        verify(labResultsController).createForm(patientId, uiModel);
        verify(vitalStatisticsController).createForm(patientId, uiModel);
    }

    @Test
    public void shouldCreateNewClinicVisitsFormGivenAPatientWithNoTreatmentAdvice() {
        String patientId = "patientId";

        when(allTreatmentAdvices.currentTreatmentAdvice(patientId)).thenReturn(null);
        String redirectURL = controller.createForm(patientId, uiModel, request);

        assertEquals("clinicvisits/create", redirectURL);
    }

    @Test
    public void shouldShowClinicVisitForm() {
        final String patientId = "patientId";
        final String clinicVisitId = "clinicVisitId";
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().withId(clinicVisitId).build();
        when(clinicVisitService.visitZero(patientId)).thenReturn(clinicVisit);

        final String showUrl = controller.show(patientId, uiModel);

        assertEquals("clinicvisits/show", showUrl);
        verify(treatmentAdviceController).show(clinicVisit.getTreatmentAdviceId(), uiModel);
        verify(labResultsController).show(patientId, clinicVisit.getId(), clinicVisit.getLabResultIds(), uiModel);
        verify(vitalStatisticsController).show(clinicVisit.getVitalStatisticsId(), uiModel);
    }

    @Test
    public void shouldCreateClinicVisit() {
        BindingResult bindingResult = mock(BindingResult.class);
        final TreatmentAdvice treatmentAdvice = new TreatmentAdvice() {{
            setPatientId("patientId");
        }};
        final LabResultsUIModel labResultsUIModel = new LabResultsUIModel();
        final VitalStatistics vitalStatistics = new VitalStatistics();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(treatmentAdviceController.create(bindingResult, uiModel, treatmentAdvice)).thenReturn("treatmentAdviceId");
        when(labResultsController.create(labResultsUIModel, bindingResult, uiModel)).thenReturn(new ArrayList<String>() {{
            add("labResultId");
        }});
        when(vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel)).thenReturn("vitalStatisticsId");
        ClinicVisit clinicVisit = new ClinicVisit();
        final DateTime visitDate = DateUtil.now();
        clinicVisit.setVisitDate(visitDate);
        String redirectURL = controller.create(clinicVisit,treatmentAdvice, labResultsUIModel, vitalStatistics, bindingResult, uiModel, request);

        assertEquals("redirect:/clinicvisits/patientId", redirectURL);
        verify(treatmentAdviceController).create(bindingResult, uiModel, treatmentAdvice);
        verify(labResultsController).create(labResultsUIModel, bindingResult, uiModel);
        verify(vitalStatisticsController).create(vitalStatistics, bindingResult, uiModel);
        verify(clinicVisitService).createVisit(visitDate, "patientId", "treatmentAdviceId", Arrays.asList("labResultId"), "vitalStatisticsId");
    }
}
