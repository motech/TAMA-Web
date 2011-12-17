package org.motechproject.tama.patient.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SystemCategoryTest {
    @Test
    public void shouldHaveAName() {
        String name = "Allergic";
        SystemCategory systemCategory = new SystemCategory();
        systemCategory.setName(SystemCategoryDefinition.Allergic.name());
        assertEquals(SystemCategoryDefinition.Allergic.name(), systemCategory.getName());
    }
}
