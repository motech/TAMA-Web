package org.motechproject.tama.ivr.decisiontree;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRRequest.CallDirection;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.ThreadLocalContext;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;
import org.motechproject.util.DateUtil;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationTestContext.xml")
public class TamaTreeChooserTest {

    @Autowired
    private TamaTreeChooser treeChooser;

    @Autowired
    @Qualifier("currentDosageReminderTree")
    private CurrentDosageReminderTree currentDosageReminderTree;

    @Autowired
    @Qualifier("currentDosageTakenTree")
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
	private Regimen6Tree regimen6Tree;

    @Before
    public void setUp(){
        initMocks(this);

        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimenResponse);
    }

    @Test
    public void shouldGetCurrentDosageReminderTreeIfTAMACallsPatient() {

    	when(ivrRequest.getParameter(PillReminderCall.DOSAGE_ID)).thenReturn("currentDosageId");
        List<DosageResponse> dosages = Arrays.asList(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, DateUtil.today().minusDays(1), null));
        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withDosages(dosages).build();
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimenResponse);

        ThreadLocalContext threadLocalContext = (ThreadLocalContext) threadLocalTargetSource.getTarget();
        threadLocalContext.setIvrContext(new IVRContext(ivrRequest, ivrSession));

        DosageResponse dosage = dosages.get(0);
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosage.getDosageHour()));
        when(ivrRequest.getCallDirection()).thenReturn(CallDirection.Outbound);

        assertEquals(currentDosageReminderTree.getTree(), treeChooser.getTree(new IVRContext(ivrRequest, ivrSession)));
    }

    @Test
    public void shouldGetCurrentDosageTakenTreeIfPatientHasTakenTheDosage() {
        DosageResponse dosage = pillRegimenResponse.getDosages().get(1);
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosage.getDosageHour()));
        when(ivrRequest.getCallDirection()).thenReturn(CallDirection.Inbound);

        assertEquals(currentDosageTakenTree.getTree(), treeChooser.getTree(new IVRContext(ivrRequest, ivrSession)));
    }

    @Test
    public void shouldGetCurrentDosageConfirmTreeIfPatientHasNotTakenTheDosage_WhenPatientCallingTAMA() {
        List<DosageResponse> dosages = Arrays.asList(new DosageResponse("dosageId", new Time(10, 5), DateUtil.today(), null, DateUtil.today().minusDays(1), null));
        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withDosages(dosages).build();
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimenResponse);

        ThreadLocalContext threadLocalContext = (ThreadLocalContext) threadLocalTargetSource.getTarget();
        threadLocalContext.setIvrContext(new IVRContext(ivrRequest, ivrSession));

        DosageResponse dosage = dosages.get(0);
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosage.getDosageHour()));
        when(ivrRequest.getCallDirection()).thenReturn(CallDirection.Inbound);

        assertEquals(currentDosageConfirmTree.getTree(), treeChooser.getTree(new IVRContext(ivrRequest, ivrSession)));
    }

    @Test
    public void shouldGetRegimen6TreeIfKookooCallsWithSymptomsReportingCallType() {
        when(ivrRequest.getCallDirection()).thenReturn(CallDirection.Inbound);
        when(ivrSession.get(TamaSessionAttribute.SYMPTOMS_REPORTING_PARAM)).thenReturn("true");
        
        assertTrue(regimen6Tree.getTree() == treeChooser.getTree(new IVRContext(ivrRequest, ivrSession)));
    }
}
