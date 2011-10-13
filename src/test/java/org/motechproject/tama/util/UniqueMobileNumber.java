package org.motechproject.tama.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class UniqueMobileNumber {
    private static final File lastUsedPhoneNumberFile = new File("logs/phoneNumber.txt");
    static long StartingNumber = 1000000001;

    public static long generate() {
        try {
            if (!lastUsedPhoneNumberFile.exists()) FileUtils.writeStringToFile(lastUsedPhoneNumberFile, Long.toString(StartingNumber));
            synchronized (lastUsedPhoneNumberFile) {
                String lastNumberSaved = FileUtils.readFileToString(lastUsedPhoneNumberFile);
                long newNumber = Long.parseLong(lastNumberSaved) + 1;
                FileUtils.writeStringToFile(lastUsedPhoneNumberFile, Long.toString(newNumber));
                return newNumber;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error generating unique phone number", e);
        }
    }

    static void startOver() {
        if (lastUsedPhoneNumberFile.exists()) lastUsedPhoneNumberFile.delete();
    }
}