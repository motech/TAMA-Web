package org.motechproject.tamadomain.domain;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

public class DrugCompositionTest {

	@Test
	public void shouldSetDrugIdsOnAddingDrug() {
		DrugComposition drugComposition = new DrugComposition();
		Drug drug = new Drug("AZT");
		drug.setId("did");
		
		drugComposition.addDrugId(drug);
		
		Assert.assertEquals(1, drugComposition.getDrugIds().size());
		Assert.assertEquals(drug.getId(), CollectionUtils.get(drugComposition.getDrugIds(), 0));
	}
	
	@Test
	public void shouldGenerateRegimenCompositionId() {
		DrugComposition drugComposition = new DrugComposition();
		Assert.assertNotNull(drugComposition.getId());
	}
}
