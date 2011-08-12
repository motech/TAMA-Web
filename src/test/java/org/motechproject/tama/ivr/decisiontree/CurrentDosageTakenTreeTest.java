package org.motechproject.tama.ivr.decisiontree;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.ThreadLocalContext;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.web.command.NextCallDetails;
import org.motechproject.util.DateUtil;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationTestContext.xml"})
public class CurrentDosageTakenTreeTest {


    @Autowired
    private TestConfirmTree testConfirmTree;

    @Autowired
    private ThreadLocalTargetSource threadLocalTargetSource;

    private IVRRequest ivrRequest;

    @Mock
    private IVRSession ivrSession;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void TearDown() {
        testConfirmTree.setTreeToNull();
    }


    @Test
    public void shouldGetPillTakenCommand() {
        setUpDataForPreviousDosage(true);

        Node nextNode = testConfirmTree.getTree().nextNode("", "");
        List<Prompt> prompts = nextNode.getPrompts();
        assertEquals(3, prompts.size());
        assertTrue(prompts.get(0).getCommand() instanceof NextCallDetails);
    }

    @Test
    public void shouldJumpToPreviousDosageTreeIfPreviousDosageNotCaptured() {
        setUpDataForPreviousDosage(false);

        Node nextNode = testConfirmTree.getTree().nextNode("", "");
        assertTrue(nextNode.hasTransitions());
    }

    private void setUpDataForPreviousDosage(boolean isCaptured) {
        LocalDate previousDosageLastTakenDate = isCaptured ? DateUtil.today().minusDays(1) : DateUtil.today().minusDays(2);

        DosageResponse currentDosage = new DosageResponse("currentDosageId", new Time(9, 5), DateUtil.newDate(2011, 7, 1), DateUtil.newDate(2012, 7, 1), DateUtil.today(), null);
        DosageResponse previousDosage = new DosageResponse("previousDosageId", new Time(15, 5), DateUtil.newDate(2011, 7, 5), DateUtil.newDate(2012, 7, 5), previousDosageLastTakenDate, null);

        List<DosageResponse> dosageResponses = Arrays.asList(currentDosage, previousDosage);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("r1", "p1", 0, 0, dosageResponses);

        IVRRequest ivrRequest = new IVRRequest();

        ivrRequest.setTamaData(String.format("{\"%s\":\"%s\"}", PillReminderCall.DOSAGE_ID, "currentDosageId"));
        ThreadLocalContext threadLocalContext = (ThreadLocalContext) threadLocalTargetSource.getTarget();
        threadLocalContext.setIvrContext(new IVRContext(ivrRequest, ivrSession));

        when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);
    }

}

@Component
class TestConfirmTree extends CurrentDosageTakenTree {
    public void setTreeToNull() {
        tree = null;
    }
}
