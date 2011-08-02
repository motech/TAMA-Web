package org.motechproject.tama.ivr.builder;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.ivr.IVRContext;

import java.util.Arrays;

import static junit.framework.Assert.*;

public class DecisionTreeBasedResponseBuilderTest {
    private DecisionTreeBasedResponseBuilder treeBasedResponseBuilder;

    @Before
    public void setUp() {
        treeBasedResponseBuilder = new DecisionTreeBasedResponseBuilder();
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
        IVRResponseBuilder responseBuilder = nextResponse(rootNode);
        assertTrue(responseBuilder.isCollectDtmf());
        assertEquals(1, responseBuilder.getPlayAudios().size());
        assertEquals(0, responseBuilder.getPlayTexts().size());
    }

    private IVRResponseBuilder nextResponse(Node rootNode) {
        return treeBasedResponseBuilder.ivrResponse("foo", rootNode, new IVRContext(null, null));
    }

    @Test
    public void shouldAddAddHangupIfTheNodeDoesNotHaveAnyTransitions() {
        Node rootNode = Node.newBuilder()
                .setPrompts(Arrays.asList(new AudioPrompt().setName("foo")))
                .build();
        IVRResponseBuilder responseBuilder = nextResponse(rootNode);
        assertFalse(responseBuilder.isCollectDtmf());
        assertTrue(responseBuilder.isHangUp());
        assertEquals(1, responseBuilder.getPlayAudios().size());
        assertEquals(0, responseBuilder.getPlayTexts().size());
    }

    @Test
    public void whenAudioCommandReturnsNullThenItShouldNotGetAddedToResponse() {
        Node rootNode = Node.newBuilder()
                .setPrompts(Arrays.asList(new AudioPrompt().setCommand(new ReturnEmptyCommand())))
                .build();
        IVRResponseBuilder responseBuilder = nextResponse(rootNode);
        assertEquals(0, responseBuilder.getPlayAudios().size());
    }

    @Test
    public void createMultiplePlayAudiosWhenACommandReturnsMultiplePrompts() {
        Node rootNode = Node.newBuilder()
                .setPrompts(Arrays.asList(new AudioPrompt().setCommand(new ReturnMultiplePromptCommand())))
                .build();
        IVRResponseBuilder responseBuilder = nextResponse(rootNode);
        assertEquals(2, responseBuilder.getPlayAudios().size());
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