package org.motechproject.tama.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.builder.TAMAPillRegimenBuilder;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.TAMAPillRegimen;
import org.motechproject.tama.preset.SuspendedAdherenceDataPreset;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.web.view.SuspendedAdherenceData;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DosageAdherenceServiceTest {

    @Mock
    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    @Mock
    private TAMAPillReminderService tamaPillReminderService;

    private DosageAdherenceService dosageAdherenceService;

    @Before
    public void setUp() {
        initMocks(this);
        dosageAdherenceService = new DosageAdherenceService(allDosageAdherenceLogs, tamaPillReminderService);
    }

    @Test
    public void shouldCreateAdherenceLogsForEveryDosageWhenRecordingAdherence() {
        SuspendedAdherenceData suspendedAdherenceData = SuspendedAdherenceDataPreset.fromYesterdayWithAnyStatus();
        TAMAPillRegimen tamaPillRegimen = TAMAPillRegimenBuilder.startRecording().withThreeDosagesInTotal().withTwoDosagesFrom(suspendedAdherenceData.suspendedFrom()).build();
        when(tamaPillReminderService.getPillRegimen("patientId")).thenReturn(tamaPillRegimen);
        dosageAdherenceService.recordAdherence(suspendedAdherenceData);
        verify(allDosageAdherenceLogs, times(2)).add(Matchers.<DosageAdherenceLog>any());
    }
}


