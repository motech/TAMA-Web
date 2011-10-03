package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.util.TamaSessionUtil;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;
import org.motechproject.util.DateUtil;

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

        when(ivrRequest.getParameter(PillReminderCall.DOSAGE_ID)).thenReturn("currentDosageId");        
        when(ivrSession.get(TamaSessionUtil.TamaSessionAttribute.PATIENT_DOC_ID)).thenReturn(PATIENT_ID);
        when(context.ivrSession()).thenReturn(ivrSession);
        when(context.ivrRequest()).thenReturn(ivrRequest);

        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        when(ivrSession.get(TamaSessionUtil.TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimenResponse);
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
        DosageAdherenceLog log = new DosageAdherenceLog(PATIENT_ID, REGIMEN_ID, PREVIOUS_DOSAGE_ID, DosageStatus.NOT_TAKEN, DateUtil.today());

        when(ivrRequest.getInput()).thenReturn("1");
        when(logs.findByDosageIdAndDate(eq(PREVIOUS_DOSAGE_ID), any(LocalDate.class))).thenReturn(log);

        UpdatePreviousPillAdherenceCommand command = new UpdatePreviousPillAdherenceCommand(logs);
        command.execute(context);

        verify(logs, never()).add(any(DosageAdherenceLog.class));
        verify(logs).update(log);
    }

    @Test
    public void shouldNotUpdateOrCreateIfThereAreNotAnyChanges() {
        DosageAdherenceLog log = new DosageAdherenceLog(PATIENT_ID, REGIMEN_ID, PREVIOUS_DOSAGE_ID, DosageStatus.NOT_TAKEN, DateUtil.today());

        when(ivrRequest.getInput()).thenReturn("3");
        when(logs.findByDosageIdAndDate(eq(PREVIOUS_DOSAGE_ID), any(LocalDate.class))).thenReturn(log);

        UpdatePreviousPillAdherenceCommand command = new UpdatePreviousPillAdherenceCommand(logs);
        command.execute(context);

        verify(logs, never()).add(any(DosageAdherenceLog.class));
        verify(logs, never()).update(log);
    }


}
