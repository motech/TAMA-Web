package org.motechproject.tama.ivr.decisiontree;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Transition;
import org.motechproject.tama.ivr.decisiontree.filter.alerts.*;
import org.motechproject.tama.web.command.SymptomReportingAlertsCommand;

public class SymptomReportingAlertServiceTest {
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

    private Node node1 = new Node();
    private Node node2 = new Node();
    private Node rootNode = new Node();

    @Before
    public void setUp() {
        initMocks(this);

        rootNode = new Node()
                .setTransitions(new Object[][]{
                        {"1", new Transition().setDestinationNode(node1)},
                        {"2", new Transition().setDestinationNode(node2)}
                });

        when(firstPriorityNodeFinder.filter(any(Node.class))).thenReturn(java.util.Arrays.asList(rootNode, node1, node2));
        when(secondPriorityNodeFinder.filter(any(Node.class))).thenReturn(java.util.Arrays.asList(rootNode, node1, node2));
        when(thirdPriorityNodeFinder.filter(any(Node.class))).thenReturn(java.util.Arrays.asList(rootNode, node1, node2));
        when(fourthPriorityNodeFinder.filter(any(Node.class))).thenReturn(java.util.Arrays.asList(rootNode, node1, node2));
        when(fifthPriorityNodeFinder.filter(any(Node.class))).thenReturn(java.util.Arrays.asList(rootNode, node1, node2));

        when(symptomReportingAlertsCommand.symptomReportingAlertWithPriority(Matchers.<Integer>any(), null)).thenReturn(new ITreeCommand() {
            @Override
            public String[] execute(Object o) {
                return new String[0];
            }
        });
    }

    @Test
    public void shouldAddCorrectCommands() {
        SymptomReportingAlertService service = new SymptomReportingAlertService(firstPriorityNodeFinder,secondPriorityNodeFinder,thirdPriorityNodeFinder,fourthPriorityNodeFinder,fifthPriorityNodeFinder,symptomReportingAlertsCommand);
        service.addAlerts(rootNode);

        assertEquals(5, rootNode.getTreeCommands().size());
        assertEquals(5, node1.getTreeCommands().size());
        assertEquals(5, node2.getTreeCommands().size());
    }
}
