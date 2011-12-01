package org.motechproject.tamahealthtip.integration.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamahealthtip.domain.HealthTipsHistory;
import org.motechproject.tamahealthtip.repository.AllHealthTipsHistory;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-TAMACallFlow.xml")
public class AllHealthTipsHistoryTest {

    @Autowired
    private AllHealthTipsHistory allHealthTipsHistory;

    @Test
    public void shouldFilterByPatientId() throws Exception {
        HealthTipsHistory healthTipsHistory1 = new HealthTipsHistory("1", "foo", DateUtil.now());
        HealthTipsHistory healthTipsHistory2 = new HealthTipsHistory("2", "fuu", DateUtil.now());
        HealthTipsHistory healthTipsHistory3 = new HealthTipsHistory("1", "fii", DateUtil.now());
        allHealthTipsHistory.add(healthTipsHistory1);
        allHealthTipsHistory.add(healthTipsHistory2);
        allHealthTipsHistory.add(healthTipsHistory3);

        assertEquals(Arrays.asList("1", "1"), extract(allHealthTipsHistory.findByPatientId("1"), on(HealthTipsHistory.class).getPatientDocumentId()));

        allHealthTipsHistory.remove(healthTipsHistory1);
        allHealthTipsHistory.remove(healthTipsHistory2);
        allHealthTipsHistory.remove(healthTipsHistory3);
    }

    @Test
    public void shouldFilterByPatientIdAndAudioFilename() throws Exception {
        HealthTipsHistory healthTipsHistory1 = new HealthTipsHistory("1", "foo", DateUtil.now());
        HealthTipsHistory healthTipsHistory2 = new HealthTipsHistory("2", "fuu", DateUtil.now());
        HealthTipsHistory healthTipsHistory3 = new HealthTipsHistory("1", "fii", DateUtil.now());
        allHealthTipsHistory.add(healthTipsHistory1);
        allHealthTipsHistory.add(healthTipsHistory2);
        allHealthTipsHistory.add(healthTipsHistory3);

        assertEquals("fii", allHealthTipsHistory.findByPatientIdAndAudioFilename("1", "fii").getAudioFilename());

        allHealthTipsHistory.remove(healthTipsHistory1);
        allHealthTipsHistory.remove(healthTipsHistory2);
        allHealthTipsHistory.remove(healthTipsHistory3);
    }
}
