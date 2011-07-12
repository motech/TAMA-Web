package org.motechproject.tama.repository;

import org.junit.Test;
import org.motechproject.tama.domain.Administrator;
import org.motechproject.tama.integration.domain.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class AdministratorIntegrationTest extends SpringIntegrationTest {
    @Autowired
    private Administrators administrators;

    @Test
    public void shouldFetchTheAdministrator() {
        Administrator administrator = new Administrator();
        administrator.setUsername("admin");
        administrator.setPassword("password");

        administrators.add(administrator);
        markForDeletion(administrator);

        Administrator dbAdministrator = administrators.findByUserNameAndPassword("admin", "password");
        assertEquals(dbAdministrator.getUsername(), "admin");
        assertEquals(dbAdministrator.getPassword(), "password");
    }

}
