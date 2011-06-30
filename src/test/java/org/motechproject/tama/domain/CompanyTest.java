package org.motechproject.tama.domain;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.ektorp.DocumentNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.repository.Companies;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Company.class)

public class CompanyTest {
    @Test
    public void findByIdShouldReturnNullIfNotFound() {

        Companies companies = mock(Companies.class);
        final String id = "id";
        when(companies.get(id)).thenThrow(new DocumentNotFoundException("NotFoundPath")) ;

        PowerMockito.spy(Company.class);
        when(Company.company()).thenReturn(companies);

        Company company = Company.findCompany(id);
        Assert.assertNull(company);
    }

}
