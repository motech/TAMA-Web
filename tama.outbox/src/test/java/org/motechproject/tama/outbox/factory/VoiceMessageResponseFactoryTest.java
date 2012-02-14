package org.motechproject.tama.outbox.factory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.tama.outbox.OutboxContextForTest;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(value = Suite.class)
@Suite.SuiteClasses({
        VoiceMessageResponseFactoryTest.ThereExistsABuilderThatHandlesTheMessage.class,
        VoiceMessageResponseFactoryTest.MoreThanOneBuilderCanHandleTheMessage.class,
        VoiceMessageResponseFactoryTest.ThereExistsABuilderThatCannotHandleTheMessage.class
})
public class VoiceMessageResponseFactoryTest {

    public static class Basis {

        Set<OutboxMessageBuilder> outboxMessageBuilders;

        @Mock
        KooKooIVRContext kookooIVRContext;
        @Mock
        KookooIVRResponseBuilder ivrResponseBuilder;
        @Mock
        OutboundVoiceMessage outboundVoiceMessage;

        OutboxContextForTest outboxContext;

        VoiceMessageResponseFactory voiceMessageResponseFactory;

        @Before
        public void setUp() {
            outboxMessageBuilders = new LinkedHashSet<OutboxMessageBuilder>();
            voiceMessageResponseFactory = new VoiceMessageResponseFactory(outboxMessageBuilders);
        }
    }

    public static class ThereExistsABuilderThatHandlesTheMessage extends Basis {

        @Before
        public void setUp() {
            super.setUp();
            OutboxMessageBuilder validBuilder = mock(OutboxMessageBuilder.class);
            when(validBuilder.canHandle(outboundVoiceMessage)).thenReturn(true);
            outboxMessageBuilders.add(validBuilder);
        }

        @Test
        public void shouldBuildVoiceMessageResponse() {
            voiceMessageResponseFactory.voiceMessageResponse(kookooIVRContext, outboxContext, outboundVoiceMessage, ivrResponseBuilder);
            verify((OutboxMessageBuilder) outboxMessageBuilders.toArray()[0]).buildVoiceMessageResponse(kookooIVRContext, outboxContext, outboundVoiceMessage, ivrResponseBuilder);
        }
    }

    public static class MoreThanOneBuilderCanHandleTheMessage extends ThereExistsABuilderThatHandlesTheMessage {

        @Before
        public void setUp() {
            super.setUp();
            OutboxMessageBuilder validBuilder = mock(OutboxMessageBuilder.class);
            when(validBuilder.canHandle(outboundVoiceMessage)).thenReturn(true);
            outboxMessageBuilders.add(validBuilder);
        }

        @Test
        public void shouldBuildResponseUsingAllValidBuilders() {
            voiceMessageResponseFactory.voiceMessageResponse(kookooIVRContext, outboxContext, outboundVoiceMessage, ivrResponseBuilder);
            verify((OutboxMessageBuilder) outboxMessageBuilders.toArray()[0]).buildVoiceMessageResponse(kookooIVRContext, outboxContext, outboundVoiceMessage, ivrResponseBuilder);
            verify((OutboxMessageBuilder) outboxMessageBuilders.toArray()[1]).buildVoiceMessageResponse(kookooIVRContext, outboxContext, outboundVoiceMessage, ivrResponseBuilder);
        }
    }

    public static class ThereExistsABuilderThatCannotHandleTheMessage extends Basis {

        @Before
        public void setUp() {
            super.setUp();
            OutboxMessageBuilder validBuilder = mock(OutboxMessageBuilder.class);
            when(validBuilder.canHandle(outboundVoiceMessage)).thenReturn(false);
            outboxMessageBuilders.add(validBuilder);
        }

        @Test
        public void shouldNotBuildVoiceMessageResponseUsingTheBuilder() {
            voiceMessageResponseFactory.voiceMessageResponse(kookooIVRContext, outboxContext, outboundVoiceMessage, ivrResponseBuilder);
            verify((OutboxMessageBuilder) outboxMessageBuilders.toArray()[0], never()).buildVoiceMessageResponse(kookooIVRContext, outboxContext, outboundVoiceMessage, ivrResponseBuilder);
        }
    }
}
