package org.motechproject.tama.ivr.builder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.*;
import org.motechproject.server.decisiontree.DecisionTreeBasedResponseBuilder;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.server.service.ivr.IVRResponseBuilder;
import org.motechproject.server.service.ivr.IVRSession;

import java.util.ArrayList;
import java.util.List;

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
        treeBasedResponseBuilder = new DecisionTreeBasedResponseBuilder(new IVRResponseBuilder() {
			
        	List<String> audios = new ArrayList<String>();
        	boolean hangup;
        	boolean collectDtmf;
			@Override
			public IVRResponseBuilder withPlayTexts(String... playTexts) {
				return this;
			}
			
			@Override
			public IVRResponseBuilder withPlayAudios(String... playAudios) {
				for (int i=0; i<playAudios.length; i++)
					audios.add(playAudios[i]);
				return this;
			}
			
			@Override
			public IVRResponseBuilder withHangUp() {
				hangup = true;
				return this;
			}

            @Override
            public IVRResponseBuilder withNextUrl(String s) {
                return this;
            }

            @Override
			public boolean isHangUp() {
				return hangup;
			}
			
			@Override
			public boolean isCollectDtmf() {
				return collectDtmf;
			}
			
			@Override
			public List<String> getPlayTexts() {
				return new ArrayList<String>();
			}
			
			@Override
			public List<String> getPlayAudios() {
				return audios;
			}
			
			@Override
			public String createWithDefaultLanguage(IVRMessage ivrMessage, String sessionId) {
				return null;
			}
			
			@Override
			public String create(IVRMessage ivrMessage, String sessionId, String languageCode) {
				return null;
			}
			
			@Override
			public IVRResponseBuilder collectDtmf(int dtmfLength) {
				collectDtmf = true;
				return this;
			}
		}, new IVRMessage() {
			
			@Override
			public String getWav(String key, String preferredLangCode) {
				return null;
			}
			
			@Override
			public String getText(String key) {
				return null;
			}
			
			@Override
			public String getSignatureMusic() {
				return "signature_music.wav";
			}
		});
        initMocks(this);
        when(ivrSession.getPreferredLanguageCode()).thenReturn("en");
        when(ivrContext.ivrSession()).thenReturn(ivrSession);
    }

    @Test
    public void shouldAddCollectDtmfIfTheNodeHasTransitions() {
        Node rootNode = new Node()
                .setPrompts(new AudioPrompt().setName("foo"))
                .setTransitions(new Object[][]{
                        {"1", new Transition()
                                .setDestinationNode(new Node()
                                        .setPrompts(new AudioPrompt().setName("bar")))
                        },
                        {"2", new Transition()
                                .setDestinationNode(new Node()
                                        .setPrompts(new AudioPrompt().setName("baz")))
                        }});
        IVRResponseBuilder responseBuilder = nextResponse(rootNode, false);
        assertTrue(responseBuilder.isCollectDtmf());
        assertEquals(1, responseBuilder.getPlayAudios().size());
        assertEquals(0, responseBuilder.getPlayTexts().size());
    }

    private IVRResponseBuilder nextResponse(Node rootNode, boolean retryOnIncorrectUserAction) {
        return treeBasedResponseBuilder.ivrResponse(rootNode, ivrContext, retryOnIncorrectUserAction);
    }

    @Test
    public void shouldAddAddHangupIfTheNodeDoesNotHaveAnyTransitions() {
        Node rootNode = new Node()
                .setPrompts(new AudioPrompt().setName("foo"));
        IVRResponseBuilder responseBuilder = nextResponse(rootNode, false);
        assertFalse(responseBuilder.isCollectDtmf());
        assertTrue(responseBuilder.isHangUp());
        assertEquals(2, responseBuilder.getPlayAudios().size());
        assertEquals(0, responseBuilder.getPlayTexts().size());
    }

    @Test
    public void whenAudioCommandReturnsNullThenItShouldNotGetAddedToResponse() {
        Node rootNode = new Node()
                .setPrompts(new AudioPrompt().setCommand(new ReturnEmptyCommand()));
        IVRResponseBuilder responseBuilder = nextResponse(rootNode, false);
        assertEquals(1, responseBuilder.getPlayAudios().size());
    }

    @Test
    public void createMultiplePlayAudiosWhenACommandReturnsMultiplePrompts() {
        Node rootNode = new Node()
                .setPrompts(new AudioPrompt().setCommand(new ReturnMultiplePromptCommand()));
        IVRResponseBuilder responseBuilder = nextResponse(rootNode, false);
        assertEquals(3, responseBuilder.getPlayAudios().size());
    }

    @Test
    public void shouldAddOnlyMenuAudioPromptsToReplayOnIncorrectUserResponse() {
        Node rootNode = new Node()
                .setPrompts(new AudioPrompt().setName("hello"), new MenuAudioPrompt().setName("menu"));
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

        Node rootNode = new Node()
                .setPrompts(new AudioPrompt().setName("hello"), menu);
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
