package org.motechproject.tama.web.command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Map;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;
import org.motechproject.util.DateUtil;

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
        context = new IVRContext(req, session);

        when(req.getParameter(PillReminderCall.DOSAGE_ID)).thenReturn(dosageId);
        when(session.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(PillRegimenResponseBuilder.startRecording().withDefaults().withRegimenId(pillRegimenId).build());
        when(session.get(TamaSessionAttribute.PATIENT_DOC_ID)).thenReturn(patientId);
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
        log = new DosageAdherenceLog(patientId, pillRegimenId, dosageId, DosageStatus.NOT_TAKEN, DateUtil.today());

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

        log = new DosageAdherenceLog(patientId, pillRegimenId, dosageId, DosageStatus.NOT_TAKEN, DateUtil.today());

        when(logs.findByDosageIdAndDate(eq(dosageId), any(LocalDate.class))).thenReturn(log);

        UpdateAdherenceCommand command = new UpdateAdherenceCommand(logs);
        command.execute(context);

        verify(logs, never()).add(any(DosageAdherenceLog.class));
        verify(logs, never()).update(log);
    }


}
