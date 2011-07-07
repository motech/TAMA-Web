package org.motechproject.tama.domain;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.junit.Test;
import org.motechproject.tama.builder.ClinicianBuilder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

    public class ClinicianTest {

    @Test
    public void shouldCallEncryptPassword() {
        Clinician clinician = ClinicianBuilder.startRecording().build();
        String password = "password";
        PBEStringEncryptor encryptor = mock(PBEStringEncryptor.class);
        clinician.setEncryptor(encryptor);

        clinician.setPassword(password);
        verify(encryptor).encrypt(password);

    }

    @Test
    public void shouldCallDeCryptPassword() {
        Clinician clinician = ClinicianBuilder.startRecording().build();
        String encrypted = "encryptedPassword";
        PBEStringEncryptor encryptor = mock(PBEStringEncryptor.class);
        clinician.setEncryptor(encryptor);
        clinician.setEncryptedPassword(encrypted);

        clinician.getPassword();
        verify(encryptor).decrypt(encrypted);

    }
}
