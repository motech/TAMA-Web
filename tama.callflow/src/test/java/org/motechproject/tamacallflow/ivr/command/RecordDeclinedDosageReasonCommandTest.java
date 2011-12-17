package org.motechproject.tamacallflow.ivr.command;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tamacallflow.builder.PillRegimenResponseBuilder;
import org.motechproject.tamacallflow.domain.DosageAdherenceLog;
import org.motechproject.tamacallflow.domain.DosageStatus;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamacallflow.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RecordDeclinedDosageReasonCommandTest {
    private String pillRegimenId;
    private String dosageId;
    private String patientId;

    @Mock
    private AllDosageAdherenceLogs logs;
    private TAMAIVRContextForTest context;

    @Before
    public void setup() {
        initMocks(this);

        pillRegimenId = "pillRegimenId";
        dosageId = "currentDosageId";
        patientId = "test";
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withRegimenId(pillRegimenId).build();
        context = new TAMAIVRContextForTest().dosageId(dosageId).pillRegimen(pillRegimenResponse).patientId(patientId).dtmfInput("1");
    }

    @Test
    public void shouldUpdateTheDeclinedDosageAdherenceLogWithReason() {
        DosageAdherenceLog log = new DosageAdherenceLog(patientId, pillRegimenId, dosageId, DosageStatus.NOT_TAKEN, DateUtil.today());
        when(logs.findByDosageIdAndDate(eq(dosageId), any(LocalDate.class))).thenReturn(log);

        RecordDeclinedDosageReasonCommand command = new RecordDeclinedDosageReasonCommand(logs, null);
        command.executeCommand(context);

        verify(logs).update(any(DosageAdherenceLog.class));
    }
}
