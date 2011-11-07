package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.service.ivr.CallDirection;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class UpdateAdherenceCommandTest {

    private String pillRegimenId;
    private String dosageId;
    private String patientId;
    private DosageAdherenceLog log;

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
        context = new TAMAIVRContextForTest().dosageId(dosageId).pillRegimen(pillRegimenResponse).patientId(patientId).dtmfInput("1").callDirection(CallDirection.Outbound).callStartTime(DateUtil.now());
    }

    @Test
    public void shouldCreateAnAdherenceLogIfThereIsNoLogFound() {
        when(logs.findByDosageIdAndDate(eq(dosageId), any(LocalDate.class))).thenReturn(null);

        UpdateAdherenceCommand command = new UpdateAdherenceCommand(logs, null);
        command.executeCommand(context);

        verify(logs).add(any(DosageAdherenceLog.class));
    }

    @Test
    public void shouldUpdateAnAdherenceLogIfThereIsAlreadyOneForTheCurrentDate() {
        log = new DosageAdherenceLog(patientId, pillRegimenId, dosageId, DosageStatus.NOT_TAKEN, DateUtil.today());

        when(logs.findByDosageIdAndDate(eq(dosageId), any(LocalDate.class))).thenReturn(log);

        UpdateAdherenceCommand command = new UpdateAdherenceCommand(logs, null);
        command.executeCommand(context);

        verify(logs, never()).add(any(DosageAdherenceLog.class));
        verify(logs).update(log);
    }

    @Test
    public void shouldNotUpdateOrCreateIfThereAreNotAnyChanges() {
        context.dtmfInput("3");

        log = new DosageAdherenceLog(patientId, pillRegimenId, dosageId, DosageStatus.NOT_TAKEN, DateUtil.today());

        when(logs.findByDosageIdAndDate(eq(dosageId), any(LocalDate.class))).thenReturn(log);

        UpdateAdherenceCommand command = new UpdateAdherenceCommand(logs, null);
        command.executeCommand(context);

        verify(logs, never()).add(any(DosageAdherenceLog.class));
        verify(logs, never()).update(log);
    }

    @Test
    public void testShouldRecordDosageForYesterdayDoseIfPatientCallsBeforePillWindowOfTodaysDosage() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();

        dosages.add(new DosageResponse("currentDosageId", new Time(19, 0), new LocalDate(2010, 10, 10), null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("nextDosageId", new Time(9, 0), new LocalDate(2010, 10, 10), null, null, new ArrayList<MedicineResponse>()));

        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        context.pillRegimen(pillRegimen).callDirection(CallDirection.Inbound).callStartTime(DateUtil.newDateTime(DateUtil.newDate(2010, 10, 11), 6, 59, 0));
        when(logs.findByDosageIdAndDate("currentDosageId", new LocalDate(2010, 10, 10))).thenReturn(null);
        UpdateAdherenceCommand command = new UpdateAdherenceCommand(logs, null);
        command.executeCommand(context);

        ArgumentCaptor<DosageAdherenceLog> logCapture = ArgumentCaptor.forClass(DosageAdherenceLog.class);
        verify(logs).add(logCapture.capture());

        DosageAdherenceLog capturedLog = logCapture.getValue();
        assertEquals("currentDosageId", capturedLog.getDosageId());
        assertEquals(new LocalDate(2010, 10, 10), capturedLog.getDosageDate());
    }
}
