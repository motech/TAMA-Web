package org.motechproject.tama.ivr.decisiontree;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.decisiontree.filter.alerts.*;
import org.motechproject.tama.web.command.SymptomReportingAlertsCommand;
import org.motechproject.tama.web.command.callforwarding.DialStateCommand;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomReportingServiceTest {
    @Mock
    private FirstPrioritySymptomReportingFilter firstPriorityNodeFinder;
    @Mock
    private SecondPrioritySymptomReportingFilter secondPriorityNodeFinder;
    @Mock
    private ThirdPrioritySymptomReportingFilter thirdPriorityNodeFinder;
    @Mock
    private FourthPrioritySymptomReportingFilter fourthPriorityNodeFinder;
    @Mock
    private FifthPrioritySymptomReportingFilter fifthPriorityNodeFinder;
    @Mock
    private SymptomReportingAlertsCommand symptomReportingAlertsCommand;
    @Mock
    private DialStateCommand dialStateCommand;

    private Node node1 = new Node();
    private Node node2 = new Node();
    private Node node3 = new Node();
    private Node node4 = new Node();
    private Node node5 = new Node();
    private Node rootNode = new Node();

    @Before
    public void setUp() {
        initMocks(this);

        rootNode = new Node()
                .setTransitions(new Object[][]{
                        {"1", new Transition().setDestinationNode(node1)},
                        {"2", new Transition().setDestinationNode(node2)},
                        {"3", new Transition().setDestinationNode(node3)},
                        {"4", new Transition().setDestinationNode(node4)},
                        {"5", new Transition().setDestinationNode(node5)}
                });

        when(firstPriorityNodeFinder.filter(any(Node.class))).thenReturn(java.util.Arrays.asList(node1));
        when(secondPriorityNodeFinder.filter(any(Node.class))).thenReturn(java.util.Arrays.asList(node2));
        when(thirdPriorityNodeFinder.filter(any(Node.class))).thenReturn(java.util.Arrays.asList(node3));
        when(fourthPriorityNodeFinder.filter(any(Node.class))).thenReturn(java.util.Arrays.asList(node4));
        when(fifthPriorityNodeFinder.filter(any(Node.class))).thenReturn(java.util.Arrays.asList(node5));

        when(symptomReportingAlertsCommand.symptomReportingAlertWithPriority(Matchers.<Integer>any(), Matchers.<Node>any())).thenReturn(new ITreeCommand() {
            @Override
            public String[] execute(Object o) {
                return new String[0];
            }
        });
    }

    @Test
    public void shouldAddAlertCommands() {
        SymptomReportingService service = new SymptomReportingService(firstPriorityNodeFinder, secondPriorityNodeFinder, thirdPriorityNodeFinder, fourthPriorityNodeFinder, fifthPriorityNodeFinder, symptomReportingAlertsCommand, dialStateCommand);
        service.addCommands(rootNode);

        assertEquals(0, rootNode.getTreeCommands().size());
        assertEquals(1, node1.getTreeCommands().size());
        assertEquals(1, node2.getTreeCommands().size());
        assertEquals(1, node3.getTreeCommands().size());
        assertEquals(1, node4.getTreeCommands().size());
        assertEquals(1, node5.getTreeCommands().size());
    }

    @Test
    public void shouldAddDialPrompts() {
        SymptomReportingService service = new SymptomReportingService(firstPriorityNodeFinder, secondPriorityNodeFinder, thirdPriorityNodeFinder, fourthPriorityNodeFinder, fifthPriorityNodeFinder, symptomReportingAlertsCommand, dialStateCommand);
        service.addCommands(rootNode);

        List<Prompt> prompts = node1.getPrompts();
        assertEquals(2, prompts.size());
        assertEquals(TamaIVRMessage.CONNECTING_TO_DOCTOR, prompts.get(0).getName());
        assertEquals(dialStateCommand.getClass(), prompts.get(1).getCommand().getClass());

        assertEquals(0, node2.getPrompts().size());
    }
}
