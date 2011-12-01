package org.motechproject.tamadomain.domain;

public interface TAMAUser {
    String getName();
    String getUsername();
    String getPassword();
    String getClinicId();
    String getClinicName();

    void setPassword(String newPassword);
}
