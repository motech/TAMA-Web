package org.motechproject.tamahealthtip.integration.repository;

import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.tamahealthtip.domain.HealthTipsHistory;
import org.motechproject.tamacommon.integration.repository.SpringIntegrationTest;
import org.motechproject.tamahealthtip.repository.AllHealthTipsHistory;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static junit.framework.Assert.assertEquals;

public class AllHealthTipsHistoryTest extends SpringIntegrationTest {

    @Autowired
    private AllHealthTipsHistory allHealthTipsHistory;

    @Test
    @Ignore
    public void shouldFilterByPatientId() throws Exception {
        HealthTipsHistory healthTipsHistory1 = new HealthTipsHistory("1", "foo", DateUtil.now());
        HealthTipsHistory healthTipsHistory2 = new HealthTipsHistory("2", "fuu", DateUtil.now());
        HealthTipsHistory healthTipsHistory3 = new HealthTipsHistory("1", "fii", DateUtil.now());
        allHealthTipsHistory.add(healthTipsHistory1);
        allHealthTipsHistory.add(healthTipsHistory2);
        allHealthTipsHistory.add(healthTipsHistory3);

        assertEquals(Arrays.asList("1", "1"), extract(allHealthTipsHistory.findByPatientId("1"), on(HealthTipsHistory.class).getPatientDocumentId()));

        markForDeletion(healthTipsHistory1, healthTipsHistory2, healthTipsHistory3);
    }

    @Test
    @Ignore
    public void shouldFilterByPatientIdAndAudioFilename() throws Exception {
        HealthTipsHistory healthTipsHistory1 = new HealthTipsHistory("1", "foo", DateUtil.now());
        HealthTipsHistory healthTipsHistory2 = new HealthTipsHistory("2", "fuu", DateUtil.now());
        HealthTipsHistory healthTipsHistory3 = new HealthTipsHistory("1", "fii", DateUtil.now());
        allHealthTipsHistory.add(healthTipsHistory1);
        allHealthTipsHistory.add(healthTipsHistory2);
        allHealthTipsHistory.add(healthTipsHistory3);

        assertEquals("fii", allHealthTipsHistory.findByPatientIdAndAudioFilename("1", "fii").getAudioFilename());

        markForDeletion(healthTipsHistory1, healthTipsHistory2, healthTipsHistory3);
    }
}
