package org.motechproject.tamatools.tools.seed;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Checksum {

    private byte[] createChecksum(String fileName) throws Exception {
        InputStream fis = new FileInputStream(fileName);
        return createChecksum(fis);
    }

    private byte[] createChecksum(InputStream fis) throws NoSuchAlgorithmException, IOException {
        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        fis.close();
        return complete.digest();
    }

    public String getMD5Checksum(String fileName) throws Exception {
        byte[] b = createChecksum(fileName);
        return getMD5String(b);
    }

    public String getMD5Checksum(InputStream inputStream) throws Exception {
        byte[] b = createChecksum(inputStream);
        return getMD5String(b);
    }

    private String getMD5String(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result +=
                    Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }
}