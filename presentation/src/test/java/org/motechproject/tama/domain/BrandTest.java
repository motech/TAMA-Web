package org.motechproject.tama.domain;

import junit.framework.Assert;

import org.junit.Test;

public class BrandTest {

	@Test
	public void equals() {
		Brand brandOne = new Brand("one");

		Brand brandOneWithSameName = new Brand("one");

		Brand brandTwo = new Brand("two");
		
		Assert.assertFalse(brandOne.equals(brandTwo));
		Assert.assertTrue(brandOne.equals(brandOne));
		Assert.assertTrue(brandOne.equals(brandOneWithSameName));
	}
	
	@Test
	public void shouldSetCompany() {
		Company company = new Company("comp1");
		company.setId("c1");
		
		Brand brand = new Brand("One", company);
		
		Assert.assertEquals(company.getId(), brand.getCompanyId());
	}
}
