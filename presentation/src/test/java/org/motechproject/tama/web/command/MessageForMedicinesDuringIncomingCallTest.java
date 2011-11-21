package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.repository.AllClinics;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
    private MessageForMedicinesDuringIncomingCall messageForMedicinesDuringIncomingCall;

    private LocalDate today;

    private DateTime now;
    private TAMAIVRContextForTest context;

    @Before
    public void setup() {
        initMocks(this);
        Patient patient = new Patient();
        patient.setClinic_id("clinicId");
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName("clinicName").build();

        messageForMedicinesDuringIncomingCall = new MessageForMedicinesDuringIncomingCall(allPatients, allClinics, null);
        context = new TAMAIVRContextForTest().pillRegimen(PillRegimenResponseBuilder.startRecording().withDefaults().build()).patientId("patientId");
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
        int dosageHour = 16;
        DateTime timeWithinPillWindow = now.withHourOfDay(dosageHour).withMinuteOfHour(5);

        context.dosageId("currentDosageId").callStartTime(timeWithinPillWindow).callDirection(CallDirection.Outbound);

        String[] messages = messageForMedicinesDuringIncomingCall.executeCommand(context);

        assertEquals(5, messages.length);
        assertEquals("welcome_to_clinicName", messages[0]);
        assertEquals("001_02_02_itsTimeForPill1", messages[1]);
        assertEquals("pillmedicine1", messages[2]);
        assertEquals("pillmedicine2", messages[3]);
        assertEquals("001_07_07_fromTheBottle1", messages[4]);
    }

    @Test
    public void shouldReturnMessagesWithAListOfMedicinesToBeTaken_timeAfterDosagePillWindow() {
        int dosageHour = 10;
        DateTime timeAfterPillWindow = now.withHourOfDay(dosageHour + 3).withMinuteOfHour(5);
        context.dosageId("currentDosageId").callStartTime(timeAfterPillWindow);
        List<DosageResponse> dosages = Arrays.asList(new DosageResponse("currentDosageId", new Time(dosageHour, 5), today.minusDays(2),
                today.plusDays(1), today.minusDays(1),
                Arrays.asList(new MedicineResponse("medicine3", today, today))));
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withDosages(dosages).build();

        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(today);

        context.pillRegimen(pillRegimenResponse).callDirection(CallDirection.Outbound);

        String[] messages = messageForMedicinesDuringIncomingCall.executeCommand(context);

        assertEquals(4, messages.length);
        assertEquals("welcome_to_clinicName", messages[0]);
        assertEquals("010_02_04_notReportedIfTaken", messages[1]);
        assertEquals("pillmedicine3", messages[2]);
        assertEquals("001_07_07_fromTheBottle1", messages[3]);
    }
    
    @Test
    public void shouldNotPlayWelcomeMessageDuringMenuRepeat(){
        int dosageHour = 16;
        DateTime timeWithinPillWindow = now.withHourOfDay(dosageHour).withMinuteOfHour(5);
        context.dosageId("currentDosageId").callStartTime(timeWithinPillWindow).callDirection(CallDirection.Outbound);
        context.addLastCompletedTreeToListOfCompletedTrees(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM);
        
        String[] messages = messageForMedicinesDuringIncomingCall.executeCommand(context);

        assertEquals(4, messages.length);
        assertFalse(Arrays.asList(messages).contains("welcome_to_clinicName"));
        assertEquals("001_02_02_itsTimeForPill1", messages[0]);
        assertEquals("pillmedicine1", messages[1]);
        assertEquals("pillmedicine2", messages[2]);
        assertEquals("001_07_07_fromTheBottle1", messages[3]);
    }
}
