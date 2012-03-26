package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.builder.VitalStatisticsBuilder;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.web.model.VitalStatisticsUIModel;
import org.motechproject.util.DateUtil;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class VitalStatisticsControllerTest {

    public static final String PATIENT_ID = "patientId";
    private static final String USER_NAME = "userName";

    private VitalStatisticsController vitalStatisticsController;
    @Mock
    private AllVitalStatistics allVitalStatistics;
    @Mock
    private AllClinicVisits allClinicVisits;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpSession httpSession;
    @Mock
    AuthenticatedUser user;
    @Mock
    BindingResult bindingResult;
    @Mock
    private Model uiModel;

    @Before
    public void setUp() {
        initMocks(this);
        vitalStatisticsController = new VitalStatisticsController(allVitalStatistics, allClinicVisits);
        when(httpServletRequest.getSession()).thenReturn(httpSession);
        when(user.getUsername()).thenReturn(USER_NAME);
        when(httpSession.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
    }

    @Test
    public void shouldShowFormWhenPatientHasNoVitalStatisticsEntered() {
        Model uiModel = new ExtendedModelMap();

        when(allVitalStatistics.findLatestVitalStatisticByPatientId(PATIENT_ID)).thenReturn(null);

        vitalStatisticsController.createForm(PATIENT_ID, uiModel);
        assertEquals(VitalStatistics.class, uiModel.asMap().get("vitalStatistics").getClass());
        assertEquals(PATIENT_ID, ((VitalStatistics) uiModel.asMap().get("vitalStatistics")).getPatientId());
    }

    @Test
    public void shouldSaveVitalStatistics() {
        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withDefaults().build();
        vitalStatistics.setWeightInKg(200.0);
        vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel, httpServletRequest);

        verify(allVitalStatistics, times(1)).add(vitalStatistics, USER_NAME);
    }

    @Test
    public void shouldNotSaveVitalStatistics_WhenFormHasErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        Model uiModel = new ExtendedModelMap();

        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withDefaults().build();
        vitalStatistics.setWeightInKg(200.0);
        vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel, httpServletRequest);

        assertEquals(VitalStatistics.class, uiModel.asMap().get("vitalStatistics").getClass());
        assertEquals(vitalStatistics, uiModel.asMap().get("vitalStatistics"));
        verifyZeroInteractions(allVitalStatistics);
    }

    @Test
    public void shouldNotSaveVitalStatistics_WhenNoneOfTheParametersAreSet() {
        when(bindingResult.hasErrors()).thenReturn(true);
        Model uiModel = new ExtendedModelMap();

        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withDefaults().build();
        vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel, httpServletRequest);

        verifyZeroInteractions(allVitalStatistics);
    }

    @Test
    public void shouldSetFlashError_WhenCreateErrorsOut() {
        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withDefaults().build();
        vitalStatistics.setWeightInKg(200.0);
        doThrow(new RuntimeException("Some error")).when(allVitalStatistics).add(vitalStatistics, USER_NAME);

        vitalStatisticsController.create(vitalStatistics, bindingResult, uiModel, httpServletRequest);

        verify(httpServletRequest).setAttribute("flash.flashErrorVitalStatistics", "Error occurred while creating Vital Statistics: Some error");
    }

    @Test
    public void shouldShowVitalStatistics() {
        final String vitalStatisticsId = "vitalStatisticsId";

        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withPatientId("patientId").withWeight(100).build();
        when(allVitalStatistics.get(vitalStatisticsId)).thenReturn(vitalStatistics);

        vitalStatisticsController.show(vitalStatisticsId, uiModel);
        verify(uiModel).addAttribute("vitalStatistics", vitalStatistics);
    }

    @Test
    public void shouldShowEmptyVitalStatisticsIfNotSaved() {
        vitalStatisticsController.show("vitalStatisticsId", uiModel);
        verify(uiModel).addAttribute("vitalStatistics", new VitalStatistics());
    }

    @Test
    public void shouldShowFormForUpdatingVitalStatistics() {
        Model uiModel = new ExtendedModelMap();

        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withId("vitalStatisticsId").withPatientId(PATIENT_ID).withPulse(100).build();
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
        when(allClinicVisits.get(PATIENT_ID, "clinicVisitId")).thenReturn(clinicVisit);
        when(allVitalStatistics.get(vitalStatistics.getId())).thenReturn(vitalStatistics);

        assertEquals("vital_statistics/update", vitalStatisticsController.updateForm(PATIENT_ID, "clinicVisitId", uiModel));

        final VitalStatisticsUIModel vitalStatisticsUIModel = (VitalStatisticsUIModel) uiModel.asMap().get("vitalStatisticsUIModel");
        assertEquals(100, vitalStatisticsUIModel.getVitalStatistics().getPulse().intValue());
        assertEquals(vitalStatisticsUIModel.getClinicVisitId(), clinicVisit.getId());
        assertEquals(uiModel.asMap().get("_method"), "put");
    }

    @Test
    public void shouldShowEmptyUpdateForm_WhenClinicVisitDoesNotHaveOne() {
        Model uiModel = new ExtendedModelMap();

        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().withVitalStatisticsId(null).build();
        when(allClinicVisits.get(PATIENT_ID, "clinicVisitId")).thenReturn(clinicVisit);

        assertEquals("vital_statistics/update", vitalStatisticsController.updateForm(PATIENT_ID, "clinicVisitId", uiModel));

        final VitalStatisticsUIModel vitalStatisticsUIModel = ((VitalStatisticsUIModel) uiModel.asMap().get("vitalStatisticsUIModel"));
        assertNull(vitalStatisticsUIModel.getVitalStatistics().getPulse());
        assertNull(vitalStatisticsUIModel.getVitalStatistics().getDiastolicBp());
        assertNull(vitalStatisticsUIModel.getVitalStatistics().getSystolicBp());
        assertNull(vitalStatisticsUIModel.getVitalStatistics().getWeightInKg());
        assertNull(vitalStatisticsUIModel.getVitalStatistics().getHeightInCm());
        assertNull(vitalStatisticsUIModel.getVitalStatistics().getTemperatureInFahrenheit());
        assertEquals(vitalStatisticsUIModel.getClinicVisitId(), clinicVisit.getId());
        assertEquals(uiModel.asMap().get("_method"), "put");
    }

    @Test
    public void shouldCreateVitalStatisticsRecord_WhenClinicVisitDoesNotHaveOne() {
        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withPatientId(PATIENT_ID).withPulse(100).build();
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().withVitalStatisticsId(null).build();
        final VitalStatisticsUIModel vitalStatisticsUIModel = VitalStatisticsUIModel.get(clinicVisit, vitalStatistics);

        final String returnUrl = vitalStatisticsController.update(vitalStatisticsUIModel, httpServletRequest);

        ArgumentCaptor<VitalStatistics> vitalStatisticsArgumentCaptor = ArgumentCaptor.forClass(VitalStatistics.class);
        verify(allVitalStatistics, times(1)).add(vitalStatisticsArgumentCaptor.capture(), eq(USER_NAME));
        assertEquals(DateUtil.today(), vitalStatisticsArgumentCaptor.getValue().getCaptureDate());
        assertEquals(PATIENT_ID, vitalStatisticsArgumentCaptor.getValue().getPatientId());
        assertEquals(100, vitalStatisticsArgumentCaptor.getValue().getPulse().intValue());
        verify(allClinicVisits, times(1)).updateVitalStatistics(eq(PATIENT_ID), eq(clinicVisit.getId()), Matchers.<String>any());
        assertEquals("redirect:/clinicvisits/" + clinicVisit.getId() + "?patientId=" + PATIENT_ID, returnUrl);
    }

    @Test
    public void shouldNotCreateVitalStatisticsRecord_WhenClinicVisitDoesNotHaveOne_ButVitalStatisticFieldsAreEmpty() {
        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withPatientId(PATIENT_ID).build();
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().withVitalStatisticsId(null).build();
        final VitalStatisticsUIModel vitalStatisticsUIModel = VitalStatisticsUIModel.get(clinicVisit, vitalStatistics);

        final String returnUrl = vitalStatisticsController.update(vitalStatisticsUIModel, httpServletRequest);

        verifyZeroInteractions(allClinicVisits);
        assertEquals("redirect:/clinicvisits/" + clinicVisit.getId() + "?patientId=" + PATIENT_ID, returnUrl);
    }

    @Test
    public void shouldUpdateVitalStatisticsRecord_WhenClinicVisitHasOne() {
        final String vitalStatisticsId = "vitalStatisticsId";
        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withId(vitalStatisticsId).withPatientId(PATIENT_ID).withPulse(100).build();
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
        final VitalStatisticsUIModel vitalStatisticsUIModel = VitalStatisticsUIModel.get(clinicVisit, vitalStatistics);
        when(allVitalStatistics.get(vitalStatisticsId)).thenReturn(new VitalStatistics());

        final String returnUrl = vitalStatisticsController.update(vitalStatisticsUIModel, httpServletRequest);

        ArgumentCaptor<VitalStatistics> vitalStatisticsArgumentCaptor = ArgumentCaptor.forClass(VitalStatistics.class);
        verify(allVitalStatistics, times(1)).update(vitalStatisticsArgumentCaptor.capture(), eq(USER_NAME));
        assertEquals(DateUtil.today(), vitalStatisticsArgumentCaptor.getValue().getCaptureDate());
        assertEquals(PATIENT_ID, vitalStatisticsArgumentCaptor.getValue().getPatientId());
        assertEquals(100, vitalStatisticsArgumentCaptor.getValue().getPulse().intValue());
        verify(allClinicVisits, times(1)).updateVitalStatistics(eq(PATIENT_ID), eq(clinicVisit.getId()), eq("vitalStatisticsId"));
        assertEquals("redirect:/clinicvisits/" + clinicVisit.getId() + "?patientId=" + PATIENT_ID, returnUrl);
    }

    @Test
    public void shouldRemoveVitalStatisticsRecord_WhenClinicVisitHasOne_AndAllFieldsAreUnset() {
        final String vitalStatisticsId = "vitalStatisticsId";
        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withId(vitalStatisticsId).withPatientId(PATIENT_ID).build();
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
        final VitalStatisticsUIModel vitalStatisticsUIModel = VitalStatisticsUIModel.get(clinicVisit, vitalStatistics);
        final VitalStatistics savedVitalStatistics = new VitalStatistics();
        when(allVitalStatistics.get(vitalStatisticsId)).thenReturn(savedVitalStatistics);

        final String returnUrl = vitalStatisticsController.update(vitalStatisticsUIModel, httpServletRequest);

        verify(allVitalStatistics, times(1)).remove(savedVitalStatistics, USER_NAME);
        verify(allClinicVisits, times(1)).updateVitalStatistics(PATIENT_ID, clinicVisit.getId(), null);
        assertEquals("redirect:/clinicvisits/" + clinicVisit.getId() + "?patientId=" + PATIENT_ID, returnUrl);
    }

}