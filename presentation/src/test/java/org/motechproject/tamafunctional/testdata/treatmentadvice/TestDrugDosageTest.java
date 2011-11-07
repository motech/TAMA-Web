package org.motechproject.tamafunctional.testdata.treatmentadvice;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.domain.Drug;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class TestDrugDosageTest {
    @Test
    public void createMorningDosage() {
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2011, 8, 1, 11, 30, 0));
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Foo");
        assertEquals(1, drugDosages.length);
        assertEquals("11:30", drugDosages[0].dosageSchedule());
        assertEquals(TestDrugDosage.MORNING_DAILY, drugDosages[0].dosageType());
    }

    @Test
    public void createEveningDosage() {
        mockStatic(DateUtil.class);
        when(DateUtil.now()).thenReturn(new DateTime(2011, 8, 1, 21, 45, 0));
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Foo");
        assertEquals(1, drugDosages.length);
        assertEquals("09:45", drugDosages[0].dosageSchedule());
        assertEquals(TestDrugDosage.EVENING_DAILY, drugDosages[0].dosageType());
    }
}
