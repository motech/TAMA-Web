package org.motechproject.tama.integration.repository;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.tama.domain.DosageType;
import org.motechproject.tama.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.repository.DosageTypes;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DosagesTest extends SpringIntegrationTest {

    @Autowired
    DosageTypes dosageTypes;

    @Test
    public void testGetAllShouldSortAndReturnTheList() {
        DosageType twice = new DosageType("Twice Daily");
        DosageType morning = new DosageType("Morning Daily");
        DosageType evening = new DosageType("Evening Daily");
        dosageTypes.add(twice);
        dosageTypes.add(morning);
        dosageTypes.add(evening);

        List<DosageType> all = dosageTypes.getAll();
        Assert.assertEquals("Evening Daily", all.get(0).getType());
        Assert.assertEquals("Twice Daily", all.get(all.size() -1).getType());

        markForDeletion(evening);
        markForDeletion(morning);
        markForDeletion(twice);
    }
}
