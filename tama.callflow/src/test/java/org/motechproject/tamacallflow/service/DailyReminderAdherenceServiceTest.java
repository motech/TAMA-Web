package org.motechproject.tamacallflow.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tamacallflow.builder.TAMAPillRegimenBuilder;
import org.motechproject.tamacallflow.domain.PillRegimen;
import org.motechproject.tamadomain.domain.DosageAdherenceLog;
import org.motechproject.tamadomain.domain.SuspendedAdherenceData;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;

import java.util.Properties;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DailyReminderAdherenceServiceTest {

    @Mock
    private AllDosageAdherenceLogs allDosageAdherenceLogs;
    @Mock
    private TAMAPillReminderService tamaPillReminderService;
    @Mock
    private Properties properties;

    private DailyReminderAdherenceService dailyReminderAdherenceService;

    @Before
    public void setUp() {
        initMocks(this);
        dailyReminderAdherenceService = new DailyReminderAdherenceService(allDosageAdherenceLogs, tamaPillReminderService, properties);
    }

    @Test
    public void shouldCreateAdherenceLogsForEveryDosageWhenRecordingAdherence() {
        SuspendedAdherenceData suspendedAdherenceData = SuspendedAdherenceDataPreset.fromYesterdayWithAnyStatus();
        PillRegimen pillRegimen = TAMAPillRegimenBuilder.startRecording().withThreeDosagesInTotal().withTwoDosagesFrom(suspendedAdherenceData.suspendedFrom()).build();
        when(properties.getProperty(any(String.class))).thenReturn("2");
        when(tamaPillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimen);
        DosageAdherenceLog testAdherenceLog = new DosageAdherenceLog();
        dailyReminderAdherenceService.recordAdherence(suspendedAdherenceData);
        verify(allDosageAdherenceLogs, times(2)).add(Matchers.<DosageAdherenceLog>any());
    }
}


