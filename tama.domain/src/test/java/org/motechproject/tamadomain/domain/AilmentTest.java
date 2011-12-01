package org.motechproject.tamadomain.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AilmentTest {

    @Test
    public void shouldHaveNameAndState() {
        Ailment ailment = AilmentDefinition.Asthma.getAilment();
        ailment.setState(AilmentState.YES);
        assertEquals(ailment.getDefinition(), AilmentDefinition.Asthma);
        assertEquals(ailment.getState(), AilmentState.YES);
    }

    @Test
    public void shouldHaveHistoryOfAilment() {
        Ailment ailment = AilmentDefinition.Asthma.getAilment();

        assertFalse(ailment.everHadTheAilment());

        ailment.setState(AilmentState.YES_WITH_HISTORY);
        assertTrue(ailment.everHadTheAilment());

        ailment.setState(AilmentState.YES);
        assertTrue(ailment.everHadTheAilment());
    }
}
