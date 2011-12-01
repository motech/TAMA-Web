package org.motechproject.tamadomain.integration.repository;

import org.junit.Test;
import org.motechproject.tamacommon.integration.repository.SpringIntegrationTest;
import org.motechproject.tamadomain.domain.Administrator;
import org.motechproject.tamadomain.repository.AllAdministrators;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

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
