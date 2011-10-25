package org.motechproject.tama.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AilmentsTest {

    @Test
    public void shouldGetAilment_GivenAnAilmentDefinition() {

        Ailments ailments = SystemCategoryDefiniton.Other.getAilments();

        Ailment diabeticAilment = ailments.getAilment(AilmentDefinition.Diabetes);
        assertEquals("Diabetes", diabeticAilment.getDefinition().getValue());

        Ailment noAilment = ailments.getAilment(AilmentDefinition.Insomnia);
        assertNull(noAilment);
    }
}
