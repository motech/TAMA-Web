package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.domain.Dose;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class UpdatePreviousPillAdherenceCommandTest {
    @Mock
    private DailyPillReminderAdherenceService dailyReminderAdherenceService;
    private DailyPillReminderContextForTest context;
    private UpdateAdherenceCommand command;

    @Before
    public void setup() {
        initMocks(this);
        command = new UpdatePreviousPillAdherenceCommand(null, dailyReminderAdherenceService);
        context = new DailyPillReminderContextForTest(new TAMAIVRContextForTest()).dtmfInput("1");
    }

    @Test
    public void recordsAdherenceToPreviousDosage() {
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, Arrays.asList(
                new DosageResponse("currentDosageId", new Time(19, 0), new LocalDate(2010, 10, 10), null, null, new ArrayList<MedicineResponse>()),
                new DosageResponse("previousDosageId", new Time(9, 0), new LocalDate(2010, 10, 10), null, null, new ArrayList<MedicineResponse>()
                )));
        DateTime doseTakenTime = DateUtil.newDateTime(DateUtil.newDate(2010, 10, 11), 18, 59, 0);
        context.pillRegimen(pillRegimen).patientId("patientId").callDirection(CallDirection.Inbound).callStartTime(doseTakenTime);

        command.executeCommand(context);

        ArgumentCaptor<Dose> doseCaptor = ArgumentCaptor.forClass(Dose.class);
        verify(dailyReminderAdherenceService).recordAdherence(eq("patientId"), eq("regimenId"), doseCaptor.capture(), eq(DosageStatus.TAKEN), eq(doseTakenTime));
        assertEquals("previousDosageId", doseCaptor.getValue().getDosageId());
    }
}
