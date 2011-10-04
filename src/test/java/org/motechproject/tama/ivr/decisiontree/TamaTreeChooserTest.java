package org.motechproject.tama.ivr.decisiontree;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.List;

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
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

    @Autowired
    private CurrentDosageConfirmTree currentDosageConfirmTree;

    @Autowired
    private Regimen1To6Tree regimen1To6Tree;

    @Autowired
    private FourDayRecallTree fourDayRecallTree;
    
    @Mock
    private IVRSession ivrSession;

    @Mock
    private IVRRequest ivrRequest;

    private IVRContext ivrContext;
    private PillRegimenResponse pillRegimenResponse;

    @Before
    public void setUp(){
        initMocks(this);

        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimenResponse);

        ivrContext = new IVRContext(ivrRequest, ivrSession);
    }

    @Test
    public void shouldGetCurrentDosageReminderTreeIfTAMACallsPatient() {

    	when(ivrRequest.getParameter(PillReminderCall.DOSAGE_ID)).thenReturn("currentDosageId");
        List<DosageResponse> dosages = Arrays.asList(new DosageResponse("currentDosageId", new Time(10, 5), DateUtil.today(), null, DateUtil.today().minusDays(1), null));
        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withDosages(dosages).build();
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimenResponse);
        DosageResponse dosage = dosages.get(0);
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosage.getDosageHour()));
        when(ivrRequest.getCallDirection()).thenReturn(CallDirection.Outbound);

        assertEquals(currentDosageReminderTree.getTree(ivrContext), treeChooser.getTree(ivrContext));
    }

    @Test
    public void shouldGetCurrentDosageTakenTreeIfPatientHasTakenTheDosage() {
        DosageResponse dosage = pillRegimenResponse.getDosages().get(1);
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosage.getDosageHour()));
        when(ivrRequest.getCallDirection()).thenReturn(CallDirection.Inbound);

        assertEquals(currentDosageTakenTree.getTree(ivrContext), treeChooser.getTree(ivrContext));
    }

    @Test
    public void shouldGetCurrentDosageConfirmTreeIfPatientHasNotTakenTheDosage_WhenPatientCallingTAMA() {
        List<DosageResponse> dosages = Arrays.asList(new DosageResponse("dosageId", new Time(10, 5), DateUtil.today(), null, DateUtil.today().minusDays(1), null));
        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().withDosages(dosages).build();
        when(ivrSession.get(TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(pillRegimenResponse);

        DosageResponse dosage = dosages.get(0);
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosage.getDosageHour()));
        when(ivrRequest.getCallDirection()).thenReturn(CallDirection.Inbound);

        assertEquals(currentDosageConfirmTree.getTree(ivrContext), treeChooser.getTree(ivrContext));
    }

    @Test
    public void shouldGetFourDayRecallTreeForPatientsOnWeeklyAdherence() {
        when(ivrSession.get(TamaSessionAttribute.FOUR_DAY_RECALL)).thenReturn("true");
        assertTrue(fourDayRecallTree.getTree(ivrContext) == treeChooser.getTree(ivrContext));
    }
}
