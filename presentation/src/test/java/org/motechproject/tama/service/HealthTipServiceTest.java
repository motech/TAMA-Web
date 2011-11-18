package org.motechproject.tama.service;

import ch.lambdaj.Lambda;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.HealthTipsHistory;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.repository.AllHealthTipsHistory;
import org.motechproject.util.DateUtil;

import java.util.*;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.motechproject.tama.service.HealthTipService.*;

public class HealthTipServiceTest {

    
    HealthTipService healthTipService;
    private Patient patient;

    @Mock
    private AllHealthTipsHistory allHealthTipsHistory;

    @Before()
    public void setUp() {
        initMocks(this);

        patient = PatientBuilder.startRecording().withDefaults().build();

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
        healthTipsHistory.add(new HealthTipsHistory(patient.getId(), "healthTipOne.wav", DateUtil.now().minusDays(6)));
        healthTipsHistory.add(new HealthTipsHistory(patient.getId(), "healthTipNine.wav", DateUtil.now().minusDays(7)));
        healthTipsHistory.add(new HealthTipsHistory(patient.getId(), "healthTipTwo.wav", DateUtil.now().minusDays(21)));
        healthTipsHistory.add(new HealthTipsHistory(patient.getId(), "healthTipFive.wav", DateUtil.now().minusDays(22)));
        healthTipsHistory.add(new HealthTipsHistory(patient.getId(), "healthTipSeven.wav", DateUtil.now().minusDays(13)));
        healthTipsHistory.add(new HealthTipsHistory(patient.getId(), "healthTipEight.wav", DateUtil.now().minusDays(14)));
        when(allHealthTipsHistory.findByPatientId(patient.getId())).thenReturn(healthTipsHistory);
    }

    @Test
    public void shouldReturnApplicableHealthTipsWithHistory() {
        List<PrioritizedHealthTip> applicableHealthTips = healthTipService.getApplicableHealthTips(patient.getId());
        assertEqualsIgnoreOrder(Arrays.asList("healthTipOne.wav", "healthTipTwo.wav", "healthTipThree.wav", "healthTipFour.wav", "healthTipFive.wav", "healthTipSix.wav", "healthTipSeven.wav", "healthTipEight.wav", "healthTipNine.wav"),
                extract(applicableHealthTips, on(PrioritizedHealthTip.class).getHealthTipsHistory().getAudioFilename()));
    }

    @Test
    public void shouldFilterRecentlyPlayedTipsBasedOnTheirPriority() {
        assertEquals(Arrays.asList("healthTipSix.wav", "healthTipFive.wav", "healthTipFour.wav", "healthTipTwo.wav", "healthTipThree.wav"), healthTipService.getPlayList(patient.getId()));
    }

    @Test
    public void shouldAddHistoryOnMarkAsRead() {
        healthTipService.markAsPlayed(patient.getId(), "file");
        verify(allHealthTipsHistory).add(any(HealthTipsHistory.class));
    }
    
    @Test
    public void shouldUpdateHistoryOnMarkAsRead() {
        final String AUDIO_FILE = "file";
        HealthTipsHistory healthTipHistory = new HealthTipsHistory(patient.getId(), AUDIO_FILE, DateUtil.now().minusDays(10));
        when(allHealthTipsHistory.findByPatientIdAndAudioFilename(patient.getPatientId(), AUDIO_FILE)).thenReturn(healthTipHistory);
        healthTipService.markAsPlayed(patient.getId(), AUDIO_FILE);
        verify(allHealthTipsHistory).add(any(HealthTipsHistory.class));
    }

    private void assertEqualsIgnoreOrder(Collection<? extends Object> list1, Collection<? extends Object> list2) {
        assertEquals(new HashSet(list1), new HashSet(list2));
    }
}
