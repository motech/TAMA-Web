package org.motechproject.tama.domain;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

public class RegimenCompositionTest {

	@Test
	public void shouldSetDrugIdsOnAddingDrug() {
		RegimenComposition regimenComposition = new RegimenComposition();
		Drug drug = new Drug("AZT");
		drug.setId("did");
		
		regimenComposition.addDrug(drug);
		
		Assert.assertEquals(1, regimenComposition.getDrugIds().size());
		Assert.assertEquals(drug.getId(), CollectionUtils.get(regimenComposition.getDrugIds(), 0));
	}
	
	@Test
	public void shouldGenerateRegimenCompositionId() {
		RegimenComposition regimenComposition = new RegimenComposition();
		Assert.assertNotNull(regimenComposition.getRegimentCompositionId());
	}
}
