package org.motechproject.tama.ivr.builder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.*;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRSession;

import java.util.Arrays;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;
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
        when(ivrSession.getPreferredLanguageCode()).thenReturn("en");
        when(ivrContext.ivrSession()).thenReturn(ivrSession);
    }

    @Test
    public void shouldAddCollectDtmfIfTheNodeHasTransitions() {
        Node rootNode = new Node()
                .setPrompts(Arrays.asList(new AudioPrompt().setName("foo")))
                .setTransitions(new Object[][]{
                        {"1", Transition.newBuilder()
                                .setDestinationNode(new Node()
                                                .setPrompts(Arrays.asList(new AudioPrompt().setName("bar")))).build()
                        },
                        {"2", Transition.newBuilder()
                                .setDestinationNode(new Node()
                                                .setPrompts(Arrays.asList(new AudioPrompt().setName("baz")))).build()
                        }});
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
        Node rootNode = new Node()
                .setPrompts(Arrays.asList(new AudioPrompt().setName("foo")));
        IVRResponseBuilder responseBuilder = nextResponse(rootNode, false);
        assertFalse(responseBuilder.isCollectDtmf());
        assertTrue(responseBuilder.isHangUp());
        assertEquals(2, responseBuilder.getPlayAudios().size());
        assertEquals(0, responseBuilder.getPlayTexts().size());
    }

    @Test
    public void whenAudioCommandReturnsNullThenItShouldNotGetAddedToResponse() {
        Node rootNode = new Node()
                .setPrompts(Arrays.asList(new AudioPrompt().setCommand(new ReturnEmptyCommand())));
        IVRResponseBuilder responseBuilder = nextResponse(rootNode, false);
        assertEquals(1, responseBuilder.getPlayAudios().size());
    }

    @Test
    public void createMultiplePlayAudiosWhenACommandReturnsMultiplePrompts() {
        Node rootNode = new Node()
                .setPrompts(Arrays.asList(new AudioPrompt().setCommand(new ReturnMultiplePromptCommand())));
        IVRResponseBuilder responseBuilder = nextResponse(rootNode, false);
        assertEquals(3, responseBuilder.getPlayAudios().size());
    }

    @Test
    public void shouldAddOnlyMenuAudioPromptsToReplayOnIncorrectUserResponse() {
        Node rootNode = new Node()
                .setPrompts(Arrays.asList(new AudioPrompt().setName("hello"), new MenuAudioPrompt().setName("menu")));
        IVRResponseBuilder responseBuilder = nextResponse(rootNode, true);
        assertEquals(2, responseBuilder.getPlayAudios().size());
        assertEquals("menu", responseBuilder.getPlayAudios().get(0));
    }

    @Test
    public void shouldExecuteCommandsInMenuAudioPromptsDuringReplayOnIncorrectUserResponse() {
        ITreeCommand mockCommand = mock(ITreeCommand.class);
        when(mockCommand.execute(any())).thenReturn(new String[]{});

        MenuAudioPrompt menu = new MenuAudioPrompt();
        menu.setName("menu");
        menu.setCommand(mockCommand);

        Node rootNode = Node.newBuilder()
                .setPrompts(Arrays.asList(new AudioPrompt().setName("hello"), menu))
                .build();
        nextResponse(rootNode, true);
        verify(mockCommand, times(1)).execute(any());

    }

    class ReturnEmptyCommand implements ITreeCommand {
        @Override
        public String[] execute(Object o) {
            return new String[0];
        }
    }

    class ReturnMultiplePromptCommand implements ITreeCommand {
        @Override
        public String[] execute(Object o) {
            return new String[]{"a", "b"};
        }
    }
}
