package org.motechproject.tama.integration.domain;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.tama.domain.DosageType;

public class DosageTypeIntegrationTest extends SpringIntegrationTest {
	
	@Test
	public void shouldPersist() {
		DosageType dosageType = new DosageType("OnceDaily");
		dosageType.persist();
		
		DosageType persistedDosageType = DosageType.findDosageType(dosageType.getId());
		
		Assert.assertEquals(dosageType, persistedDosageType);
		
		markForDeletion(dosageType);
	}
	
	@Test
	public void shouldRemove() {
		DosageType dosageTypeOnce = new DosageType("OnceDaily");
		dosageTypeOnce.persist();
		dosageTypeOnce.remove();
		
		DosageType persistedDosageType = DosageType.findDosageType(dosageTypeOnce.getId());
		Assert.assertNull(persistedDosageType);
		
		markForDeletion(dosageTypeOnce);
	}
	
	@Test
	public void shouldFindAll() {
		DosageType dosageTypeOnce = new DosageType("OnceDaily");
		dosageTypeOnce.persist();
		DosageType dosageTypeTwice = new DosageType("TwiceDaily");
		dosageTypeTwice.persist();
		
		List<DosageType> dosageTypes = DosageType.findAllDosageTypes();
		Assert.assertEquals(2, dosageTypes.size());
		Assert.assertTrue(dosageTypes.containsAll(Arrays.asList(dosageTypeOnce, dosageTypeTwice)));
		
		markForDeletion(dosageTypeOnce);
		markForDeletion(dosageTypeTwice);
	}
	
	@Test
	public void shouldMerge() {
		DosageType dosageTypeOnce = new DosageType("OnceDaily");
		dosageTypeOnce.persist();

		dosageTypeOnce.setType("Twice Daily");
		dosageTypeOnce.merge();
		
		DosageType persistedDosageType = DosageType.findDosageType(dosageTypeOnce.getId());
		markForDeletion(persistedDosageType);
		
		Assert.assertEquals(dosageTypeOnce.getType(), persistedDosageType.getType());
	}
}
