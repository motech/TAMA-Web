package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
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

public class UpdateAdherenceCommandTest {

    private String pillRegimenId;
    private String dosageId;
    private String patientId;
    private String userInput;
    private Map<String, String> params;
    private IVRContext context;
    private DosageAdherenceLog log;

    @Mock
    private AllDosageAdherenceLogs logs;
    @Mock
    private IVRRequest req;
    @Mock
    private IVRSession session;

    @Before
    public void setup() {
        initMocks(this);

        pillRegimenId = "pillRegimenId";
        dosageId = "currentDosageId";
        patientId = "test";
        userInput = "1";
        params = new HashMap<String, String>();
        params.put(PillReminderCall.DOSAGE_ID, dosageId);
        context = new IVRContext(req, session);

        when(req.getTamaParams()).thenReturn(params);
        when(session.getPatientId()).thenReturn(patientId);
        when(session.getPillRegimen()).thenReturn(PillRegimenResponseBuilder.startRecording().withDefaults().withRegimenId(pillRegimenId).build());
        when(req.getInput()).thenReturn(userInput);
    }

    @Test
    public void shouldCreateAnAdherenceLogIfThereIsNoLogFound() {
        when(logs.findByDosageIdAndDate(eq(dosageId), any(LocalDate.class))).thenReturn(null);

        UpdateAdherenceCommand command = new UpdateAdherenceCommand(logs);
        command.execute(context);

        verify(logs).add(any(DosageAdherenceLog.class));
    }

    @Test
    public void shouldUpdateAnAdherenceLogIfThereIsAlreadyOneForTheCurrentDate() {
        log = new DosageAdherenceLog(patientId, pillRegimenId, dosageId, DosageStatus.NOT_TAKEN);

        when(logs.findByDosageIdAndDate(eq(dosageId), any(LocalDate.class))).thenReturn(log);

        UpdateAdherenceCommand command = new UpdateAdherenceCommand(logs);
        command.execute(context);

        verify(logs, never()).add(any(DosageAdherenceLog.class));
        verify(logs).update(log);
    }

    @Test
    public void shouldNotUpdateOrCreateIfThereAreNotAnyChanges() {
        userInput = "3";
        when(req.getInput()).thenReturn(userInput);

        log = new DosageAdherenceLog(patientId, pillRegimenId, dosageId, DosageStatus.NOT_TAKEN);

        when(logs.findByDosageIdAndDate(eq(dosageId), any(LocalDate.class))).thenReturn(log);

        UpdateAdherenceCommand command = new UpdateAdherenceCommand(logs);
        command.execute(context);

        verify(logs, never()).add(any(DosageAdherenceLog.class));
        verify(logs, never()).update(log);
    }


}
