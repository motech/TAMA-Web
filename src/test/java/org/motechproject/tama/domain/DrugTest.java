package org.motechproject.tama.domain;

import junit.framework.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Drug.class)
public class DrugTest {

    @Test
    public void addBrands() {
        Drug drug = new Drug();
        Brand brandOne = new Brand("one");
        Brand brandTwo = new Brand("two");
        drug.addBrand(brandOne);
        drug.addBrand(brandTwo);

        Assert.assertEquals(2, drug.getBrands().size());
        Assert.assertTrue(drug.getBrands().containsAll(Arrays.asList(brandOne, brandTwo)));
    }

    @Test
    public void removeBrands() {
        Drug drug = new Drug();
        Brand brandOne = new Brand("brand one");
        Brand brandTwo = new Brand("brand two");
        drug.addBrand(brandOne);
        drug.addBrand(brandTwo);

        Assert.assertEquals(2, drug.getBrands().size());
        drug.removeBrand(brandTwo);

        Assert.assertEquals(1, drug.getBrands().size());
        Assert.assertEquals(brandOne.getName(), ((Brand) CollectionUtils.get(drug.getBrands(), 0)).getName());
    }

    @Test
    public void shouldReturnBrandAndDrugCombinationAsFullName() {
        Drug drug = new Drug("D1+D2");
        Company company = new Company();
        company.setId("C1");
        Brand brandOne = new Brand("B1", company);
        drug.addBrand(brandOne);

        Assert.assertEquals("D1D2_B1", drug.fullName(brandOne.getCompanyId()));
    }
}
