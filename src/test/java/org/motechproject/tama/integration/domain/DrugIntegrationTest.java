package org.motechproject.tama.integration.domain;


import java.util.Arrays;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.motechproject.tama.domain.Brand;
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
		
		delete(drug);
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
		
		delete(drug);
	}
	
	@Test
	public void shouldReturnDrugCount() {
		long numberOfDrugs = Drug.countDrugs();
		Assert.assertEquals(0, numberOfDrugs);

		Drug drug = new Drug();
		drug.persist();
		numberOfDrugs = Drug.countDrugs();
		Assert.assertEquals(1, numberOfDrugs);
		
		delete(drug);
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
		
		Assert.assertEquals(2,drugs.size());
		Assert.assertTrue(drugs.containsAll(Arrays.asList(drugOne, drugTwo)));

        delete(drugOne);
        delete(drugTwo);
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
		Assert.assertEquals(brand.getName(), ((Brand)CollectionUtils.get(persistedBrands, 0)).getName());
		
		delete(drug);
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

		delete(drug);
	}
}
