package org.motechproject.tama.common.domain;

public interface TAMAUser {
    String getName();

    String getUsername();

    String getPassword();

    String getClinicId();

    String getClinicName();

    void setPassword(String newPassword);
}
