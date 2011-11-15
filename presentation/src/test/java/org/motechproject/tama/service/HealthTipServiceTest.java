package org.motechproject.tama.service;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
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
    }

    @Test
    public void shouldFilterPriority1TipsYoungerThan7Days() {
        healthTipService = Mockito.spy(healthTipService);
        DateTime now = DateUtil.now().minusDays(1);
        List<PrioritizedHealthTip> healthTips = new ArrayList<PrioritizedHealthTip>();
        healthTips.add(new PrioritizedHealthTip(new HealthTipsHistory(patient.getId(), "foo", now), 1));
        healthTips.add(new PrioritizedHealthTip(new HealthTipsHistory(patient.getId(), "bar", now.minusDays(4)), 1));
        healthTips.add(new PrioritizedHealthTip(new HealthTipsHistory(patient.getId(), "baz", now.minusDays(7)), 1));
        healthTips.add(new PrioritizedHealthTip(new HealthTipsHistory(patient.getId(), "qux", now.minusDays(8)), 1));
        when(healthTipService.getApplicableHealthTips(patient)).thenReturn(healthTips);

        assertEquals(Arrays.asList("baz", "qux"), healthTipService.getPlayList(patient));
    }

    @Test
    public void shouldFilterPriority1TipsYoungerThan14Days() {
        healthTipService = Mockito.spy(healthTipService);
        DateTime now = DateUtil.now().minusDays(1);
        List<PrioritizedHealthTip> healthTips = new ArrayList<PrioritizedHealthTip>();
        healthTips.add(new PrioritizedHealthTip(new HealthTipsHistory(patient.getId(), "foo", now.minusDays(12)), 2));
        healthTips.add(new PrioritizedHealthTip(new HealthTipsHistory(patient.getId(), "bar", now.minusDays(14)), 2));
        healthTips.add(new PrioritizedHealthTip(new HealthTipsHistory(patient.getId(), "baz", now.minusDays(15)), 2));
        healthTips.add(new PrioritizedHealthTip(new HealthTipsHistory(patient.getId(), "qux", now.minusDays(22)), 2));
        when(healthTipService.getApplicableHealthTips(patient)).thenReturn(healthTips);

        assertEquals(Arrays.asList("bar", "baz", "qux"), healthTipService.getPlayList(patient));
    }

    @Test
    public void shouldFilterPriority1TipsYoungerThan21Days() {
        healthTipService = Mockito.spy(healthTipService);
        DateTime now = DateUtil.now().minusDays(1);
        List<PrioritizedHealthTip> healthTips = new ArrayList<PrioritizedHealthTip>();
        healthTips.add(new PrioritizedHealthTip(new HealthTipsHistory(patient.getId(), "foo", now.minusDays(7)), 3));
        healthTips.add(new PrioritizedHealthTip(new HealthTipsHistory(patient.getId(), "bar", now.minusDays(14)), 3));
        healthTips.add(new PrioritizedHealthTip(new HealthTipsHistory(patient.getId(), "baz", now.minusDays(21)), 3));
        healthTips.add(new PrioritizedHealthTip(new HealthTipsHistory(patient.getId(), "qux", now.minusDays(23)), 3));
        when(healthTipService.getApplicableHealthTips(patient)).thenReturn(healthTips);

        assertEquals(Arrays.asList("baz", "qux"), healthTipService.getPlayList(patient));
    }
}
