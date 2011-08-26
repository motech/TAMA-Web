package org.motechproject.tama.ivr.decisiontree;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.ThreadLocalContext;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.util.DateUtil;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationTestContext.xml")
public class TreeChooserTest {

    @Autowired
    private TreeChooser treeChooser;


    @Autowired
    @Qualifier("currentDosageReminderTree")
    private CurrentDosageReminderTree currentDosageReminderTree;

    @Autowired
    private CurrentDosageTakenTree currentDosageTakenTree;

    @Mock
    private IVRSession ivrSession;

    @Mock
    private IVRRequest ivrRequest;

    private PillRegimenResponse pillRegimenResponse;

    @Autowired
    private ThreadLocalTargetSource threadLocalTargetSource;
    @Autowired
    private CurrentDosageConfirmTree currentDosageConfirmTree;

    @Autowired
	private Regimen6PartialTree regimen6PartialTree;

    @Before
    public void setUp(){
        initMocks(this);

        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);


    }

    @Test
    public void shouldGetCurrentDosageReminderTreeIfTAMACallsPatient() {

        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.DOSAGE_ID, "currentDosageId");
        when(ivrRequest.getTamaParams()).thenReturn(params);

        List<DosageResponse> dosages = Arrays.asList(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, DateUtil.today().minusDays(1), null));
        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withDosages(dosages).build();
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);

        ThreadLocalContext threadLocalContext = (ThreadLocalContext) threadLocalTargetSource.getTarget();
        threadLocalContext.setIvrContext(new IVRContext(ivrRequest, ivrSession));

        DosageResponse dosage = dosages.get(0);
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosage.getDosageHour()));
        when(ivrRequest.hasNoTamaData()).thenReturn(false);

        assertEquals(currentDosageReminderTree.getTree(), treeChooser.getTree(new IVRContext(ivrRequest, ivrSession)));
    }

    @Test
    public void shouldGetCurrentDosageTakenTreeIfPatientHasTakenTheDosage() {
        DosageResponse dosage = pillRegimenResponse.getDosages().get(1);
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosage.getDosageHour()));
        when(ivrRequest.hasNoTamaData()).thenReturn(true);

        assertEquals(currentDosageTakenTree.getTree(), treeChooser.getTree(new IVRContext(ivrRequest, ivrSession)));
    }

    @Test
    public void shouldGetCurrentDosageConfirmTreeIfPatientHasNotTakenTheDosage_WhenPatientCallingTAMA() {
        List<DosageResponse> dosages = Arrays.asList(new DosageResponse("dosageId", new Time(10, 5), DateUtil.today(), null, DateUtil.today().minusDays(1), null));
        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withDosages(dosages).build();
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);

        ThreadLocalContext threadLocalContext = (ThreadLocalContext) threadLocalTargetSource.getTarget();
        threadLocalContext.setIvrContext(new IVRContext(ivrRequest, ivrSession));

        DosageResponse dosage = dosages.get(0);
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosage.getDosageHour()));
        when(ivrRequest.hasNoTamaData()).thenReturn(true);

        assertEquals(currentDosageConfirmTree.getTree(), treeChooser.getTree(new IVRContext(ivrRequest, ivrSession)));
    }

    @Test
    public void shouldGetRegimen6PartialTreeIfKookooCallsWithSymptomsReportingCallType() {
        when(ivrRequest.hasNoTamaData()).thenReturn(true);
        when(ivrSession.isSymptomsReportingCall()).thenReturn(true);
        
        assertTrue(regimen6PartialTree.getTree() == treeChooser.getTree(new IVRContext(ivrRequest, ivrSession)));
    }

}
