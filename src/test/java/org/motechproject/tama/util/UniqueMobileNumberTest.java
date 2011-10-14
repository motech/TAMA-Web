package org.motechproject.tama.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class UniqueMobileNumberTest {
    @Test
    public void generateUniquePhoneNumber() {
        long number1 = UniqueMobileNumber.generate();
        long number2 = UniqueMobileNumber.generate();
        assertEquals(true, number1 != number2);
    }
}
