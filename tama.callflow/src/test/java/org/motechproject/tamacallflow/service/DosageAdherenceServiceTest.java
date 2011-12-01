package org.motechproject.tamacallflow.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tamacallflow.builder.TAMAPillRegimenBuilder;
import org.motechproject.tamacallflow.domain.TAMAPillRegimen;
import org.motechproject.tamadomain.domain.DosageAdherenceLog;
import org.motechproject.tamadomain.domain.SuspendedAdherenceData;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;

import java.util.Properties;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DosageAdherenceServiceTest {

    @Mock
    private AllDosageAdherenceLogs allDosageAdherenceLogs;
    @Mock
    private TAMAPillReminderService tamaPillReminderService;
    @Mock
    private Properties properties;

    private DosageAdherenceService dosageAdherenceService;

    @Before
    public void setUp() {
        initMocks(this);
        dosageAdherenceService = new DosageAdherenceService(allDosageAdherenceLogs, tamaPillReminderService, properties);
    }

    @Test
    public void shouldCreateAdherenceLogsForEveryDosageWhenRecordingAdherence() {
        SuspendedAdherenceData suspendedAdherenceData = SuspendedAdherenceDataPreset.fromYesterdayWithAnyStatus();
        TAMAPillRegimen tamaPillRegimen = TAMAPillRegimenBuilder.startRecording().withThreeDosagesInTotal().withTwoDosagesFrom(suspendedAdherenceData.suspendedFrom()).build();
        when(properties.getProperty(any(String.class))).thenReturn("2");
        when(tamaPillReminderService.getPillRegimen("patientId")).thenReturn(tamaPillRegimen);
        DosageAdherenceLog testAdherenceLog = new DosageAdherenceLog();
        dosageAdherenceService.recordAdherence(suspendedAdherenceData);
        verify(allDosageAdherenceLogs, times(2)).add(Matchers.<DosageAdherenceLog>any());
    }
}


