package org.motechproject.tama.web.command;

import org.junit.Test;
import org.motechproject.server.pillreminder.domain.PillReminder;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.DosageAdherenceLogs;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateAdherenceCommandTest {
    @Test
    public void shouldCreateAnAdherenceLogWithIVRParams() {
        String pillRegimenId = "pillRegimenId";
        String dosageId = "dosageId";
        String patientId = "test";
        String userInput = "1";
        Map<String, String> params = new HashMap<String, String>();
        params.put(PillReminderCall.REGIMEN_ID, pillRegimenId);
        params.put(PillReminderCall.DOSAGE_ID, dosageId);

        DosageAdherenceLogs logs = mock(DosageAdherenceLogs.class);
        IVRContext context = mock(IVRContext.class);
        IVRRequest req = mock(IVRRequest.class);
        IVRSession session = mock(IVRSession.class);

        when(context.ivrRequest()).thenReturn(req);
        when(context.ivrSession()).thenReturn(session);
        when(session.get(IVRCallAttribute.PATIENT_DOC_ID)).thenReturn(patientId);
        when(req.getInput()).thenReturn(userInput);

        UpdateAdherenceCommand command = new UpdateAdherenceCommand(logs);
        command.execute(context);

        verify(logs).add(any(DosageAdherenceLog.class));
    }

    @Test
    public void shouldCreateAnAdherenceLogIfThereIsNoneForTheCurrentDate() {
        String pillRegimenId = "pillRegimenId";
        String dosageId = "dosageId";
        String patientId = "test";
        String userInput = "1";
        Map<String, String> params = new HashMap<String, String>();
        params.put(PillReminderCall.REGIMEN_ID, pillRegimenId);
        params.put(PillReminderCall.DOSAGE_ID, dosageId);

        DosageAdherenceLogs logs = mock(DosageAdherenceLogs.class);
        IVRContext context = mock(IVRContext.class);
        IVRRequest req = mock(IVRRequest.class);
        IVRSession session = mock(IVRSession.class);

        when(context.ivrRequest()).thenReturn(req);
        when(context.ivrSession()).thenReturn(session);
        when(session.get(IVRCallAttribute.PATIENT_DOC_ID)).thenReturn(patientId);
        when(req.getInput()).thenReturn(userInput);

        UpdateAdherenceCommand command = new UpdateAdherenceCommand(logs);
        command.execute(context);

        verify(logs).add(any(DosageAdherenceLog.class));
    }
}
