package org.motechproject.tama.util;

public class UniqueMobileNumber {
    //TODO: when running tests in parallel we might have to come up with something better than this. This doesn't protect you against multiple runs
    private static long uniquePhoneNumber = 1000000001;

    public static long generate() {
        return uniquePhoneNumber++;
    }
}
