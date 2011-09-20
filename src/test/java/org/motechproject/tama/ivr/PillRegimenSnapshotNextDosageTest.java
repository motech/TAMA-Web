package org.motechproject.tama.ivr;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class PillRegimenSnapshotNextDosageTest {
    @Mock
    private IVRSession ivrSession;
    @Mock
    private IVRRequest ivrRequest;

    private IVRContext ivrContext;
    private PillRegimenResponse pillRegimen;
    private PillRegimenSnapshot pillRegimenSnapshot;

    @Before
    public void setUp() {
        initMocks(this);

        pillRegimen = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        ivrContext = new IVRContext(ivrRequest, ivrSession);

        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimen);
        when(ivrRequest.getParameter(PillReminderCall.DOSAGE_ID)).thenReturn("currentDosageId");
    }

    @Test
    public void shouldGetNextDosageTimeWhenNextDosageIsTomorrow_AndCurrentDoseIsInTheEvening() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        dosages.add(new DosageResponse("currentDosageId", new Time(20, 5), null, null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("nextDosageId", new Time(10, 5), null, null, null, new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimen);
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2010, 10, 10, 19, 00, 00));
        when(ivrSession.getCallTime()).thenReturn(new DateTime(2010, 10, 10, 19, 00, 00));

        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        DateTime nextDosageTime = pillRegimenSnapshot.getNextDosageTime();

        verifyStatic();
        assertEquals(10, nextDosageTime.getHourOfDay());
        assertEquals(05, nextDosageTime.getMinuteOfHour());
        assertEquals(11, nextDosageTime.getDayOfMonth());
    }

    @Test
    public void shouldGetNextDosageTimeWhenNextDosageIsToday_AndCurrentDoseIsInTheMorning() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), null, null, null, new ArrayList<MedicineResponse>()));
        dosages.add(new DosageResponse("nextDosageId", new Time(20, 5), null, null, null, new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimen);
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2010, 10, 10, 12, 00, 00));
        when(ivrSession.getCallTime()).thenReturn(new DateTime(2010, 10, 10, 12, 00, 00));

        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        DateTime nextDosageTime = pillRegimenSnapshot.getNextDosageTime();

        verifyStatic();
        assertEquals(20, nextDosageTime.getHourOfDay());
        assertEquals(05, nextDosageTime.getMinuteOfHour());
        assertEquals(10, nextDosageTime.getDayOfMonth());
    }

    @Test
    public void shouldGetNextDosageTimeWhenNextDosageIsToday_AndCurrentDoseIsInTheMorningForDailyDosage() {
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        dosages.add(new DosageResponse("currentDosageId", new Time(10, 5), null, null, null, new ArrayList<MedicineResponse>()));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimen);
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2010, 10, 10, 9, 00, 00));
        when(ivrSession.getCallTime()).thenReturn(new DateTime(2010, 10, 10, 9, 00, 00));

        pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext);
        DateTime nextDosageTime = pillRegimenSnapshot.getNextDosageTime();

        verifyStatic();
        assertEquals(10, nextDosageTime.getHourOfDay());
        assertEquals(05, nextDosageTime.getMinuteOfHour());
        assertEquals(11, nextDosageTime.getDayOfMonth());
    }

}
