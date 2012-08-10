package org.motechproject.tama.fourdayrecall.factory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.VoiceMessageType;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;

import static junit.framework.Assert.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(value = Suite.class)
@Suite.SuiteClasses({
        WeeklyOutboxMessageBuilderTest.PatientIsOnFourDayRecall.VoiceMessageTypeIsAudioCommand.class,
        WeeklyOutboxMessageBuilderTest.PatientIsOnFourDayRecall.VoiceMessageTypeIsNotAudioCommand.class,
        WeeklyOutboxMessageBuilderTest.PatientIsNotOnFourDayRecall.VoiceMessageTypeIsAudioCommand.class
})
public class WeeklyOutboxMessageBuilderTest {

    public static class Basis {

        final String PARTY_ID = "party_id";
        @Mock
        KookooIVRResponseBuilder ivrResponseBuilder;
        @Mock
        AllPatients allPatients;
        @Mock
        OutboundVoiceMessage outboundVoiceMessage;

        Patient patient;
        WeeklyOutboxMessageBuilder weeklyOutboxMessageFactory;

        @Before
        public void setup() {
            initMocks(this);
            when(outboundVoiceMessage.getExternalId()).thenReturn(PARTY_ID);
            ivrResponseBuilder = new KookooIVRResponseBuilder();
            weeklyOutboxMessageFactory = new WeeklyOutboxMessageBuilder(allPatients);
        }
    }

    public static class PatientIsOnFourDayRecall extends Basis {

        @Before
        public void setup() {
            super.setup();
            patient = PatientBuilder.startRecording().withDefaults().withId(PARTY_ID).withCallPreference(CallPreference.FourDayRecall).build();
            when(allPatients.get(PARTY_ID)).thenReturn(patient);
        }

        public static class VoiceMessageTypeIsAudioCommand extends PatientIsOnFourDayRecall {
            @Before
            public void setup() {
                super.setup();
                VoiceMessageType voiceMessageType = new VoiceMessageType();
                voiceMessageType.setVoiceMessageTypeName(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
                when(outboundVoiceMessage.getVoiceMessageType()).thenReturn(voiceMessageType);
            }

            @Test
            public void shouldReturnCanHandle() {
                assertTrue(weeklyOutboxMessageFactory.canHandle(outboundVoiceMessage));
            }

            @Test
            public void shouldReturnPlayTexts() {
                weeklyOutboxMessageFactory.buildVoiceMessageResponse(null, null, null, ivrResponseBuilder);

                assertEquals(1, ivrResponseBuilder.getPlayTexts().size());
            }
        }

        public static class VoiceMessageTypeIsNotAudioCommand extends PatientIsOnFourDayRecall {

            @Before
            public void setup() {
                super.setup();
                VoiceMessageType voiceMessageType = new VoiceMessageType();
                voiceMessageType.setVoiceMessageTypeName("");
                when(outboundVoiceMessage.getVoiceMessageType()).thenReturn(voiceMessageType);
            }

            @Test
            public void shouldNotReturnCanHandle() {
                assertFalse(weeklyOutboxMessageFactory.canHandle(outboundVoiceMessage));
            }
        }
    }

    public static class PatientIsNotOnFourDayRecall extends Basis {

        @Before
        public void setup() {
            super.setup();
            patient = PatientBuilder.startRecording().withDefaults().withId(PARTY_ID).withCallPreference(CallPreference.DailyPillReminder).build();
            when(allPatients.get(PARTY_ID)).thenReturn(patient);
        }

        public static class VoiceMessageTypeIsAudioCommand extends PatientIsNotOnFourDayRecall {

            @Before
            public void setup() {
                super.setup();
                VoiceMessageType voiceMessageType = new VoiceMessageType();
                voiceMessageType.setVoiceMessageTypeName(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
                when(outboundVoiceMessage.getVoiceMessageType()).thenReturn(voiceMessageType);
            }

            @Test
            public void shouldNotReturnCanHandle() {
                assertFalse(weeklyOutboxMessageFactory.canHandle(outboundVoiceMessage));
            }
        }
    }


}
