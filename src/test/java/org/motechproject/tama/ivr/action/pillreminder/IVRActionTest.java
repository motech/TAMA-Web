package org.motechproject.tama.ivr.action.pillreminder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.decisiontree.TAMADecisionTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpSession;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationContext.xml")
public class IVRActionTest {
    @Mock
    private HttpSession httpSession;
    @Autowired
    private IVRMessage ivrMessage;

    private IVRAction tamaIvrAction;
    private IVRActionTest.CommandForTamaIvrActionTest commandForTamaIvrActionTest;

    @Before
    public void setup() {
        initMocks(this);
        tamaIvrAction = new IVRAction(new TestTreeForTamaIvrActionTest(), ivrMessage);
        commandForTamaIvrActionTest = new CommandForTamaIvrActionTest();
    }

    @Test
    public void shouldExecuteCommandIfNextNodeIsNotNull() {
        tamaIvrAction.handle(new IVRRequest("sid", "cid", "event", ""), new IVRSession(httpSession));
        assertTrue(commandForTamaIvrActionTest.isCalled());
    }

    @Test
    public void shouldExecuteSameCommandIfTransitionIsInvalid() {
        tamaIvrAction.handle(new IVRRequest("sid", "cid", "event", ""), new IVRSession(httpSession));
        when(httpSession.getAttribute(IVRCallAttribute.CURRENT_DECISION_TREE_POSITION)).thenReturn("/");
        tamaIvrAction.handle(new IVRRequest("sid", "cid", "event", "3"), new IVRSession(httpSession));
        commandForTamaIvrActionTest.setCalled(false);
        assertFalse(commandForTamaIvrActionTest.isCalled());
    }

    @Test
    public void shouldChangeCurrentNodePath() {

        tamaIvrAction.handle(new IVRRequest("sid", "cid", "event", ""), new IVRSession(httpSession));

        verify(httpSession).setAttribute(IVRCallAttribute.CURRENT_DECISION_TREE_POSITION, "/");
    }

    class TestTreeForTamaIvrActionTest extends TAMADecisionTree {
        @Override
        protected Node createRootNode() {
            Node rootNode = Node.newBuilder()
                .setPrompts(Arrays.asList(new AudioPrompt().setName("foo")))
                    .setTreeCommands(commandForTamaIvrActionTest)
                .setTransitions(new Object[][]{
                        {"1", Transition.newBuilder()
                                .setDestinationNode(Node.newBuilder()
                                        .setPrompts(Arrays.asList(new AudioPrompt().setName("bar"))).build()).build()
                        },
                        {"2", Transition.newBuilder()
                                .setDestinationNode(Node.newBuilder()
                                        .setPrompts(Arrays.asList(new AudioPrompt().setName("baz"))).build()).build()
                        }}).build();
            return rootNode;
        }
    }

    class CommandForTamaIvrActionTest implements ITreeCommand{
        private boolean called;

        @Override
        public String[] execute(Object o) {
            called = true;
            return new String[0];
        }

        public boolean isCalled() {
            return called;
        }

        public void setCalled(boolean called) {
            this.called = called;
        }
    }
}
