package org.motechproject.tama.refdata.integration.repository;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.tama.refdata.domain.DosageType;
import org.motechproject.tama.refdata.repository.AllDosageTypes;
import org.motechproject.tamacommon.integration.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@ContextConfiguration(locations = "classpath*:applicationRefDataContext.xml", inheritLocations = false)
public class DosagesTest extends SpringIntegrationTest {

    @Autowired
    AllDosageTypes allDosageTypes;

    @Test
    public void testGetAllShouldSortAndReturnTheList() {
        DosageType twice = new DosageType("Twice Daily");
        DosageType morning = new DosageType("Morning Daily");
        DosageType evening = new DosageType("Evening Daily");
        DosageType variable = new DosageType("Variable Dosage");
        allDosageTypes.add(twice);
        allDosageTypes.add(morning);
        allDosageTypes.add(evening);
        allDosageTypes.add(variable);

        List<DosageType> all = allDosageTypes.getAll();
        Assert.assertEquals("Evening Daily", all.get(0).getType());
        Assert.assertEquals("Variable Dosage", all.get(all.size() - 1).getType());

        markForDeletion(evening);
        markForDeletion(morning);
        markForDeletion(twice);
        markForDeletion(variable);
    }
}
