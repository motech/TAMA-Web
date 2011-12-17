package org.motechproject.tamacallflow.ivr.decisiontree;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.AudioPrompt;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tamacallflow.ivr.command.SuspendAdherenceCallsCommand;
import org.motechproject.tamacallflow.ivr.command.SymptomReportingAlertsCommand;
import org.motechproject.tamacallflow.ivr.command.callforwarding.DialStateCommand;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomReportingTreeInterceptorTest {
    @Mock
    private SymptomReportingAlertsCommand symptomReportingAlertsCommand;
    @Mock
    private DialStateCommand dialStateCommand;
    @Mock
    private SuspendAdherenceCallsCommand suspendAdherenceCallsCommand;

    private SymptomReportingTreeInterceptor interceptor;

    @Before
    public void setUp() {
        initMocks(this);

        when(symptomReportingAlertsCommand.symptomReportingAlertWithPriority(Matchers.<Integer>any(), Matchers.<Node>any(), Matchers.<TAMAConstants.ReportedType>any())).thenReturn(new ITreeCommand() {
            @Override
            public String[] execute(Object o) {
                return new String[0];
            }
        });
        interceptor = new SymptomReportingTreeInterceptor(symptomReportingAlertsCommand, dialStateCommand, suspendAdherenceCallsCommand);
    }

    @Test
    public void shouldAddAlertCommands() {
        Node node1 = node("adv_crocin01");
        Node node2 = node("adv_stopmedicineseeclinicasap");
        Node node3 = node("adv_continuemedicineseeclinicasap");
        Node node4 = node("adv_callclinic");
        Node node5 = node("adv_tingpainfeetcropanto");
        Node rootNode = new Node();

        interceptor.addCommands(rootNode);

        assertEquals(0, rootNode.getTreeCommands().size());
        interceptor.addCommands(node1);
        verify(symptomReportingAlertsCommand).symptomReportingAlertWithPriority(1, node1, TAMAConstants.ReportedType.No);
        interceptor.addCommands(node2);
        verify(symptomReportingAlertsCommand).symptomReportingAlertWithPriority(2, node2, TAMAConstants.ReportedType.No);
        interceptor.addCommands(node3);
        verify(symptomReportingAlertsCommand).symptomReportingAlertWithPriority(3, node3, TAMAConstants.ReportedType.NA);
        interceptor.addCommands(node4);
        verify(symptomReportingAlertsCommand).symptomReportingAlertWithPriority(4, node4, TAMAConstants.ReportedType.NA);
        interceptor.addCommands(node5);
        verify(symptomReportingAlertsCommand).symptomReportingAlertWithPriority(5, node5, TAMAConstants.ReportedType.NA);
    }

    @Test
    public void shouldAddDialPrompts_ForFirstPriorityNodes() {
        Node node1 = node("adv_crocin01");
        interceptor.addCommands(node1);
        List<Prompt> prompts = node1.getPrompts();
        assertEquals(2, prompts.size());
        assertEquals("adv_crocin01", prompts.get(0).getName());
        assertEquals(dialStateCommand.getClass(), prompts.get(1).getCommand().getClass());

        Node node2 = node("some other node");
        interceptor.addCommands(node2);
        assertTrue(node2.getPrompts().size() == 1);
    }

    @Test
    public void shouldAddDialPrompts_ForSecondPriorityNodes() {
        Node node1 = node("adv_seeclinicasapdepression");
        interceptor.addCommands(node1);
        List<Prompt> prompts = node1.getPrompts();
        assertEquals(2, prompts.size());
        assertEquals("adv_seeclinicasapdepression", prompts.get(0).getName());
        assertEquals(dialStateCommand.getClass(), prompts.get(1).getCommand().getClass());
    }

    @Test
    public void shouldSetSuspendAdherenceCallsCommandOnlyOnTheFollowingNodes() {
        Node node1 = new Node().setPrompts(
                new AudioPrompt().setName("cn_swellfacelegs"),
                new AudioPrompt().setName("cy_fever"),
                new AudioPrompt().setName("adv_crocin01")
        );
        interceptor.addCommands(node1);
        Assert.assertTrue(containsTreeCommand(node1, suspendAdherenceCallsCommand));

        Node node2 = new Node().setPrompts(
                new AudioPrompt().setName("cn_swellfacelegs"),
                new AudioPrompt().setName("cy_fever"),
                new AudioPrompt().setName("adv_noteatanythg")
        );
        interceptor.addCommands(node2);
        Assert.assertTrue(containsTreeCommand(node2, suspendAdherenceCallsCommand));

        Node node3 = new Node().setPrompts(
                new AudioPrompt().setName("cn_swellfacelegs"),
                new AudioPrompt().setName("cy_fever"),
                new AudioPrompt().setName("adv_stopmedicineseeclinicasap")
        );
        interceptor.addCommands(node3);
        Assert.assertTrue(containsTreeCommand(node3, suspendAdherenceCallsCommand));
    }

    @Test
    public void shouldNotSetSuspendAdherenceCallsCommandOnlyOnAnyOtherNodes() {
        Node node = new Node().setPrompts(
                new AudioPrompt().setName("cn_swellfacelegs"),
                new AudioPrompt().setName("cy_fever")
        );
        interceptor.addCommands(node);
        assertEquals(0, node.getTreeCommands().size());
    }

    private Node node(String promptName) {
        return new Node().setPrompts(new AudioPrompt().setName(promptName));
    }

    private boolean containsTreeCommand(Node node, ITreeCommand command) {
        for (ITreeCommand cmd : node.getTreeCommands()) {
            if (cmd == command) return true;
        }
        return false;
    }
}
