package org.motechproject.tama.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SystemCategoryTest {

    @Test
    public void shouldHaveAName() {
        String name = "Allergic";
        SystemCategory systemCategory = new SystemCategory();
        systemCategory.setName(SystemCategoryDefiniton.Allergic.name());
        assertEquals(SystemCategoryDefiniton.Allergic.name(), systemCategory.getName());
    }

}
