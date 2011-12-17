package org.motechproject.tama.refdata.integration.repository;

import org.junit.Test;
import org.motechproject.tama.refdata.domain.Administrator;
import org.motechproject.tama.refdata.repository.AllAdministrators;
import org.motechproject.tamacommon.integration.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationRefDataContext.xml", inheritLocations = false)
public class AllAdministratorsTest extends SpringIntegrationTest {
    @Autowired
    private AllAdministrators allAdministrators;

    @Test
    public void shouldFetchTheAdministratorWithPasswordDecrypted() {
        Administrator administrator = new Administrator();
        administrator.setUsername("new_admin");
        administrator.setPassword("password");

        allAdministrators.add(administrator);
        markForDeletion(administrator);

        Administrator dbAdministrator = allAdministrators.findByUserNameAndPassword("new_admin", "password");
        assertEquals(dbAdministrator.getUsername(), "new_admin");
        assertEquals(dbAdministrator.getPassword(), "password");
    }
}
