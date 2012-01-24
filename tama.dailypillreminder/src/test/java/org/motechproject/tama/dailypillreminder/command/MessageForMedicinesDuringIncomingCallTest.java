package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.command.ClinicNameMessageBuilder;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


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
    }

    private void setUpContexts() {
        TAMAIVRContextForTest tamaivrContextForTest = new TAMAIVRContextForTest().patientDocumentId("patientId");
        context = new DailyPillReminderContextForTest(tamaivrContextForTest).pillRegimen(new PillRegimen(PillRegimenResponseBuilder.startRecording().withDefaults().build()));
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
        context.callStartTime(timeWithinPillWindow).callDirection(CallDirection.Outbound);
        String[] messages = messageForMedicinesDuringIncomingCall.executeCommand(context);
        assertArrayEquals(new String[]{"welcome_to_someClinicName", TamaIVRMessage.ITS_TIME_FOR_THE_PILL_INCOMING_CALL_INSIDE_PILL_WINDOW, "pillmedicine1", "pillmedicine2", TamaIVRMessage.FROM_THE_BOTTLE_INCOMING_CALL_INSIDE_PILL_WINDOW}, messages);
    }

    @Test
    public void shouldReturnMessagesWithAListOfMedicinesToBeTaken_timeAfterDosagePillWindow() {
        int dosageHour = 10;
        DateTime timeAfterPillWindow = now.withHourOfDay(dosageHour + 3).withMinuteOfHour(5);
        context.callStartTime(timeAfterPillWindow);
        List<DosageResponse> dosages = Arrays.asList(new DosageResponse("currentDosageId", new Time(dosageHour, 5), today.minusDays(2),
                today.plusDays(1), today.minusDays(1),
                Arrays.asList(new MedicineResponse("medicine3", today, today))));
        PillRegimenResponse pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withDosages(dosages).build();

        context.pillRegimen(new PillRegimen(pillRegimenResponse)).callDirection(CallDirection.Outbound);

        String[] messages = messageForMedicinesDuringIncomingCall.executeCommand(context);
        assertArrayEquals(new String[]{"welcome_to_someClinicName", TamaIVRMessage.NOT_REPORTED_IF_TAKEN, "pillmedicine3", TamaIVRMessage.FROM_THE_BOTTLE_INCOMING_CALL_AFTER_PILL_WINDOW}, messages);
    }

    @Test
    public void shouldNotPlayWelcomeMessageDuringMenuRepeat() {
        int dosageHour = 16;
        DateTime timeWithinPillWindow = now.withHourOfDay(dosageHour).withMinuteOfHour(5);
        context.callStartTime(timeWithinPillWindow).callDirection(CallDirection.Outbound);
        context.addLastCompletedTreeToListOfCompletedTrees(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM);

        String[] messages = messageForMedicinesDuringIncomingCall.executeCommand(context);

        assertArrayEquals(new String[]{TamaIVRMessage.ITS_TIME_FOR_THE_PILL_INCOMING_CALL_INSIDE_PILL_WINDOW, "pillmedicine1", "pillmedicine2", TamaIVRMessage.FROM_THE_BOTTLE_INCOMING_CALL_INSIDE_PILL_WINDOW}, messages);
        assertFalse(Arrays.asList(messages).contains("welcome_to_someClinicName"));
        assertFalse(Arrays.asList(messages).contains("welcome_to_clinicName"));
    }
}
