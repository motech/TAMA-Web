package org.motechproject.tama.web.model;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.facility.builder.ClinicBuilder;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class ClinicFilterTest {

    private ClinicFilter clinicFilter;

    @Before
    public void setup() {
        clinicFilter = new ClinicFilter(asList(ClinicBuilder.startRecording().withDefaults().build()));
    }

    @Test
    public void shouldHaveDefaultClinicOption() {
        assertNull(clinicFilter.getAllClinics().get(0).getId());
        assertTrue(clinicFilter.getAllClinics().get(0).getName().isEmpty());
    }
}
