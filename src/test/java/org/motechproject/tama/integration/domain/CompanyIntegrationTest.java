package org.motechproject.tama.integration.domain;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.tama.builder.CompanyBuilder;
import org.motechproject.tama.domain.Company;
import org.motechproject.tama.repository.Companies;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class CompanyIntegrationTest extends SpringIntegrationTest{
    @Autowired
    private Companies companies;

    @Test
    public void shouldPersistCompany() {

        Company testCompany = CompanyBuilder.startRecording().withName("TestComapny").build();
        testCompany.setCompanies(companies);
        testCompany.persist();

        Company company = Company.findCompany(testCompany.getId());
        Assert.assertNotNull(company);
        Assert.assertEquals(testCompany.getName(), company.getName());

        delete(testCompany);
    }

    public void shouldUpdateCompany() {

        Company testCompany = CompanyBuilder.startRecording().withName("TestComapny").build();
        testCompany.setCompanies(companies);
        testCompany.persist();

        testCompany.setName("NewCompany");
        testCompany.merge();
        Assert.assertEquals("NewCompany", Company.findCompany(testCompany.getId()));
        delete(testCompany);
    }

    @Test
    public void shouldReturnCompanyCount() {
        long numberOfCompanies = Company.countCompanys();
        Assert.assertEquals(0, numberOfCompanies);

        Company company = CompanyBuilder.startRecording().build();
        company.persist();
        numberOfCompanies = Company.countCompanys();
        Assert.assertEquals(1, numberOfCompanies);

        delete(company);
    }

    @Test
    public void shouldFindAllCompanies() {

        Company companyOne = new Company();
        Company companyTwo = new Company();

        List<Company> companyList = Company.findAllCompanys();
        Assert.assertTrue(companyList.isEmpty());

        companyOne.persist();
        companyTwo.persist();
        companyList = Company.findAllCompanys();

        Assert.assertEquals(2,companyList.size());
        Assert.assertTrue(companyList.containsAll(Arrays.asList(companyOne, companyTwo)));

        delete(companyOne);
        delete(companyTwo);
    }
    @Test
    public void shouldRemoveCompanies() {

        Company company = CompanyBuilder.startRecording().build();
        company.persist();

        String id = company.getId();
        company.remove();
        Assert.assertNull(Company.findCompany(id));

        delete(company);
    }

}
