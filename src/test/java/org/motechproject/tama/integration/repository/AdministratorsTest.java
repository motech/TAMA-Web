package org.motechproject.tama.integration.repository;

import org.junit.Test;
import org.motechproject.tama.domain.Administrator;
import org.motechproject.tama.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.repository.Administrators;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class AdministratorsTest extends SpringIntegrationTest {
    @Autowired
    private Administrators administrators;

    @Test
    public void shouldFetchTheAdministratorWithPasswordDecrypted() {
        Administrator administrator = new Administrator();
        administrator.setUsername("new_admin");
        administrator.setPassword("password");

        administrators.add(administrator);
        markForDeletion(administrator);

        Administrator dbAdministrator = administrators.findByUserNameAndPassword("new_admin", "password");
        assertEquals(dbAdministrator.getUsername(), "new_admin");
        assertEquals(dbAdministrator.getPassword(), "password");
    }

}
