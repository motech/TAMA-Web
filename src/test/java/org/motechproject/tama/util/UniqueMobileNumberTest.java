package org.motechproject.tama.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class UniqueMobileNumberTest {
    @Test
    public void generateUniquePhoneNumber() {
        long number1 = UniqueMobileNumber.generate();
        long number2 = UniqueMobileNumber.generate();
        assertEquals(number1 + 1, number2);
    }

    @Test
    public void shouldStartFromBegining() {
        UniqueMobileNumber.startOver();
        assertEquals(UniqueMobileNumber.StartingNumber + 1, UniqueMobileNumber.generate());
    }
}
