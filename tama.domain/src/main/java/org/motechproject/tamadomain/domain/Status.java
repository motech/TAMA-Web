package org.motechproject.tamadomain.domain;

import java.util.Arrays;
import java.util.List;

public enum Status {
    Inactive("Inactive"),
    Active("Active"),
    Temporary_Deactivation("Temporary Deactivation"),
    Study_Complete("Study complete"),
    Premature_Termination_By_Clinic("Premature termination by clinic"),
    Patient_Withdraws_Consent("Patient withdraws consent"),
    Loss_To_Follow_Up("Loss to follow up"),
    Suspended("Suspended adherence calls");

    private String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public static List<Status> deactivationStatuses() {
        return Arrays.asList(Temporary_Deactivation, Study_Complete, Premature_Termination_By_Clinic, Patient_Withdraws_Consent, Loss_To_Follow_Up);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return this.name();
    }

    public boolean isActive() {
        return this.equals(Status.Active);
    }

    public boolean isSuspended() {
        return this.equals(Status.Suspended);
    }

    @Override
    public String toString() {
       return getDisplayName();
    }
}
