package org.motechproject.tama.integration.domain;


import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.motechproject.tama.domain.Drug;
import org.motechproject.tama.repository.Drugs;
import org.springframework.beans.factory.annotation.Autowired;

public class DrugIntegrationTest extends SpringIntegrationTest {

	@Autowired
	private Drugs drugs;
	
	@Test
	public void shouldPersistDrug() {

		Drug drug = new Drug();
		drug.setName("AZT");
		drug.setDrugs(drugs);
		drug.persist();
		
		Drug actualDrug = Drug.findDrug(drug.getId());
		Assert.assertNotNull(actualDrug);
		Assert.assertEquals(drug.getName(), actualDrug.getName());
		
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
}
