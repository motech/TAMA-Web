package org.motechproject.tama.symptomreporting.decisiontree;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.*;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.service.SendSMSService;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.symptomreporting.command.*;
import org.motechproject.tama.symptomreporting.filter.*;
import org.motechproject.tama.symptomreporting.service.SymptomRecordingService;
import org.motechproject.tama.symptomreporting.service.SymptomReportingAlertService;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SymptomReportingTreeInterceptorTest {
    @Mock
    private UpdateAdviceCommand symptomReportingAlertsCommand;
    @Mock
    private DialStateCommand dialStateCommand;
    @Mock
    private SuspendAdherenceCallsCommand suspendAdherenceCallsCommand;
    @Mock
    private SymptomRecordingService symptomRecordingService;
    @Mock
    private SendSMSService sendSMSService;
    @Mock
    private AllPatients allPatients;
    @Mock
    private Properties properties;
    @Mock
    private SMSFilter smsFilter;
    @Mock
    private SymptomReportingAlertService symptomReportingAlertService;

    private SuspendAdherenceCallsFilter suspendAdherenceCallsFilter = new SuspendAdherenceCallsFilter();

    private SymptomReportingTreeInterceptor interceptor;

    @Before
    public void setUp() {
        initMocks(this);
        when(
                symptomReportingAlertsCommand
                        .get(
                                Matchers.<Integer>any(),
                                Matchers.<Node>any()
                        ))
                .thenReturn(new ITreeCommand() {
                    @Override
                    public String[] execute(Object o) {
                        return new String[0];
                    }
                });
        AlertFilters alertFilters = new AlertFilters(new FirstPriorityFilter(), new SecondPriorityFilter(),
                new ThirdPriorityFilter(), new FourthPriorityFilter(),
                new FifthPriorityFilter());

        SwitchDialPromptFilter switchDialPromptFilter = new SwitchDialPromptFilter(new FirstPriorityFilter(), new SecondPriorityFilter());

        interceptor = new SymptomReportingTreeInterceptor(symptomReportingAlertsCommand, dialStateCommand, suspendAdherenceCallsCommand,
                symptomRecordingService, alertFilters, switchDialPromptFilter, suspendAdherenceCallsFilter, smsFilter, sendSMSService, allPatients, properties, symptomReportingAlertService);
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
        verify(symptomReportingAlertsCommand).get(1, node1);
        interceptor.addCommands(node2);
        verify(symptomReportingAlertsCommand).get(2, node2);
        interceptor.addCommands(node3);
        verify(symptomReportingAlertsCommand).get(3, node3);
        interceptor.addCommands(node4);
        verify(symptomReportingAlertsCommand).get(4, node4);
        interceptor.addCommands(node5);
        verify(symptomReportingAlertsCommand).get(5, node5);
    }

    @Test
    public void shouldAddDialPrompts_ForFirstPriorityNodes() {
        Node node1 = node("adv_crocin01");
        interceptor.addCommands(node1);
        List<Prompt> prompts = node1.getPrompts();
        assertEquals(3, prompts.size());
        assertEquals(TamaIVRMessage.START_SYMPTOM_FLOW, prompts.get(0).getName());
        assertEquals("adv_crocin01", prompts.get(1).getName());
        assertEquals(dialStateCommand.getClass(), prompts.get(2).getCommand().getClass());

        Node node2 = node("some other node");
        interceptor.addCommands(node2);
        assertTrue(node2.getPrompts().size() == 2);
    }

    @Test
    public void shouldAddDialPrompts_ForSecondPriorityNodes() {
        Node node1 = node("adv_seeclinicasap");
        interceptor.addCommands(node1);
        List<Prompt> prompts = node1.getPrompts();
        assertEquals(3, prompts.size());
        assertEquals(TamaIVRMessage.START_SYMPTOM_FLOW, prompts.get(0).getName());
        assertEquals("adv_seeclinicasap", prompts.get(1).getName());
        assertEquals(dialStateCommand.getClass(), prompts.get(2).getCommand().getClass());
    }

    @Test
    public void shouldAddSymptomStartWavFile_ToTheBeginningOfTheRootNode() {
        Node node1 = node("adv_seeclinicasap");
        interceptor.addCommands(node1);
        List<Prompt> prompts = node1.getPrompts();
        assertEquals(3, prompts.size());
        assertEquals(TamaIVRMessage.START_SYMPTOM_FLOW, prompts.get(0).getName());
    }

    @Test
    public void shouldSetSuspendAdherenceCallsCommandOnlyOnTheFollowingNodes() {
        Node node1 = new Node().setPrompts(
                new AudioPrompt().setName("cn_swellfacelegs"),
                new AudioPrompt().setName("cy_fever"),
                new AudioPrompt().setName("adv_crocin01"));
        interceptor.addCommands(node1);
        Assert.assertTrue(node1.getTreeCommands().contains(suspendAdherenceCallsCommand));

        Node node2 = new Node().setPrompts(
                new AudioPrompt().setName("cn_swellfacelegs"),
                new AudioPrompt().setName("cy_fever"),
                new AudioPrompt().setName("adv_noteatanythg"));
        interceptor.addCommands(node2);
        Assert.assertTrue(node2.getTreeCommands().contains(suspendAdherenceCallsCommand));

        Node node3 = new Node().setPrompts(
                new AudioPrompt().setName("cn_swellfacelegs"),
                new AudioPrompt().setName("cy_fever"),
                new AudioPrompt().setName("adv_stopmedicineseeclinicasap"));
        interceptor.addCommands(node3);
        Assert.assertTrue(node3.getTreeCommands().contains(suspendAdherenceCallsCommand));
    }

    @Test
    public void shouldNotSetSuspendAdherenceCallsCommandOnlyOnAnyOtherNodes() {
        Node node = new Node().setPrompts(
                new AudioPrompt().setName("cn_swellfacelegs"),
                new AudioPrompt().setName("cy_fever"));
        interceptor.addCommands(node);
        assertFalse(node.getTreeCommands().contains(suspendAdherenceCallsCommand));
    }

    @Test
    public void shouldAddCommandToLogSymptoms() {
        Node innerNode = new Node().setPrompts(
                new AudioPrompt().setName("ppc_fevswellfacelegs"),
                new AudioPrompt().setName("adv_continuemedicineseeclinicasap"));
        final Node node = new Node()
                .setPrompts(
                        new AudioPrompt().setName("cn_lowurineorgenweakness"),
                        new MenuAudioPrompt().setName("q_swellfacelegs"))
                .setTransitions(
                        new Object[][]{
                                {
                                        "1",
                                        new Transition()
                                                .setDestinationNode(innerNode)},
                                {
                                        "3",
                                        new Transition().setDestinationNode(new Node().setPrompts(
                                                new AudioPrompt()
                                                        .setName("cn_swellfacelegs"),
                                                new AudioPrompt()
                                                        .setName("cy_fever"),
                                                new AudioPrompt()
                                                        .setName("adv_crocin02"),
                                                new DialPrompt()))}});

        interceptor.addCommands(node);
        assertHasRecordSymptomCommand(innerNode);
    }

    @Test
    public void shouldAddRecordAdviceGivenCommand() {
        final Node nodeWithAdvice = new Node().setPrompts(
                new AudioPrompt()
                        .setName("cn_swellfacelegs"),
                new AudioPrompt()
                        .setName("cy_fever"),
                new AudioPrompt()
                        .setName("adv_crocin02"),
                new DialPrompt());
        final Node node = new Node()
                .setPrompts(
                        new AudioPrompt().setName("cn_lowurineorgenweakness"),
                        new MenuAudioPrompt().setName("q_swellfacelegs"))
                .setTransitions(
                        new Object[][]{
                                {
                                        "1",
                                        new Transition()
                                                .setDestinationNode(new Node().setPrompts(
                                                        new AudioPrompt().setName("ppc_fevswellfacelegs"),
                                                        new AudioPrompt().setName("adv_continuemedicineseeclinicasap")))},
                                {
                                        "3",
                                        new Transition().setDestinationNode(nodeWithAdvice)}});
        interceptor.addCommands(node);
        final List<ITreeCommand> commands = nodeWithAdvice.getTreeCommands();
        boolean recordAdviceGivenCommandFound = false;
        for (ITreeCommand command : commands) {
            if (command instanceof RecordAdviceGivenCommand) {
                recordAdviceGivenCommandFound = true;
                assertEquals("adv_crocin02", ((RecordAdviceGivenCommand) command).getAdviceNodeName());
            }
        }
        assertTrue(recordAdviceGivenCommandFound);
    }

    @Test
    public void shouldAddCommandToLogSymptomsForLocalRash() {
        Node noInnerNode = new Node().setPrompts(
                new AudioPrompt().setName("ppc_nvrashallbody"),
                new AudioPrompt().setName("adv_continuemedicineseeclinicasap"));
        Node yesInnerNode = new Node().setPrompts(
                new AudioPrompt().setName("ppc_nvrashallbody")
                , new AudioPrompt().setName("adv_stopmedicineseeclinicasap"));

        final Node node = new Node()
                .setPrompts(new AudioPrompt().setName("cy_rash"),
                        new MenuAudioPrompt().setName("q_rashalloverbody"))
                .setTransitions(
                        new Object[][]{
                                {"1", new Transition().setDestinationNode(yesInnerNode)},
                                {"3", new Transition().setDestinationNode(noInnerNode)}}
                );

        interceptor.addCommands(node);
        assertHasRecordSymptomCommand(yesInnerNode);
        RecordSymptomCommand command = assertHasRecordSymptomCommand(noInnerNode);
        assertEquals("localrash", command.getFileName());
    }

    @Test
    public void shouldAddAudioPromptWithSendSMSCommandToNodesThatPassSMSFilter() {
        Node noInnerNode = new Node().setPrompts(
                new AudioPrompt().setName("ppc_fevswellfacelegs"),
                new AudioPrompt().setName("adv_crocin02"));
        Node yesInnerNode = new Node().setPrompts(
                new AudioPrompt().setName("ppc_nvrashallbody")
                , new AudioPrompt().setName("adv_panto02"));

        final Node node = new Node()
                .setPrompts(new AudioPrompt().setName("cy_rash"),
                        new MenuAudioPrompt().setName("q_rashalloverbody"))
                .setTransitions(
                        new Object[][]{
                                {"1", new Transition().setDestinationNode(yesInnerNode)},
                                {"3", new Transition().setDestinationNode(noInnerNode)}}
                );

        when(smsFilter.filter(node)).thenReturn(Arrays.asList(yesInnerNode, noInnerNode));
        interceptor.addCommands(node);

        List<Prompt> prompts = noInnerNode.getPrompts();
        assertEquals(3, prompts.size());
        assertEquals("n10:sendSMS", prompts.get(2).getName());
        assertEquals(SendSMSCommand.class, prompts.get(2).getCommand()
                .getClass());
        assertEquals("adv_crocin02", ((SendSMSCommand) prompts.get(2).getCommand()).getAdvicePrompts().get(0).getName());

        prompts = yesInnerNode.getPrompts();
        assertEquals(3, prompts.size());
        assertEquals("n10:sendSMS", prompts.get(2).getName());
        assertEquals(SendSMSCommand.class, prompts.get(2).getCommand()
                .getClass());
        assertEquals("adv_panto02", ((SendSMSCommand) prompts.get(2).getCommand()).getAdvicePrompts().get(0).getName());
    }

    private RecordSymptomCommand assertHasRecordSymptomCommand(Node node) {
        for (ITreeCommand command : node.getTreeCommands()) {
            if (command instanceof RecordSymptomCommand) {
                return (RecordSymptomCommand) command;
            }
        }
        fail("Expected a symptom reporting command. None found");
        return null;
    }

    private Node node(String promptName) {
        return new Node().setPrompts(new AudioPrompt().setName(promptName));
    }
}
