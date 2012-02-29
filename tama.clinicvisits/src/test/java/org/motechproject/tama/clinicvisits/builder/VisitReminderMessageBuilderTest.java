package org.motechproject.tama.clinicvisits.builder;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.OutboxContextForTest;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static junit.framework.Assert.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class VisitReminderMessageBuilderTest extends BaseUnitTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private AppointmentService appointmentService;

    private OutboxContextForTest outboxContext;
    private AppointmentCalendar appointmentCalendar;
    private OutboundVoiceMessage visitReminderVoiceMessage;
    private Visit confirmedVisit;
    private Patient patientWithLanguagePreference;

    private KookooIVRResponseBuilder kookooIVRResponseBuilder = new KookooIVRResponseBuilder();
    private VisitReminderMessageBuilder visitReminderMessageBuilder;

    private DateTime monday;

    public VisitReminderMessageBuilderTest() {
        initMocks(this);
        mockTime();
        setupPatient();
        setupAppointmentCalendar();
        setupVisitReminderVoiceMessage();
        visitReminderMessageBuilder = new VisitReminderMessageBuilder(allPatients, appointmentService);
    }

    private void setupPatient() {
        patientWithLanguagePreference = PatientBuilder
                .startRecording()
                .withDefaults()
                .withId("patientId")
                .withIVRLanguage(IVRLanguage.newIVRLanguage("English", "en")).build();
        when(allPatients.get("patientId")).thenReturn(patientWithLanguagePreference);
    }

    private void mockTime() {
        monday = DateUtil.newDateTime(2012, 2, 27, 10, 0, 0);
        mockCurrentDate(monday);
    }

    private void setupAppointmentCalendar() {
        setupVisit();
        appointmentCalendar = new AppointmentCalendar().addVisit(confirmedVisit);
    }

    private void setupVisit() {
        confirmedVisit = new Visit().name("visitName");
        confirmedVisit.addAppointment(monday.minusDays(3), new Reminder());
        confirmedVisit.appointment().confirmedDate(monday);
        when(appointmentService.findVisit("patientId", "visitName")).thenReturn(new VisitResponse(confirmedVisit));
    }

    private void setupVisitReminderVoiceMessage() {
        VoiceMessageType voiceMessageType = voiceMessageTypeIsVisitReminder();
        visitReminderVoiceMessage = new OutboundVoiceMessage();
        visitReminderVoiceMessage.setVoiceMessageType(voiceMessageType);
        visitReminderVoiceMessage.setPartyId("patientId");
        setupParametersWithVisitName();
    }

    private VoiceMessageType voiceMessageTypeIsVisitReminder() {
        VoiceMessageType voiceMessageType = new VoiceMessageType();
        voiceMessageType.setVoiceMessageTypeName(TAMAConstants.VISIT_REMINDER_VOICE_MESSAGE);
        return voiceMessageType;
    }

    private void setupParametersWithVisitName() {
        HashMap<String, Object> map = new HashMap<String, Object>() {{
            put(TAMAConstants.MESSAGE_PARAMETER_VISIT_NAME, "visitName");
        }};
        visitReminderVoiceMessage.setParameters(map);
        when(appointmentService.getAppointmentCalendar("patientId")).thenReturn(appointmentCalendar);
    }

    @Test
    public void shouldHandleVisitReminderMessage() {
        assertTrue(visitReminderMessageBuilder.canHandle(visitReminderVoiceMessage));
    }

    @Test
    public void shouldNotHandleMessageIfItIsNotAVisitReminderMessage() {
        assertFalse(visitReminderMessageBuilder.canHandle(new OutboundVoiceMessage()));
    }

    @Test
    public void shouldAddDayOfWeekToVisitReminderMessage() {
        visitReminderMessageBuilder.buildVoiceMessageResponse(kookooIVRContext, outboxContext, visitReminderVoiceMessage, kookooIVRResponseBuilder);
        assertEquals("weekday_Monday", kookooIVRResponseBuilder.getPlayAudios().get(1));
    }

    @Test
    public void shouldAddDayToVisitReminderMessage() {
        visitReminderMessageBuilder.buildVoiceMessageResponse(kookooIVRContext, outboxContext, visitReminderVoiceMessage, kookooIVRResponseBuilder);
        assertEquals("dates_27th", kookooIVRResponseBuilder.getPlayAudios().get(3));
    }

    @Test
    public void shouldAddMonthToVisitReminderMessage() {
        visitReminderMessageBuilder.buildVoiceMessageResponse(kookooIVRContext, outboxContext, visitReminderVoiceMessage, kookooIVRResponseBuilder);
        assertEquals("month_February", kookooIVRResponseBuilder.getPlayAudios().get(4));
    }

    @Test
    public void shouldAddTimeConstructToVisitReminderMessage() {
        visitReminderMessageBuilder.buildVoiceMessageResponse(kookooIVRContext, outboxContext, visitReminderVoiceMessage, kookooIVRResponseBuilder);
        assertEquals("Num_010", kookooIVRResponseBuilder.getPlayAudios().get(6));
        assertEquals("timeofDayAM", kookooIVRResponseBuilder.getPlayAudios().get(7));
    }

    @Test
    public void shouldAddFillerMessagesToVisitReminderMessage() {
        visitReminderMessageBuilder.buildVoiceMessageResponse(kookooIVRContext, outboxContext, visitReminderVoiceMessage, kookooIVRResponseBuilder);
        assertEquals("M07b_01_yourNextClinicVisit", kookooIVRResponseBuilder.getPlayAudios().get(0));
        assertEquals("M07b_03_yourNextClinicVisit3", kookooIVRResponseBuilder.getPlayAudios().get(2));
        assertEquals("M07b_06_yourNextClinicVisit4", kookooIVRResponseBuilder.getPlayAudios().get(5));
        assertEquals("M07b_08_yourNextClinicVisit5", kookooIVRResponseBuilder.getPlayAudios().get(8));
    }
}