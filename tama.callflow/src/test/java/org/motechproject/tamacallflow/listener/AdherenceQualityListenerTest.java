package org.motechproject.tamacallflow.listener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;

import java.util.Map;
import java.util.Properties;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class AdherenceQualityListenerTest {
    @Mock
    private DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    @Mock
    private Properties properties;


    private AdherenceQualityListener adherenceQualityListener;

    @Before
    public void setUp() {
        initMocks(this);
        adherenceQualityListener = new AdherenceQualityListener(dailyReminderAdherenceTrendService, properties);
    }

    @Test
    public void shouldDetermineAdherenceQualityAndRaiseRedAlertIfAdherenceIsBelowAcceptablePercentage() throws Exception {
        String patientId = "patient_UUId";
        String acceptableAdherence = "70";
        double adherencePercentage = 69d;

        when(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE)).thenReturn(acceptableAdherence);
        when(dailyReminderAdherenceTrendService.getAdherencePercentage(patientId)).thenReturn(adherencePercentage);

        MotechEvent motechEvent = new MotechEvent(TAMAConstants.DAILY_ADHERENCE_IN_RED_ALERT_SUBJECT);
        Map<String,Object> parameters = motechEvent.getParameters();
        parameters.put(EventKeys.EXTERNAL_ID_KEY, patientId);

        adherenceQualityListener.determineAdherenceQualityAndRaiseAlert(motechEvent);

        verify(dailyReminderAdherenceTrendService).getAdherencePercentage(eq(patientId));
        verify(dailyReminderAdherenceTrendService, times(1)).raiseAdherenceInRedAlert(eq(patientId), eq(adherencePercentage));
    }

    @Test
    public void shouldDetermineAdherenceQualityAndNotRaiseRedAlertIfAdherencePercentageIsAcceptable() throws Exception {
        String patientId = "patient_UUId";
        String acceptableAdherence = "70";
        double adherencePercentage = 70.001;

        when(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE)).thenReturn(acceptableAdherence);
        when(dailyReminderAdherenceTrendService.getAdherencePercentage(patientId)).thenReturn(adherencePercentage);

        MotechEvent motechEvent = new MotechEvent(TAMAConstants.DAILY_ADHERENCE_IN_RED_ALERT_SUBJECT);
        Map<String,Object> parameters = motechEvent.getParameters();
        parameters.put(EventKeys.EXTERNAL_ID_KEY, patientId);

        adherenceQualityListener.determineAdherenceQualityAndRaiseAlert(motechEvent);

        verify(dailyReminderAdherenceTrendService).getAdherencePercentage(eq(patientId));
        verify(dailyReminderAdherenceTrendService, never()).raiseAdherenceInRedAlert(Matchers.<String>any(), Matchers.<Double>any());
    }


}
