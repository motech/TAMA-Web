package org.motechproject.tama.appointments;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.tama.appointments.matchers.AppointmentMessageFor;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.outbox.OutboxContextForTest;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(value = Suite.class)
@Suite.SuiteClasses({
        AppointmentReminderMessageBuilderTest.VoiceMessageTypeIsAppointmentReminder.class,
        AppointmentReminderMessageBuilderTest.VoiceMessageTypeIsNotAppointmentReminder.class
})
public class AppointmentReminderMessageBuilderTest {

    public static class Basis {

        final String PARTY_ID = "party_id";

        @Mock
        KooKooIVRContext kookooIVRContext;
        OutboxContext outboxContext;
        KookooIVRResponseBuilder ivrResponseBuilder;

        Patient patient;
        @Mock
        AllPatients allPatients;

        Clinic clinic;

        @Mock
        OutboundVoiceMessage outboundVoiceMessage;
        AppointmentReminderMessageBuilder appointmentReminderMessageBuilder;

        @Before
        public void setup() {
            initMocks(this);
            initOutboxContext();
            initOutboundVoiceMessage();
            initPatient();
            ivrResponseBuilder = new KookooIVRResponseBuilder();
            appointmentReminderMessageBuilder = new AppointmentReminderMessageBuilder(allPatients);
        }

        private void initPatient() {
            clinic = ClinicBuilder.startRecording().withDefaults().build();
            patient = PatientBuilder.startRecording().withDefaults().withId(PARTY_ID).withClinic(clinic).build();
            when(allPatients.get(PARTY_ID)).thenReturn(patient);
        }

        private void initOutboundVoiceMessage() {
            when(outboundVoiceMessage.getPartyId()).thenReturn(PARTY_ID);
        }

        private void initOutboxContext() {
            outboxContext = new OutboxContextForTest().partyId(PARTY_ID);
        }
    }

    public static class VoiceMessageTypeIsAppointmentReminder extends Basis {

        @Before
        public void setup() {
            super.setup();
            VoiceMessageType voiceMessageType = new VoiceMessageType();
            voiceMessageType.setVoiceMessageTypeName(TAMAConstants.APPOINTMENT_REMINDER_VOICE_MESSAGE);
            when(outboundVoiceMessage.getVoiceMessageType()).thenReturn(voiceMessageType);
        }

        @Test
        public void shouldReturnCanHandleVoiceMessage() {
            assertTrue(appointmentReminderMessageBuilder.canHandle(outboundVoiceMessage));
        }

        @Test
        public void shouldAddAppointmentReminderToResponse() {
            appointmentReminderMessageBuilder.buildVoiceMessageResponse(kookooIVRContext, outboxContext, outboundVoiceMessage, ivrResponseBuilder);
            assertThat(ivrResponseBuilder.getPlayAudios(), is(new AppointmentMessageFor(patient)));
        }
    }

    public static class VoiceMessageTypeIsNotAppointmentReminder extends Basis {

        @Before
        public void setup() {
            super.setup();
            VoiceMessageType voiceMessageType = new VoiceMessageType();
            voiceMessageType.setVoiceMessageTypeName("");
            when(outboundVoiceMessage.getVoiceMessageType()).thenReturn(voiceMessageType);
        }

        @Test
        public void shouldNotReturnCanHandleVoiceMessage() {
            assertFalse(appointmentReminderMessageBuilder.canHandle(outboundVoiceMessage));
        }
    }
}
