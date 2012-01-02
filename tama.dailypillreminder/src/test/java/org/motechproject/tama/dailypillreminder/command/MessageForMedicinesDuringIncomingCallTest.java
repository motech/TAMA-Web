package org.motechproject.tama.dailypillreminder.command;

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
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.command.ClinicNameMessageBuilder;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
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

    @Mock
    private ClinicNameMessageBuilder clinicNameMessageBuilder;

    private MessageForMedicinesDuringIncomingCall messageForMedicinesDuringIncomingCall;

    private LocalDate today;

    private DateTime now;

    private DailyPillReminderContextForTest context;

    private Patient patient;

    private Clinic clinic;

    private void setUpTime() {
        today = DateUtil.today();
        now = DateUtil.now();
        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.now()).thenReturn(now);
    }

    private void setUpContexts() {
        TAMAIVRContextForTest tamaivrContextForTest = new TAMAIVRContextForTest().patientId("patientId");
        context = new DailyPillReminderContextForTest(tamaivrContextForTest).pillRegimen(PillRegimenResponseBuilder.startRecording().withDefaults().build());
    }

    @Before
    public void setup() {
        initMocks(this);
        clinic = ClinicBuilder.startRecording().withDefaults().withName("clinicName").withId("clinicId").build();
        patient = PatientBuilder.startRecording().withDefaults().withClinic(clinic).withIVRLanguage(IVRLanguage.newIVRLanguage("English", "en")).build();
        when(allPatients.get("patientId")).thenReturn(patient);
        when(allClinics.get("clinicId")).thenReturn(clinic);
        when(clinicNameMessageBuilder.getInboundMessage(clinic, patient.getPatientPreferences().getIvrLanguage())).thenReturn("welcome_to_someClinicName");

        messageForMedicinesDuringIncomingCall = new MessageForMedicinesDuringIncomingCall(allPatients, allClinics, null, clinicNameMessageBuilder);
        setUpContexts();
        setUpTime();
    }

    @Test
    public void shouldReturnMessagesWithAListOfMedicinesToBeTaken_timeWithinDosagePillWindow() {
        int dosageHour = 16;
        DateTime timeWithinPillWindow = now.withHourOfDay(dosageHour).withMinuteOfHour(5);
        context.dosageId("currentDosageId").callStartTime(timeWithinPillWindow).callDirection(CallDirection.Outbound);
        String[] messages = messageForMedicinesDuringIncomingCall.executeCommand(context);
        assertArrayEquals(new String[]{"welcome_to_someClinicName", "001_02_02_itsTimeForPill1", "pillmedicine1", "pillmedicine2", "001_07_07_fromTheBottle1"}, messages);
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
        assertArrayEquals(new String[]{"welcome_to_someClinicName", "010_02_04_notReportedIfTaken", "pillmedicine3", "010_02_06_fromTheBottle2"}, messages);
    }

    @Test
    public void shouldNotPlayWelcomeMessageDuringMenuRepeat() {
        int dosageHour = 16;
        DateTime timeWithinPillWindow = now.withHourOfDay(dosageHour).withMinuteOfHour(5);
        context.dosageId("currentDosageId").callStartTime(timeWithinPillWindow).callDirection(CallDirection.Outbound);
        context.addLastCompletedTreeToListOfCompletedTrees(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM);

        String[] messages = messageForMedicinesDuringIncomingCall.executeCommand(context);

        assertArrayEquals(new String[]{"001_02_02_itsTimeForPill1", "pillmedicine1", "pillmedicine2", "001_07_07_fromTheBottle1"}, messages);
        assertFalse(Arrays.asList(messages).contains("welcome_to_someClinicName"));
        assertFalse(Arrays.asList(messages).contains("welcome_to_clinicName"));
    }
}
