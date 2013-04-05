package org.motechproject.tama.healthtips.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.healthtips.domain.HealthTipsHistory;
import org.motechproject.tama.healthtips.repository.AllHealthTipsHistory;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.domain.HealthTip;
import org.motechproject.tama.refdata.repository.AllHealthTips;
import org.motechproject.util.DateUtil;

import java.util.*;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class HealthTipServiceTest {

    @Mock
    private AllHealthTipsHistory allHealthTipsHistory;

    @Mock
    private AllPatients allPatients;

    @Mock
    private AllHealthTips allHealthTips;

    private String patientId;
    private String patientDocId;
    private Patient patient;

    HealthTipService healthTipService;

    @Before()
    public void setUp() {
        initMocks(this);

        patientId = "patientId";
        patientDocId = "patientDocId";

        healthTipService = new HealthTipService(allHealthTipsHistory, allPatients, allHealthTips);

        setUpDailyPillReminderPatient();

        setupHealthTips();
    }


    @Test
    public void shouldGetHealthTipForACategoryAndPullDailySequence(){
        String healthTip = healthTipService.nextHealthTip(patient.getId(), TAMAMessageType.FAMILY_AND_CHILDREN);
        assertEquals("healthTipSeven", healthTip);
    }

    @Test
    public void shouldGetHealthTipForACategoryAndPushWeeklySequence(){
        setUpWeeklyPillReminderPatient();
        String healthTip = healthTipService.nextHealthTip(patient.getId(), null);
        assertEquals("healthTipNine", healthTip);
    }


    @Test
    public void shouldGetNextHealthTipForACategoryAfterHistory(){

        List<HealthTipsHistory> healthTipsHistory = new ArrayList<>();
        healthTipsHistory.add(new HealthTipsHistory(patient.getId(), "healthTipSeven", DateUtil.now().minusDays(13)));

        when(allHealthTipsHistory.findByPatientId(patient.getId())).thenReturn(healthTipsHistory);

        String healthTip = healthTipService.nextHealthTip(patient.getId(), TAMAMessageType.FAMILY_AND_CHILDREN);
        assertEquals("healthTipEight", healthTip);
    }

    @Test
    public void shouldGetPlaylistForACategory(){
        assertEquals(Arrays.asList("healthTipSeven", "healthTipEight"), healthTipService.getPlayList(patient.getId(), TAMAMessageType.FAMILY_AND_CHILDREN));
    }

    @Test
    public void shouldGetAllPlaylistWhenCategoryIsNull(){
        assertEquals(Arrays.asList("healthTipNine", "healthTipOne","healthTipTwo", "healthTipSeven", "healthTipEight"), healthTipService.getPlayList(patient.getId(), null));
    }

    @Test
    public void shouldAddHistoryOnMarkAsRead() {
        healthTipService.markAsPlayed(patientDocId, "file");
        verify(allHealthTipsHistory).add(any(HealthTipsHistory.class));
    }

    @Test
    public void shouldUpdateHistoryOnMarkAsRead() {
        final String AUDIO_FILE = "file";
        HealthTipsHistory healthTipHistory = new HealthTipsHistory(patientDocId, AUDIO_FILE, DateUtil.now().minusDays(10));
        when(allHealthTipsHistory.findByPatientIdAndAudioFilename(patientId, AUDIO_FILE)).thenReturn(healthTipHistory);
        healthTipService.markAsPlayed(patientDocId, AUDIO_FILE);
        verify(allHealthTipsHistory).add(any(HealthTipsHistory.class));
    }

    private void setupHealthTips() {
        List<HealthTip> healthTips = new ArrayList<>();
        healthTips.add(HealthTip.newHealthTip(TAMAMessageType.ALL_MESSAGES.getDisplayName(), "healthTipOne"  , 1,1,2,2));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageType.ALL_MESSAGES.getDisplayName(), "healthTipNine" , 2,2,1,1));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageType.HIV_AND_CD4.getDisplayName(), "healthTipTwo", 1, null, 3, null));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageType.HIV_AND_CD4.getDisplayName(), "healthTipFive" , null,1,null,3));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageType.FAMILY_AND_CHILDREN.getDisplayName(), "healthTipSeven", 1,1,5,5));
        healthTips.add(HealthTip.newHealthTip(TAMAMessageType.FAMILY_AND_CHILDREN.getDisplayName(), "healthTipEight", 2,2,6,6));

        when(allHealthTips.findByCategory(null)).thenReturn(healthTips);
        when(allHealthTips.findByCategory(TAMAMessageType.ALL_MESSAGES.getDisplayName())).thenReturn(asList(healthTips.get(0), healthTips.get(1)));
        when(allHealthTips.findByCategory(TAMAMessageType.HIV_AND_CD4.getDisplayName())).thenReturn(asList(healthTips.get(2), healthTips.get(3)));
        when(allHealthTips.findByCategory(TAMAMessageType.FAMILY_AND_CHILDREN.getDisplayName())).thenReturn(asList(healthTips.get(4), healthTips.get(5)));
    }

    private void setUpDailyPillReminderPatient(){
        patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        when(allPatients.get(patient.getId())).thenReturn(patient);
    }

    private void setUpWeeklyPillReminderPatient(){
        patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        when(allPatients.get(patient.getId())).thenReturn(patient);
    }


}
