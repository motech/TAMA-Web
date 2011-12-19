package org.motechproject.tama.dailypillreminder.listener;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.repository.AllPatients;

import java.util.Map;
import java.util.Properties;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

public class AdherenceQualityListenerTest {

    @Mock
    private DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    @Mock
    private Properties properties;
    @Mock
    private AllPatients allPatients;

    @Mock
    private DailyPillReminderAdherenceService dailyReminderAdherenceService;

    private AdherenceQualityListener adherenceQualityListener;

    private Patient patient;
    private static String PATIENT_ID = "patient_UUId";

    @Before
    public void setUp() {
        initMocks(this);
        adherenceQualityListener = new AdherenceQualityListener(dailyReminderAdherenceTrendService, properties, dailyReminderAdherenceService, allPatients);

        patient = new PatientBuilder().withId(PATIENT_ID).withDefaults().withStatus(Status.Active).build();
        when(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE)).thenReturn("70");

    }

    @Test
    public void shouldDetermineAdherenceQualityAndRaiseRedAlertIfAdherenceIsBelowAcceptablePercentage() throws Exception {
        double adherencePercentage = 69.0;

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(dailyReminderAdherenceService.getAdherenceInPercentage(same(PATIENT_ID), Matchers.<DateTime>any())).thenReturn(adherencePercentage);

        MotechEvent motechEvent = new MotechEvent(TAMAConstants.DAILY_ADHERENCE_IN_RED_ALERT_SUBJECT);
        Map<String, Object> parameters = motechEvent.getParameters();
        parameters.put(EventKeys.EXTERNAL_ID_KEY, PATIENT_ID);

        adherenceQualityListener.determineAdherenceQualityAndRaiseAlert(motechEvent);

        verify(dailyReminderAdherenceService).getAdherenceInPercentage(same(PATIENT_ID), Matchers.<DateTime>any());
        verify(dailyReminderAdherenceTrendService, times(1)).raiseAdherenceInRedAlert(eq(PATIENT_ID), eq(adherencePercentage));
    }

    @Test
    public void shouldDetermineAdherenceQualityAndNotRaiseRedAlertIfAdherencePercentageIsAcceptable() throws Exception {
        double adherencePercentage = 70.0;

        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(dailyReminderAdherenceService.getAdherenceInPercentage(same(PATIENT_ID), Matchers.<DateTime>any())).thenReturn(adherencePercentage);

        MotechEvent motechEvent = new MotechEvent(TAMAConstants.DAILY_ADHERENCE_IN_RED_ALERT_SUBJECT);
        Map<String, Object> parameters = motechEvent.getParameters();
        parameters.put(EventKeys.EXTERNAL_ID_KEY, PATIENT_ID);

        adherenceQualityListener.determineAdherenceQualityAndRaiseAlert(motechEvent);

        verify(dailyReminderAdherenceService).getAdherenceInPercentage(same(PATIENT_ID), Matchers.<DateTime>any());
        verify(dailyReminderAdherenceTrendService, never()).raiseAdherenceInRedAlert(Matchers.<String>any(), Matchers.<Double>any());
    }

    @Test
    public void shouldNotRaiseRedAlertWhenPatientIsSuspended() throws Exception {
        patient.setStatus(Status.Suspended);
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);

        MotechEvent motechEvent = new MotechEvent(TAMAConstants.DAILY_ADHERENCE_IN_RED_ALERT_SUBJECT);
        Map<String, Object> parameters = motechEvent.getParameters();
        parameters.put(EventKeys.EXTERNAL_ID_KEY, PATIENT_ID);

        adherenceQualityListener.determineAdherenceQualityAndRaiseAlert(motechEvent);

        verifyNoMoreInteractions(dailyReminderAdherenceService);
        verifyNoMoreInteractions(dailyReminderAdherenceTrendService);
    }


}
