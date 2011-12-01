package org.motechproject.tamahealthtip.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.tamahealthtip.domain.HealthTipsHistory;
import org.motechproject.tamahealthtip.repository.AllHealthTipsHistory;
import org.motechproject.util.DateUtil;

import java.util.*;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class HealthTipServiceTest {

    HealthTipService healthTipService;
    private String patientId;
    private String patientDocId;

    @Mock
    private AllHealthTipsHistory allHealthTipsHistory;

    @Before()
    public void setUp() {
        initMocks(this);

        patientId = "patientId";
        patientDocId = "patientDocId";

        healthTipService = new HealthTipService(allHealthTipsHistory);

        healthTipService = Mockito.spy(healthTipService);
        Map<String, Integer> healthTipMap = new HashMap<String, Integer>();
        healthTipMap.put("healthTipOne.wav", 1);
        healthTipMap.put("healthTipTwo.wav", 2);
        healthTipMap.put("healthTipThree.wav", 3);
        healthTipMap.put("healthTipFour.wav", 2);
        healthTipMap.put("healthTipFive.wav", 1);
        healthTipMap.put("healthTipSix.wav", 1);
        healthTipMap.put("healthTipSeven.wav", 2);
        healthTipMap.put("healthTipEight.wav", 3);
        healthTipMap.put("healthTipNine.wav", 2);
        when(healthTipService.runHealthTipRules()).thenReturn(healthTipMap);

        List<HealthTipsHistory> healthTipsHistory = new ArrayList<HealthTipsHistory>();
        healthTipsHistory.add(new HealthTipsHistory(patientDocId, "healthTipOne.wav", DateUtil.now().minusDays(6)));
        healthTipsHistory.add(new HealthTipsHistory(patientDocId, "healthTipNine.wav", DateUtil.now().minusDays(7)));
        healthTipsHistory.add(new HealthTipsHistory(patientDocId, "healthTipTwo.wav", DateUtil.now().minusDays(21)));
        healthTipsHistory.add(new HealthTipsHistory(patientDocId, "healthTipFive.wav", DateUtil.now().minusDays(22)));
        healthTipsHistory.add(new HealthTipsHistory(patientDocId, "healthTipSeven.wav", DateUtil.now().minusDays(13)));
        healthTipsHistory.add(new HealthTipsHistory(patientDocId, "healthTipEight.wav", DateUtil.now().minusDays(14)));
        when(allHealthTipsHistory.findByPatientId(patientDocId)).thenReturn(healthTipsHistory);
    }

    @Test
    public void shouldReturnApplicableHealthTipsWithHistory() {
        List<HealthTipService.PrioritizedHealthTip> applicableHealthTips = healthTipService.getApplicableHealthTips(patientDocId);
        assertEqualsIgnoreOrder(Arrays.asList("healthTipOne.wav", "healthTipTwo.wav", "healthTipThree.wav", "healthTipFour.wav", "healthTipFive.wav", "healthTipSix.wav", "healthTipSeven.wav", "healthTipEight.wav", "healthTipNine.wav"),
                extract(applicableHealthTips, on(HealthTipService.PrioritizedHealthTip.class).getHealthTipsHistory().getAudioFilename()));
    }

    @Test
    public void shouldFilterRecentlyPlayedTipsBasedOnTheirPriority() {
        assertEquals(Arrays.asList("healthTipSix.wav", "healthTipFive.wav", "healthTipFour.wav", "healthTipTwo.wav", "healthTipThree.wav"), healthTipService.getPlayList(patientDocId));
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

    private void assertEqualsIgnoreOrder(Collection<? extends Object> list1, Collection<? extends Object> list2) {
        assertEquals(new HashSet(list1), new HashSet(list2));
    }
}
