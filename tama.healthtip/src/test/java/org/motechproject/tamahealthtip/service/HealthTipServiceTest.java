package org.motechproject.tamahealthtip.service;

import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.tamadomain.builder.PatientBuilder;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.domain.TreatmentAdvice;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamadomain.repository.AllTreatmentAdvices;
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

    @Mock
    private AllHealthTipsHistory allHealthTipsHistory;
    @Mock
    private StatelessKnowledgeSession healthTipsSession;
    @Mock
    private HealthTipRuleService healthTipRuleService;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private AllPatients allPatients;
    @Mock
    private TreatmentAdvice treatmentAdvice;

    private String patientId;
    private String patientDocId;
    private Patient patient;

    HealthTipService healthTipService;

    @Before()
    public void setUp() {
        initMocks(this);

        patientId = "patientId";
        patientDocId = "patientDocId";
        patient = PatientBuilder.startRecording().withDefaults().build();
        when(allPatients.get(patient.getId())).thenReturn(patient);

        when(allTreatmentAdvices.currentTreatmentAdvice(patient.getId())).thenReturn(treatmentAdvice);
        when(treatmentAdvice.getStartDate()).thenReturn(DateUtil.today().toDate());

        healthTipService = new HealthTipService(allHealthTipsHistory, healthTipRuleService, allTreatmentAdvices, allPatients);
        healthTipService = Mockito.spy(healthTipService);

        Map<String, String> healthTipMap = new HashMap<String, String>();
        healthTipMap.put("healthTipOne", "1");
        healthTipMap.put("healthTipTwo", "2");
        healthTipMap.put("healthTipThree", "3");
        healthTipMap.put("healthTipFour", "2");
        healthTipMap.put("healthTipFive", "1");
        healthTipMap.put("healthTipSix", "1");
        healthTipMap.put("healthTipSeven", "2");
        healthTipMap.put("healthTipEight", "3");
        healthTipMap.put("healthTipNine", "2");

        List<HealthTipsHistory> healthTipsHistory = new ArrayList<HealthTipsHistory>();
        healthTipsHistory.add(new HealthTipsHistory(patient.getId(), "healthTipOne", DateUtil.now().minusDays(6)));
        healthTipsHistory.add(new HealthTipsHistory(patient.getId(), "healthTipNine", DateUtil.now().minusDays(7)));
        healthTipsHistory.add(new HealthTipsHistory(patient.getId(), "healthTipTwo", DateUtil.now().minusDays(21)));
        healthTipsHistory.add(new HealthTipsHistory(patient.getId(), "healthTipFive", DateUtil.now().minusDays(22)));
        healthTipsHistory.add(new HealthTipsHistory(patient.getId(), "healthTipSeven", DateUtil.now().minusDays(13)));
        healthTipsHistory.add(new HealthTipsHistory(patient.getId(), "healthTipEight", DateUtil.now().minusDays(14)));

        when(healthTipRuleService.getHealthTipsFromRuleEngine(DateUtil.today(), patient)).thenReturn(healthTipMap);
        when(allHealthTipsHistory.findByPatientId(patient.getId())).thenReturn(healthTipsHistory);
    }

    @Test
    public void shouldReturnApplicableHealthTipsWithHistory() {
        List<HealthTipService.PrioritizedHealthTip> applicableHealthTips = healthTipService.getApplicableHealthTips(patient.getId());
        assertEqualsIgnoreOrder(Arrays.asList("healthTipOne", "healthTipTwo", "healthTipThree", "healthTipFour", "healthTipFive", "healthTipSix", "healthTipSeven", "healthTipEight", "healthTipNine"),
                extract(applicableHealthTips, on(HealthTipService.PrioritizedHealthTip.class).getHealthTipsHistory().getAudioFilename()));
        verify(healthTipRuleService).getHealthTipsFromRuleEngine(DateUtil.today(), patient);
    }

    @Test
    public void shouldFilterRecentlyPlayedTipsBasedOnTheirPriority() {
        assertEquals(Arrays.asList("healthTipSix", "healthTipFive", "healthTipFour", "healthTipTwo", "healthTipThree"), healthTipService.getPlayList(patient.getId()));
        verify(healthTipRuleService).getHealthTipsFromRuleEngine(DateUtil.today(), patient);
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

    private void assertEqualsIgnoreOrder(Collection<String> list1, Collection<String> list2) {
        assertEquals(new HashSet<String>(list1), new HashSet<String>(list2));
    }
}
