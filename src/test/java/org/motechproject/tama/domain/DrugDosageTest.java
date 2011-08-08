package org.motechproject.tama.domain;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

public class DrugDosageTest {
    @Test
    public void shouldNotSetDefaultEndDate() {
        LocalDate startDate = DateUtil.newDate(2010, 10, 10);

        DrugDosage drugDosage = new DrugDosage();
        drugDosage.setStartDate(startDate);

        assertEquals(null, drugDosage.getEndDate());
    }

    @Test
    public void shouldSortMedicineBrandsInAlphabeticalOrderCaseInsensitive() {
        Set<Brand> brands = new HashSet<Brand>();
        brands.add(new Brand("Estiva"));
        brands.add(new Brand("Efferven"));
        brands.add(new Brand("efcure"));
        brands.add(new Brand("Stocrin"));
        brands.add(new Brand("Efavir"));

        DrugDosage drugDosage = new DrugDosage();
        drugDosage.setBrands(brands);
        Set<Brand> sortedBrands = drugDosage.getBrands();

        Assert.assertEquals(5, sortedBrands.size());
        Brand[] sortedBrandsArray = sortedBrands.toArray(new Brand[0]);
        Assert.assertEquals("Efavir", sortedBrandsArray[0].getName());
        Assert.assertEquals("efcure", sortedBrandsArray[1].getName());
        Assert.assertEquals("Efferven", sortedBrandsArray[2].getName());
        Assert.assertEquals("Estiva", sortedBrandsArray[3].getName());
        Assert.assertEquals("Stocrin", sortedBrandsArray[4].getName());
    }
}
