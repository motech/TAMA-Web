package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class VitalStatisticsControllerTest {

    private VitalStatisticsController vitalStatisticsController;

    @Mock
    private AllVitalStatistics allVitalStatistics;

    @Before
    public void setUp() {
        initMocks(this);
        vitalStatisticsController = new VitalStatisticsController(allVitalStatistics);
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

        VitalStatistics vitalStatistics = new VitalStatistics("patient_id");
        vitalStatistics.setWeightInKg(200.0);
        vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel);

        verify(allVitalStatistics, times(1)).add(vitalStatistics);
    }

    @Test
    public void shouldNotSaveVitalStatistics_WhenFormHasErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        Model uiModel = new ExtendedModelMap();

        VitalStatistics vitalStatistics = new VitalStatistics("patient_id");
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

        VitalStatistics vitalStatistics = new VitalStatistics("patient_id");
        vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel);

        verifyZeroInteractions(allVitalStatistics);
    }

    @Test
    public void shouldShowVitalStatistics() {
        Model uiModel = mock(Model.class);

        VitalStatistics vitalStatistics = new VitalStatistics("patient_id");
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patient_id")).thenReturn(vitalStatistics);

        vitalStatisticsController.show("patient_id", uiModel);
        verify(uiModel).addAttribute("vitalStatistics", vitalStatistics);
    }

    @Test
    public void shouldShowFormForUpdatingVitalStatistics() {
        Model uiModel = mock(Model.class);

        VitalStatistics vitalStatistics = new VitalStatistics("patient_id");
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patient_id")).thenReturn(vitalStatistics);

        assertEquals("vital_statistics/update", vitalStatisticsController.updateForm("patient_id", uiModel));
        verify(uiModel).addAttribute("vitalStatistics", vitalStatistics);
        verify(uiModel).addAttribute("_method", "put");
    }

    @Test
    public void shouldUpdateVitalStatistics() {
        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = mock(Model.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        VitalStatistics vitalStatistics = new VitalStatistics("patient_id");
        when(allVitalStatistics.findLatestVitalStatisticByPatientId("patient_id")).thenReturn(vitalStatistics);

        vitalStatisticsController.update(vitalStatistics, bindingResult, uiModel, httpServletRequest);

        verify(allVitalStatistics, times(1)).update(vitalStatistics);
    }
}