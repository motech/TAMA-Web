package org.motechproject.tama.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AilmentTest {

    @Test
    public void shouldHaveNameAndState() {
        Ailment ailment = AilmentDefinition.Asthma.getAilment();
        ailment.setState(AilmentState.YES);
        assertEquals(ailment.getDefinition(), AilmentDefinition.Asthma);
        assertEquals(ailment.getState(), AilmentState.YES);
    }
}
