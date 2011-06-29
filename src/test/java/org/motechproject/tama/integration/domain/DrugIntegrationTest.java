package org.motechproject.tama.integration.domain;


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
}
