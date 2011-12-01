package org.motechproject.tamadomain.domain;

import org.junit.Test;
import org.motechproject.tamadomain.domain.SystemCategoryDefinition;

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
