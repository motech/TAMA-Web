package org.motechproject.tama.healthtips.integration.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.healthtips.domain.HealthTipsHistory;
import org.motechproject.tama.healthtips.repository.AllHealthTipsHistory;
import org.motechproject.tama.refdata.repository.AllHealthTips;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationHealthTipsContext.xml")
public class AllHealthTipsHistoryTest {

    @Autowired
    private AllHealthTipsHistory allHealthTipsHistory;

    @Autowired
    private AllHealthTips allHealthTips;

    HealthTipsHistory healthTipsHistory1;
    HealthTipsHistory healthTipsHistory2;
    HealthTipsHistory healthTipsHistory3;

    @Before
    public void setup(){

        healthTipsHistory1 = new HealthTipsHistory("1", "audio1", DateUtil.now());
        healthTipsHistory2 = new HealthTipsHistory("2", "audio2", DateUtil.now());
        healthTipsHistory3 = new HealthTipsHistory("1", "audio3", DateUtil.now());
        allHealthTipsHistory.add(healthTipsHistory1);
        allHealthTipsHistory.add(healthTipsHistory2);
        allHealthTipsHistory.add(healthTipsHistory3);
    }

    @After
    public void tearDown(){
        allHealthTipsHistory.remove(healthTipsHistory1);
        allHealthTipsHistory.remove(healthTipsHistory2);
        allHealthTipsHistory.remove(healthTipsHistory3);
    }

    @Test
    public void shouldFilterByPatientId() throws Exception {
        assertEquals(Arrays.asList("1", "1"), extract(allHealthTipsHistory.findByPatientId("1"), on(HealthTipsHistory.class).getPatientDocumentId()));
    }

    @Test
    public void shouldFilterByPatientIdAndAudioFilename() throws Exception {
        assertEquals("audio3", allHealthTipsHistory.findByPatientIdAndAudioFilename("1", "audio3").getAudioFilename());
    }

    @Test
    public void shouldIncrementPlayCount(){
        HealthTipsHistory tip = new HealthTipsHistory("patient1", "audio1", DateTime.now().minusDays(2));

        assertEquals(1, tip.getPlayCount().intValue());

        tip.setLastPlayed(DateTime.now());

        assertEquals(2, tip.getPlayCount().intValue());
    }
}
