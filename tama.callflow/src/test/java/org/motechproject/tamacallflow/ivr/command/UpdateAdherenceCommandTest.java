package org.motechproject.tamacallflow.ivr.command;

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
import org.motechproject.tamacallflow.domain.DosageStatus;
import org.motechproject.tamacallflow.ivr.Dose;
import org.motechproject.tamacallflow.ivr.TAMAIVRContextForTest;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceService;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class UpdateAdherenceCommandTest {

    @Mock
    private DailyReminderAdherenceService dailyReminderAdherenceService;
    private TAMAIVRContextForTest context;
    private UpdateAdherenceCommand command;

    @Before
    public void setup() {
        initMocks(this);
        command = new UpdateAdherenceCommand(null, dailyReminderAdherenceService);
        context = new TAMAIVRContextForTest().dtmfInput("1");
    }

    @Test
    public void recordsAdherenceToCurrentDosage() {
        PillRegimenResponse pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, Arrays.asList(
                new DosageResponse("currentDosageId", new Time(19, 0), new LocalDate(2010, 10, 10), null, null, new ArrayList<MedicineResponse>()),
                new DosageResponse("nextDosageId", new Time(9, 0), new LocalDate(2010, 10, 10), null, null, new ArrayList<MedicineResponse>()
                )));
        DateTime doseTakenTime = DateUtil.newDateTime(DateUtil.newDate(2010, 10, 11), 18, 59, 0);
        context.patientId("patientId").pillRegimen(pillRegimen).callDirection(CallDirection.Inbound).callStartTime(doseTakenTime);

        command.executeCommand(context);

        ArgumentCaptor<Dose> doseCaptor = ArgumentCaptor.forClass(Dose.class);
        verify(dailyReminderAdherenceService).recordAdherence(eq("patientId"), eq("regimenId"), doseCaptor.capture(), eq(DosageStatus.TAKEN), eq(doseTakenTime));
        assertEquals("currentDosageId", doseCaptor.getValue().getDosageId());
    }

}
