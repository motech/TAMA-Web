package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.AllClinics;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class MessageForMedicinesDuringIncomingCallTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllClinics allClinics;
    @Mock
    private IVRContext context;
    @Mock
    private IVRRequest ivrRequest;
    @Mock
    private IVRSession ivrSession;

    private MessageForMedicinesDuringIncomingCall messageForMedicinesDuringIncomingCall;

    private LocalDate today;

    private DateTime now;

    @Before
    public void setup() {
        initMocks(this);
        Patient patient = new Patient();
        patient.setClinic_id("clinicId");
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName("clinicName").build();

        messageForMedicinesDuringIncomingCall = new MessageForMedicinesDuringIncomingCall(allPatients, allClinics);

        when(context.ivrSession()).thenReturn(ivrSession);
        when(ivrSession.getPillRegimen()).thenReturn(PillRegimenResponseBuilder.startRecording().withDefaults().build());
        when(context.ivrRequest()).thenReturn(ivrRequest);
        when(ivrSession.getPatientId()).thenReturn("patientId");
        when(allPatients.get("patientId")).thenReturn(patient);
        when(allClinics.get("clinicId")).thenReturn(clinic);

        today = DateUtil.today();
        now = DateUtil.now();

        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.now()).thenReturn(now);

    }

    @Test
    public void shouldReturnMessagesWithAListOfMedicinesToBeTaken_timeWithinDosagePillWindow() {
        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.DOSAGE_ID, "currentDosageId");

        int dosageHour = 16;
        DateTime timeWithinPillWindow = now.withHourOfDay(dosageHour).withMinuteOfHour(5);

        when(ivrRequest.getTamaParams()).thenReturn(params);
        when(ivrSession.getCallTime()).thenReturn(timeWithinPillWindow);

        String[] messages = messageForMedicinesDuringIncomingCall.execute(context);

        assertEquals(5, messages.length);
        assertEquals("welcome_to_clinicName", messages[0]);
        assertEquals("001_02_02_itsTimeForPill1", messages[1]);
        assertEquals("medicine1", messages[2]);
        assertEquals("medicine2", messages[3]);
        assertEquals("001_07_07_fromTheBottle1", messages[4]);
    }


    @Test
    public void shouldReturnMessagesWithAListOfMedicinesToBeTaken_timeAfterDosagePillWindow() {
        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.DOSAGE_ID, "currentDosageId");

        int dosageHour = 10;
        DateTime timeAfterPillWindow = now.withHourOfDay(dosageHour + 3).withMinuteOfHour(5);
        List<DosageResponse> dosages = Arrays.asList(new DosageResponse("currentDosageId", new Time(dosageHour, 5), today.minusDays(2),
                today.plusDays(1), today.minusDays(1),
                Arrays.asList(new MedicineResponse("medicine3", today, today))));
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withDosages(dosages).build();

        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(today);

        when(ivrRequest.getTamaParams()).thenReturn(params);
        when(ivrSession.getCallTime()).thenReturn(timeAfterPillWindow);
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);

        String[] messages = messageForMedicinesDuringIncomingCall.execute(context);

        assertEquals(4, messages.length);
        assertEquals("welcome_to_clinicName", messages[0]);
        assertEquals("010_02_04_notReportedIfTaken", messages[1]);
        assertEquals("medicine3", messages[2]);
        assertEquals("001_07_07_fromTheBottle1", messages[3]);
    }
}
