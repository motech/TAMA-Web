package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.api.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLog;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
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
    private DailyPillReminderContextForTest context;

    @Before
    public void setup() {
        initMocks(this);

        pillRegimenId = "pillRegimenId";
        dosageId = "currentDosageId";
        patientId = "test";
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withRegimenId(pillRegimenId).build();
        context = new DailyPillReminderContextForTest(new TAMAIVRContextForTest()).pillRegimen(pillRegimenResponse).patientDocumentId(patientId).dtmfInput("1").callStartTime(DateUtil.newDateTime(DateUtil.today(), 16, 0, 0));
    }

    @Test
    public void shouldUpdateTheDeclinedDosageAdherenceLogWithReason() {
        DosageAdherenceLog log = new DosageAdherenceLog(patientId, pillRegimenId, dosageId, null, DosageStatus.NOT_TAKEN, DateUtil.today(), new Time(10, 5), DateUtil.newDateTime(DateUtil.today(), 0, 0, 0));
        when(logs.findByDosageIdAndDate(eq(dosageId), any(LocalDate.class))).thenReturn(log);

        RecordDeclinedDosageReasonCommand command = new RecordDeclinedDosageReasonCommand(logs, null);
        command.executeCommand(context);

        verify(logs).update(any(DosageAdherenceLog.class));
    }
}
