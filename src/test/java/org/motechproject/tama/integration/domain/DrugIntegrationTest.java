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
		
		Assert.assertNotNull(Drug.findDrug(drug.getId()));
	}
}
