package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamadomain.builder.PillRegimenResponseBuilder;
import org.motechproject.tamadomain.domain.DosageAdherenceLog;
import org.motechproject.tamadomain.domain.DosageStatus;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class UpdatePreviousPillAdherenceCommandTest {
    public static final String PREVIOUS_DOSAGE_ID = "previousDosageId";
    public static final String PATIENT_ID = "patientId";
    public static final String REGIMEN_ID = "regimenId";
    @Mock
    private AllDosageAdherenceLogs logs;
    private TAMAIVRContextForTest ivrContext;

    @Before
    public void setup() {
        initMocks(this);
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        ivrContext = new TAMAIVRContextForTest().dosageId("currentDosageId").patientId(PATIENT_ID).pillRegimen(pillRegimenResponse).callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
    }

    @Test
    public void shouldCreateAnAdherenceLogIfThereIsNoLogFound() {
        ivrContext.dtmfInput("1");
        when(logs.findByDosageIdAndDate(eq(PREVIOUS_DOSAGE_ID), any(LocalDate.class))).thenReturn(null);

        UpdatePreviousPillAdherenceCommand command = new UpdatePreviousPillAdherenceCommand(logs, null);
        command.executeCommand(ivrContext);

        verify(logs).add(any(DosageAdherenceLog.class));
    }

    @Test
    public void shouldUpdateAnAdherenceLogIfThereIsAlreadyOneForTheCurrentDate() {
        DosageAdherenceLog log = new DosageAdherenceLog(PATIENT_ID, REGIMEN_ID, PREVIOUS_DOSAGE_ID, DosageStatus.NOT_TAKEN, DateUtil.today());
        ivrContext.dtmfInput("1");
        when(logs.findByDosageIdAndDate(eq(PREVIOUS_DOSAGE_ID), any(LocalDate.class))).thenReturn(log);

        UpdatePreviousPillAdherenceCommand command = new UpdatePreviousPillAdherenceCommand(logs, null);
        command.executeCommand(ivrContext);

        verify(logs, never()).add(any(DosageAdherenceLog.class));
        verify(logs).update(log);
    }

    @Test
    public void shouldNotUpdateOrCreateIfThereAreNotAnyChanges() {
        DosageAdherenceLog log = new DosageAdherenceLog(PATIENT_ID, REGIMEN_ID, PREVIOUS_DOSAGE_ID, DosageStatus.NOT_TAKEN, DateUtil.today());

        ivrContext.dtmfInput("3");
        when(logs.findByDosageIdAndDate(eq(PREVIOUS_DOSAGE_ID), any(LocalDate.class))).thenReturn(log);

        UpdatePreviousPillAdherenceCommand command = new UpdatePreviousPillAdherenceCommand(logs, null);
        command.executeCommand(ivrContext);

        verify(logs, never()).add(any(DosageAdherenceLog.class));
        verify(logs, never()).update(log);
    }
}
