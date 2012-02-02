package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.ClinicVisitBuilder;
import org.motechproject.tama.patient.builder.VitalStatisticsBuilder;
import org.motechproject.tama.patient.domain.ClinicVisit;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllClinicVisits;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.patient.service.ClinicVisitService;
import org.motechproject.tama.web.model.VitalStatisticsUIModel;
import org.motechproject.util.DateUtil;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class VitalStatisticsControllerTest {

    private VitalStatisticsController vitalStatisticsController;
    @Mock
    private AllVitalStatistics allVitalStatistics;
    @Mock
    private AllClinicVisits allClinicVisits;
    @Mock
    private ClinicVisitService clinicVisitService;

    @Before
    public void setUp() {
        initMocks(this);
        vitalStatisticsController = new VitalStatisticsController(allVitalStatistics, allClinicVisits, clinicVisitService);
    }

    @Test
    public void shouldShowFormWhenPatientHasNoVitalStatisticsEntered() {
        Model uiModel = new ExtendedModelMap();

        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patient_id")).thenReturn(null);

        vitalStatisticsController.createForm("patient_id", uiModel);
        assertEquals(VitalStatistics.class, uiModel.asMap().get("vitalStatistics").getClass());
        assertEquals("patient_id", ((VitalStatistics) uiModel.asMap().get("vitalStatistics")).getPatientId());
    }

    @Test
    public void shouldSaveVitalStatistics() {
        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = mock(Model.class);

        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withDefaults().build();
        vitalStatistics.setWeightInKg(200.0);
        vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel);

        verify(allVitalStatistics, times(1)).add(vitalStatistics);
    }

    @Test
    public void shouldNotSaveVitalStatistics_WhenFormHasErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        Model uiModel = new ExtendedModelMap();

        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withDefaults().build();
        vitalStatistics.setWeightInKg(200.0);
        vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel);

        assertEquals(VitalStatistics.class, uiModel.asMap().get("vitalStatistics").getClass());
        assertEquals(vitalStatistics, uiModel.asMap().get("vitalStatistics"));
        verifyZeroInteractions(allVitalStatistics);
    }

    @Test
    public void shouldNotSaveVitalStatistics_WhenNoneOfTheParametersAreSet() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        Model uiModel = new ExtendedModelMap();

        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withDefaults().build();
        vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel);

        verifyZeroInteractions(allVitalStatistics);
    }

    @Test
    public void shouldShowVitalStatistics() {
        Model uiModel = mock(Model.class);
        final String vitalStatisticsId = "vitalStatisticsId";

        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withPatientId("patientId").withWeight(100).build();
        when(allVitalStatistics.get(vitalStatisticsId)).thenReturn(vitalStatistics);

        vitalStatisticsController.show(vitalStatisticsId, uiModel);
        verify(uiModel).addAttribute("vitalStatistics", vitalStatistics);
    }

    @Test
    public void shouldShowEmptyVitalStatisticsIfNotSaved() {
        Model uiModel = mock(Model.class);
        vitalStatisticsController.show("vitalStatisticsId", uiModel);
        verify(uiModel).addAttribute("vitalStatistics", new VitalStatistics());
    }

    @Test
    public void shouldShowFormForUpdatingVitalStatistics() {
        Model uiModel = new ExtendedModelMap();

        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withId("vitalStatisticsId").withPatientId("patient_id").withPulse(100).build();
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
        when(allClinicVisits.get("clinicVisitId")).thenReturn(clinicVisit);
        when(allVitalStatistics.get(vitalStatistics.getId())).thenReturn(vitalStatistics);

        assertEquals("vital_statistics/update", vitalStatisticsController.updateForm("clinicVisitId", uiModel));

        final VitalStatisticsUIModel vitalStatisticsUIModel = (VitalStatisticsUIModel) uiModel.asMap().get("vitalStatisticsUIModel");
        assertEquals(100, vitalStatisticsUIModel.getVitalStatistics().getPulse().intValue());
        assertEquals(vitalStatisticsUIModel.getClinicVisitId(), "clinicVisitId");
        assertEquals(uiModel.asMap().get("_method"), "put");
    }

    @Test
    public void shouldShowEmptyUpdateForm_WhenClinicVisitDoesNotHaveOne() {
        Model uiModel = new ExtendedModelMap();

        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().withVitalStatisticsId(null).build();
        when(allClinicVisits.get("clinicVisitId")).thenReturn(clinicVisit);

        assertEquals("vital_statistics/update", vitalStatisticsController.updateForm("clinicVisitId", uiModel));

        final VitalStatisticsUIModel vitalStatisticsUIModel = ((VitalStatisticsUIModel) uiModel.asMap().get("vitalStatisticsUIModel"));
        assertNull(vitalStatisticsUIModel.getVitalStatistics().getPulse());
        assertNull(vitalStatisticsUIModel.getVitalStatistics().getDiastolicBp());
        assertNull(vitalStatisticsUIModel.getVitalStatistics().getSystolicBp());
        assertNull(vitalStatisticsUIModel.getVitalStatistics().getWeightInKg());
        assertNull(vitalStatisticsUIModel.getVitalStatistics().getHeightInCm());
        assertNull(vitalStatisticsUIModel.getVitalStatistics().getTemperatureInFahrenheit());
        assertEquals(vitalStatisticsUIModel.getClinicVisitId(), "clinicVisitId");
        assertEquals(uiModel.asMap().get("_method"), "put");
    }

    @Test
    public void shouldCreateVitalStatisticsRecord_WhenClinicVisitDoesNotHaveOne() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withPatientId("patient_id").withPulse(100).build();
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().withVitalStatisticsId(null).build();
        final VitalStatisticsUIModel vitalStatisticsUIModel = VitalStatisticsUIModel.get(clinicVisit, vitalStatistics);

        final String returnUrl = vitalStatisticsController.update(vitalStatisticsUIModel, httpServletRequest);

        ArgumentCaptor<VitalStatistics> vitalStatisticsArgumentCaptor = ArgumentCaptor.forClass(VitalStatistics.class);
        verify(allVitalStatistics, times(1)).add(vitalStatisticsArgumentCaptor.capture());
        assertEquals(DateUtil.today(), vitalStatisticsArgumentCaptor.getValue().getCaptureDate());
        assertEquals("patient_id", vitalStatisticsArgumentCaptor.getValue().getPatientId());
        assertEquals(100, vitalStatisticsArgumentCaptor.getValue().getPulse().intValue());
        verify(clinicVisitService, times(1)).updateVitalStatistics(eq(clinicVisit.getId()), Matchers.<String>any());
        assertEquals("redirect:/clinicvisits/patient_id", returnUrl);
    }

    @Test
    public void shouldNotCreateVitalStatisticsRecord_WhenClinicVisitDoesNotHaveOne_ButVitalStatisticFieldsAreEmpty() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withPatientId("patient_id").build();
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().withVitalStatisticsId(null).build();
        final VitalStatisticsUIModel vitalStatisticsUIModel = VitalStatisticsUIModel.get(clinicVisit, vitalStatistics);

        final String returnUrl = vitalStatisticsController.update(vitalStatisticsUIModel, httpServletRequest);

        verifyZeroInteractions(allClinicVisits);
        verifyZeroInteractions(clinicVisitService);
        assertEquals("redirect:/clinicvisits/patient_id", returnUrl);
    }

    @Test
    public void shouldUpdateVitalStatisticsRecord_WhenClinicVisitHasOne() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        final String vitalStatisticsId = "vitalStatisticsId";
        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withId(vitalStatisticsId).withPatientId("patient_id").withPulse(100).build();
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
        final VitalStatisticsUIModel vitalStatisticsUIModel = VitalStatisticsUIModel.get(clinicVisit, vitalStatistics);
        when(allVitalStatistics.get(vitalStatisticsId)).thenReturn(new VitalStatistics());

        final String returnUrl = vitalStatisticsController.update(vitalStatisticsUIModel, httpServletRequest);

        ArgumentCaptor<VitalStatistics> vitalStatisticsArgumentCaptor = ArgumentCaptor.forClass(VitalStatistics.class);
        verify(allVitalStatistics, times(1)).update(vitalStatisticsArgumentCaptor.capture());
        assertEquals(DateUtil.today(), vitalStatisticsArgumentCaptor.getValue().getCaptureDate());
        assertEquals("patient_id", vitalStatisticsArgumentCaptor.getValue().getPatientId());
        assertEquals(100, vitalStatisticsArgumentCaptor.getValue().getPulse().intValue());
        verify(clinicVisitService, times(1)).updateVitalStatistics(eq(clinicVisit.getId()), Matchers.<String>any());
        assertEquals("redirect:/clinicvisits/patient_id", returnUrl);
    }

    @Test
    public void shouldRemoveVitalStatisticsRecord_WhenClinicVisitHasOne_AndAllFieldsAreUnset() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        final String vitalStatisticsId = "vitalStatisticsId";
        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withId(vitalStatisticsId).withPatientId("patient_id").build();
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
        final VitalStatisticsUIModel vitalStatisticsUIModel = VitalStatisticsUIModel.get(clinicVisit, vitalStatistics);
        final VitalStatistics savedVitalStatistics = new VitalStatistics();
        when(allVitalStatistics.get(vitalStatisticsId)).thenReturn(savedVitalStatistics);

        final String returnUrl = vitalStatisticsController.update(vitalStatisticsUIModel, httpServletRequest);

        verify(allVitalStatistics, times(1)).remove(savedVitalStatistics);
        verify(clinicVisitService, times(1)).updateVitalStatistics(clinicVisit.getId(), null);
        assertEquals("redirect:/clinicvisits/patient_id", returnUrl);
    }

}