package org.motechproject.tama.integration.domain;


import java.util.Arrays;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.motechproject.tama.domain.Brand;
import org.motechproject.tama.domain.Company;
import org.motechproject.tama.domain.Drug;

public class DrugIntegrationTest extends SpringIntegrationTest {


    @Test
    public void shouldPersistDrug() {
        Drug drug = new Drug();
        drug.setName("AZT");
        drug.persist();

        Drug actualDrug = Drug.findDrug(drug.getId());
        Assert.assertNotNull(actualDrug);
        Assert.assertEquals(drug.getName(), actualDrug.getName());

        markForDeletion(drug);
    }

    @Test
    public void mergeDrug() {
        Drug drug = new Drug();
        drug.setName("AZT");
        drug.persist();

        Assert.assertEquals("AZT", Drug.findDrug(drug.getId()).getName());
        drug.setName("new name");

        drug.merge();
        Assert.assertEquals("new name", Drug.findDrug(drug.getId()).getName());

        markForDeletion(drug);
    }

    @Test
    public void shouldReturnDrugCount() {
        long numberOfDrugs = Drug.countDrugs();
        Assert.assertEquals(0, numberOfDrugs);

        Drug drug = new Drug();
        drug.persist();
        numberOfDrugs = Drug.countDrugs();
        Assert.assertEquals(1, numberOfDrugs);

        markForDeletion(drug);
    }

    @Test
    public void shouldFindAllDrugs() {

        Drug drugOne = new Drug();
        Drug drugTwo = new Drug();

        List<Drug> drugs = Drug.findAllDrugs();
        Assert.assertTrue(drugs.isEmpty());

        drugOne.persist();
        drugTwo.persist();
        drugs = Drug.findAllDrugs();

        Assert.assertEquals(2, drugs.size());
        Assert.assertTrue(drugs.containsAll(Arrays.asList(drugOne, drugTwo)));

        markForDeletion(drugOne);
        markForDeletion(drugTwo);
    }

    @Test
    public void shouldRemoveDrugs() {

        Drug drug = new Drug();
        drug.persist();

        String id = drug.getId();
        drug.remove();
        Assert.assertNull(Drug.findDrug(id));
    }

    @Test
    public void shouldAddBrandsAndPersist() {
        Drug drug = new Drug();
        Brand brand = new Brand("name");
        drug.addBrand(brand);
        drug.persist();
        Drug persistedDrug = Drug.findDrug(drug.getId());

        Set<Brand> persistedBrands = persistedDrug.getBrands();
        Assert.assertEquals(1, persistedBrands.size());
        Assert.assertEquals(brand.getName(), ((Brand) CollectionUtils.get(persistedBrands, 0)).getName());

        markForDeletion(drug);
    }

    @Test
    public void shouldRemoveBrandsAndPersist() {
        Drug drug = new Drug();
        Brand brandOne = new Brand("one");
        drug.addBrand(brandOne);

        Brand brandTwo = new Brand("two");
        drug.addBrand(brandTwo);

        drug.persist();
        Drug persistedDrug = Drug.findDrug(drug.getId());

        Set<Brand> persistedBrands = persistedDrug.getBrands();
        Assert.assertEquals(2, persistedBrands.size());
        drug.removeBrand(brandOne);
        drug.merge();

        persistedDrug = Drug.findDrug(drug.getId());
        Assert.assertEquals(1, persistedDrug.getBrands().size());

        markForDeletion(drug);
    }

    @Test
    public void shouldPersistCompany() {

        Company company = createCompany("companyName");

        Drug drug = new Drug();
        drug.setName("drugName");
        Brand brand = new Brand("TestBrand");
        brand.setCompanyId(company.getId());
        drug.addBrand(brand);

        drug.persist();

        String companyName = ((Brand) CollectionUtils.get(drug.getBrands(), 0)).getCompany().getName();
        Assert.assertEquals(company.getName(), companyName);
        markForDeletion(drug);

    }

    private Company createCompany(String companyName) {
        Company company = new Company();
        company.setName(companyName);

        company.persist();

        markForDeletion(company);
        return company;
    }
}
