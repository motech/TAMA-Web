package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.domain.LabResult;
import org.motechproject.tama.domain.VitalStatistics;
import org.motechproject.tama.repository.AllVitalStatistics;
import org.motechproject.tama.web.model.LabResultsUIModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        Model uiModel = mock(Model.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        when(allVitalStatistics.findByPatientId("patient_id")).thenReturn(new ArrayList<VitalStatistics>());

        assertEquals("vital_statistics/form", vitalStatisticsController.createForm("patient_id", uiModel, servletRequest));
        verify(uiModel).addAttribute("vitalStatistics", new VitalStatistics("patient_id"));
    }

    @Test
    public void shouldRedirectToShowPageWhenPatientHasVitalStatisticsEntered() {
        Model uiModel = mock(Model.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        List<VitalStatistics> vitalStatistics = Arrays.asList(new VitalStatistics("patient_id"));
        when(allVitalStatistics.findByPatientId("patient_id")).thenReturn(vitalStatistics);

        assertEquals("redirect:/vital_statistics/" + "patient_id", vitalStatisticsController.createForm("patient_id", uiModel, servletRequest));
        verify(uiModel).addAttribute("vitalStatistics", vitalStatistics.get(0));
    }

    @Test
    public void shouldSaveVitalStatistics() {
        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = mock(Model.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        VitalStatistics vitalStatistics = new VitalStatistics("patient_id");
        vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel, httpServletRequest);

        verify(allVitalStatistics, times(1)).add(vitalStatistics);
    }

    @Test
    public void shouldShowVitalStatistics() {
        Model uiModel = mock(Model.class);

        List<VitalStatistics> vitalStatistics = Arrays.asList(new VitalStatistics("patient_id"));
        when(allVitalStatistics.findByPatientId("patient_id")).thenReturn(vitalStatistics);

        assertEquals("vital_statistics/show", vitalStatisticsController.show("patient_id", uiModel));
        verify(uiModel).addAttribute("vitalStatistics", vitalStatistics.get(0));
    }

    @Test
    public void shouldShowFormForUpdatingVitalStatistics() {
        Model uiModel = mock(Model.class);
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        List<VitalStatistics> vitalStatistics = Arrays.asList(new VitalStatistics("patient_id"));
        when(allVitalStatistics.findByPatientId("patient_id")).thenReturn(vitalStatistics);

        assertEquals("vital_statistics/form", vitalStatisticsController.updateForm("patient_id", uiModel));
        verify(uiModel).addAttribute("vitalStatistics", vitalStatistics.get(0));
        verify(uiModel).addAttribute("_method", "put");
    }

    @Test
    public void shouldUpdateVitalStatistics() {
        BindingResult bindingResult = mock(BindingResult.class);
        Model uiModel = mock(Model.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

        List<VitalStatistics> vitalStatistics = Arrays.asList(new VitalStatistics("patient_id"));
        when(allVitalStatistics.findByPatientId("patient_id")).thenReturn(vitalStatistics);

        vitalStatisticsController.update(vitalStatistics.get(0), bindingResult, uiModel, httpServletRequest);

        verify(allVitalStatistics, times(1)).update(vitalStatistics.get(0));
    }
}