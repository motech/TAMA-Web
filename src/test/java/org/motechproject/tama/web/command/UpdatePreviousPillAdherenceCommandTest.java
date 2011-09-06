package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class UpdatePreviousPillAdherenceCommandTest {

    public static final String PREVIOUS_DOSAGE_ID = "previousDosageId";
    public static final String PATIENT_ID = "patientId";
    public static final String REGIMEN_ID = "regimenId";
    public static final String CURRENT_DOSAGE_ID = "currentDosageId";
    @Mock
    private IVRContext context;
    @Mock
    private IVRRequest ivrRequest;
    @Mock
    private IVRSession ivrSession;
    @Mock
    private PillReminderService pillReminderService;
    @Mock
    private AllDosageAdherenceLogs logs;

    private StopPreviousPillReminderCommand previousPillTakenCommand;
    private PillRegimenResponse pillRegimenResponse;

    @Before
    public void setup() {
        initMocks(this);

        previousPillTakenCommand = new StopPreviousPillReminderCommand(pillReminderService);

        Map<String, String> params = new HashMap<String, String>();
        params.put(PillReminderCall.DOSAGE_ID, CURRENT_DOSAGE_ID);

        when(ivrSession.getPatientId()).thenReturn(PATIENT_ID);
        when(ivrRequest.getTamaParams()).thenReturn(params);
        when(context.ivrSession()).thenReturn(ivrSession);
        when(context.ivrRequest()).thenReturn(ivrRequest);

        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);
    }

    @Test
    public void shouldCreateAnAdherenceLogIfThereIsNoLogFound() {
        when(ivrRequest.getInput()).thenReturn("1");
        when(logs.findByDosageIdAndDate(eq(PREVIOUS_DOSAGE_ID), any(LocalDate.class))).thenReturn(null);

        UpdatePreviousPillAdherenceCommand command = new UpdatePreviousPillAdherenceCommand(logs);
        command.execute(context);

        verify(logs).add(any(DosageAdherenceLog.class));
    }

    @Test
    public void shouldUpdateAnAdherenceLogIfThereIsAlreadyOneForTheCurrentDate() {
        DosageAdherenceLog log = new DosageAdherenceLog(PATIENT_ID, REGIMEN_ID, PREVIOUS_DOSAGE_ID, DosageStatus.NOT_TAKEN);

        when(ivrRequest.getInput()).thenReturn("1");
        when(logs.findByDosageIdAndDate(eq(PREVIOUS_DOSAGE_ID), any(LocalDate.class))).thenReturn(log);

        UpdatePreviousPillAdherenceCommand command = new UpdatePreviousPillAdherenceCommand(logs);
        command.execute(context);

        verify(logs, never()).add(any(DosageAdherenceLog.class));
        verify(logs).update(log);
    }

    @Test
    public void shouldNotUpdateOrCreateIfThereAreNotAnyChanges() {
        DosageAdherenceLog log = new DosageAdherenceLog(PATIENT_ID, REGIMEN_ID, PREVIOUS_DOSAGE_ID, DosageStatus.NOT_TAKEN);

        when(ivrRequest.getInput()).thenReturn("3");
        when(logs.findByDosageIdAndDate(eq(PREVIOUS_DOSAGE_ID), any(LocalDate.class))).thenReturn(log);

        UpdatePreviousPillAdherenceCommand command = new UpdatePreviousPillAdherenceCommand(logs);
        command.execute(context);

        verify(logs, never()).add(any(DosageAdherenceLog.class));
        verify(logs, never()).update(log);
    }


}
