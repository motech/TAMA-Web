package org.motechproject.tama.domain;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.ektorp.DocumentNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.repository.Drugs;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Drug.class)
public class DrugTest {

	@Test
	public void findByIdShouldReturnNullIfNotFound() {

        Drugs drugs = mock(Drugs.class);
        final String id = "id";
        when(drugs.get(id)).thenThrow(new DocumentNotFoundException("NotFoundPath")) ;

        PowerMockito.spy(Drug.class);
        when(Drug.drugs()).thenReturn(drugs);

        Drug drug = Drug.findDrug(id);
        Assert.assertNull(drug);
    }
	
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
		Assert.assertEquals(brandOne.getName(), ((Brand)CollectionUtils.get(drug.getBrands(), 0)).getName());
	}
}
