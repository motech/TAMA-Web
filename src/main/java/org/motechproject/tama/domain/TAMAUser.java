package org.motechproject.tama.domain;

public interface TAMAUser {
    boolean isAdmin();
    String getName();
    String getUsername();
    String getPassword();
    String getClinicName();
}
