package org.motechproject.tama.integration.repository;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.tama.domain.DosageType;
import org.motechproject.tama.repository.AllDosageTypes;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DosagesTest extends SpringIntegrationTest {

    @Autowired
    AllDosageTypes allDosageTypes;

    @Test
    public void testGetAllShouldSortAndReturnTheList() {
        DosageType twice = new DosageType("Twice Daily");
        DosageType morning = new DosageType("Morning Daily");
        DosageType evening = new DosageType("Evening Daily");
        allDosageTypes.add(twice);
        allDosageTypes.add(morning);
        allDosageTypes.add(evening);

        List<DosageType> all = allDosageTypes.getAll();
        Assert.assertEquals("Evening Daily", all.get(0).getType());
        Assert.assertEquals("Twice Daily", all.get(all.size() -1).getType());

        markForDeletion(evening);
        markForDeletion(morning);
        markForDeletion(twice);
    }
}
