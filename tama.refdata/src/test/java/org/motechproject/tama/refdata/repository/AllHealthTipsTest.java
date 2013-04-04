package org.motechproject.tama.refdata.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.refdata.domain.HealthTip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:/applicationRefDataContext.xml")
public class AllHealthTipsTest extends SpringIntegrationTest {
    @Autowired
    private AllHealthTips allHealthTips;

    @Before
    public void setup() {
        allHealthTips.removeAll();
        allHealthTips.add(HealthTip.newHealthTip("Category1", "AudioFileName1", 1, 1, 1, 1));
        allHealthTips.add(HealthTip.newHealthTip("Category1", "AudioFileName2", 2, 2, 2, 2));
        allHealthTips.add(HealthTip.newHealthTip("Category2", "AudioFileName3", 1, 1, 3, 3));
        allHealthTips.add(HealthTip.newHealthTip("Category2", "AudioFileName4", 2, 2, 4, 4));
    }

    @After
    public void tearDown(){
        allHealthTips.removeAll();
    }

    @Test
    public void shouldReturnHealthTipsWhenFindByCategory(){
        List<HealthTip> healthTips = allHealthTips.findByCategory("Category1");

        assertEquals(2, healthTips.size());
        assertEquals("AudioFileName1", healthTips.get(0).getAudioFileName());
        assertEquals("AudioFileName2", healthTips.get(1).getAudioFileName());
    }

    @Test
    public void shouldReturnEmptyListWhenFindByCategory(){
        List<HealthTip> healthTips = allHealthTips.findByCategory("nonExistingCategory");
        assertEquals(0, healthTips.size());
    }

    @Test
    public void shouldReturnAllHealthTipsWhenCategoryIsNull(){
        List<HealthTip> healthTips = allHealthTips.findByCategory(null);
        assertEquals(4, healthTips.size());
        assertEquals("AudioFileName1", healthTips.get(0).getAudioFileName());
        assertEquals("AudioFileName2", healthTips.get(1).getAudioFileName());
        assertEquals("AudioFileName3", healthTips.get(2).getAudioFileName());
        assertEquals("AudioFileName4", healthTips.get(3).getAudioFileName());
    }
}
