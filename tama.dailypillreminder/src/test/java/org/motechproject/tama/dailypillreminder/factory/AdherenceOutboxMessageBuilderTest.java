package org.motechproject.tama.dailypillreminder.factory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.VoiceMessageType;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.command.PlayAdherenceTrendFeedbackCommand;
import org.motechproject.tama.dailypillreminder.outbox.AdherenceOutboxMessageBuilder;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


@RunWith(value = Suite.class)
@Suite.SuiteClasses({
        AdherenceOutboxMessageBuilderTest.PatientIsOnDailyPillReminder.VoiceMessageTypeIsAudioCommand.class,
        AdherenceOutboxMessageBuilderTest.PatientIsNotOnDailyPillReminder.VoiceMessageTypeIsAudio.class,
        AdherenceOutboxMessageBuilderTest.PatientIsOnDailyPillReminder.VoiceMessageTypeIsNotAudioCommand.class
})
public class AdherenceOutboxMessageBuilderTest {

    public static class Basis {

        final String PARTY_ID = "patientId";
        @Mock
        PlayAdherenceTrendFeedbackCommand playAdherenceTrendFeedbackCommand;
        @Mock
        OutboxContext outboxContext;
        @Mock
        OutboundVoiceMessage outboundVoiceMessage;
        @Mock
        AllPatients allPatients;

        Patient patient;
        AdherenceOutboxMessageBuilder adherenceOutboxMessageBuilder;

        @Before
        public void setUp() {
            initMocks(this);
            when(outboxContext.partyId()).thenReturn(PARTY_ID);
            when(outboundVoiceMessage.getPartyId()).thenReturn(PARTY_ID);
            adherenceOutboxMessageBuilder = new AdherenceOutboxMessageBuilder(playAdherenceTrendFeedbackCommand, allPatients);
        }
    }

    public static class PatientIsOnDailyPillReminder extends Basis {

        @Before
        public void setUp() {
            super.setUp();
            patient = PatientBuilder.startRecording().withDefaults().withId(PARTY_ID)
                    .withCallPreference(CallPreference.DailyPillReminder).build();
            when(allPatients.get(PARTY_ID)).thenReturn(patient);
        }

        public static class VoiceMessageTypeIsAudioCommand extends PatientIsOnDailyPillReminder {

            KookooIVRResponseBuilder ivrResponseBuilder = new KookooIVRResponseBuilder();

            @Before
            public void setUp() {
                super.setUp();
                VoiceMessageType voiceMessageType = new VoiceMessageType();
                voiceMessageType.setVoiceMessageTypeName(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
                when(outboundVoiceMessage.getVoiceMessageType()).thenReturn(voiceMessageType);
            }

            @Test
            public void shouldHandleTheMessage() {
                assertTrue(adherenceOutboxMessageBuilder.canHandle(outboundVoiceMessage));
            }

            @Test
            public void shouldAddAdherenceMessageToTheResponse() {
                when(playAdherenceTrendFeedbackCommand.execute(PARTY_ID)).thenReturn(new String[]{"percentage", "trend"});
                adherenceOutboxMessageBuilder.buildVoiceMessageResponse(null, outboxContext, outboundVoiceMessage, ivrResponseBuilder);
                assertEquals("percentage", ivrResponseBuilder.getPlayAudios().get(0));
            }

            @Test
            public void shouldAddAdherenceTrendMessageToResponse() {
                when(playAdherenceTrendFeedbackCommand.execute(PARTY_ID)).thenReturn(new String[]{"percentage", "trend"});
                adherenceOutboxMessageBuilder.buildVoiceMessageResponse(null, outboxContext, outboundVoiceMessage, ivrResponseBuilder);
                assertEquals("trend", ivrResponseBuilder.getPlayAudios().get(1));
            }
        }

        public static class VoiceMessageTypeIsNotAudioCommand extends PatientIsOnDailyPillReminder {

            @Before
            public void setUp() {
                super.setUp();
                VoiceMessageType voiceMessageType = new VoiceMessageType();
                voiceMessageType.setVoiceMessageTypeName("outbox");
                when(outboundVoiceMessage.getVoiceMessageType()).thenReturn(voiceMessageType);
            }

            @Test
            public void shouldHandleTheMessage() {
                assertFalse(adherenceOutboxMessageBuilder.canHandle(outboundVoiceMessage));
            }
        }
    }

    public static class PatientIsNotOnDailyPillReminder extends Basis {

        @Before
        public void setUp() {
            super.setUp();
            patient = PatientBuilder.startRecording().withDefaults().withId(PARTY_ID)
                    .withCallPreference(CallPreference.FourDayRecall).build();
            when(allPatients.get(PARTY_ID)).thenReturn(patient);
        }

        public static class VoiceMessageTypeIsAudio extends PatientIsNotOnDailyPillReminder {

            @Before
            public void setUp() {
                super.setUp();
                VoiceMessageType voiceMessageType = new VoiceMessageType();
                voiceMessageType.setVoiceMessageTypeName(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
                when(outboundVoiceMessage.getVoiceMessageType()).thenReturn(voiceMessageType);
            }

            @Test
            public void shouldNotHandleTheMessage() {
                assertFalse(adherenceOutboxMessageBuilder.canHandle(outboundVoiceMessage));
            }
        }
    }
}
