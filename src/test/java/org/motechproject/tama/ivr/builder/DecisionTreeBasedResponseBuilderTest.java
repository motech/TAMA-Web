package org.motechproject.tama.ivr.builder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.*;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRSession;

import java.util.Arrays;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DecisionTreeBasedResponseBuilderTest {
    private DecisionTreeBasedResponseBuilder treeBasedResponseBuilder;

    @Mock
    IVRContext ivrContext;
    @Mock
    IVRSession ivrSession;
    
    @Before
    public void setUp() {
        treeBasedResponseBuilder = new DecisionTreeBasedResponseBuilder();
        initMocks(this);
        when(ivrSession.getPrefferedLanguageCode()).thenReturn("en");
        when(ivrContext.ivrSession()).thenReturn(ivrSession);
    }

    @Test
    public void shouldAddCollectDtmfIfTheNodeHasTransitions() {
        Node rootNode = Node.newBuilder()
                .setPrompts(Arrays.asList(new AudioPrompt().setName("foo")))
                .setTransitions(new Object[][]{
                        {"1", Transition.newBuilder()
                                .setDestinationNode(Node.newBuilder()
                                                .setPrompts(Arrays.asList(new AudioPrompt().setName("bar"))).build()).build()
                        },
                        {"2", Transition.newBuilder()
                                .setDestinationNode(Node.newBuilder()
                                                .setPrompts(Arrays.asList(new AudioPrompt().setName("baz"))).build()).build()
                        }}).build();
        IVRResponseBuilder responseBuilder = nextResponse(rootNode, false);
        assertTrue(responseBuilder.isCollectDtmf());
        assertEquals(1, responseBuilder.getPlayAudios().size());
        assertEquals(0, responseBuilder.getPlayTexts().size());
    }

    private IVRResponseBuilder nextResponse(Node rootNode, boolean retryOnIncorrectUserAction) {
        return treeBasedResponseBuilder.ivrResponse("foo", rootNode, ivrContext, retryOnIncorrectUserAction);
    }

    @Test
    public void shouldAddAddHangupIfTheNodeDoesNotHaveAnyTransitions() {
        Node rootNode = Node.newBuilder()
                .setPrompts(Arrays.asList(new AudioPrompt().setName("foo")))
                .build();
        IVRResponseBuilder responseBuilder = nextResponse(rootNode, false);
        assertFalse(responseBuilder.isCollectDtmf());
        assertTrue(responseBuilder.isHangUp());
        assertEquals(2, responseBuilder.getPlayAudios().size());
        assertEquals(0, responseBuilder.getPlayTexts().size());
    }

    @Test
    public void whenAudioCommandReturnsNullThenItShouldNotGetAddedToResponse() {
        Node rootNode = Node.newBuilder()
                .setPrompts(Arrays.asList(new AudioPrompt().setCommand(new ReturnEmptyCommand())))
                .build();
        IVRResponseBuilder responseBuilder = nextResponse(rootNode, false);
        assertEquals(1, responseBuilder.getPlayAudios().size());
    }

    @Test
    public void createMultiplePlayAudiosWhenACommandReturnsMultiplePrompts() {
        Node rootNode = Node.newBuilder()
                .setPrompts(Arrays.asList(new AudioPrompt().setCommand(new ReturnMultiplePromptCommand())))
                .build();
        IVRResponseBuilder responseBuilder = nextResponse(rootNode, false);
        assertEquals(3, responseBuilder.getPlayAudios().size());
    }

    @Test
    public void shouldAddOnlyMenuAudioPromptsToReplayOnIncorrectUserResponse() {
        Node rootNode = Node.newBuilder()
                .setPrompts(Arrays.asList(new AudioPrompt().setName("hello"), new MenuAudioPrompt().setName("menu")))
                .build();
        IVRResponseBuilder responseBuilder = nextResponse(rootNode, true);
        assertEquals(2, responseBuilder.getPlayAudios().size());
        assertEquals("menu", responseBuilder.getPlayAudios().get(0));
    }

    class ReturnEmptyCommand implements ITreeCommand{
        @Override
        public String[] execute(Object o) {
            return new String[0];
        }
    }

    class ReturnMultiplePromptCommand implements ITreeCommand{
        @Override
        public String[] execute(Object o) {
            return new String[] {"a", "b"};
        }
    }
}
