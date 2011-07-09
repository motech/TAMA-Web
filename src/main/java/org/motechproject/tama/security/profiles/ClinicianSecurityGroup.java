package org.motechproject.tama.security.profiles;

import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.repository.Clinicians;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.Role;
import org.springframework.beans.factory.annotation.Autowired;

public class ClinicianSecurityGroup extends AbstractSecurityGroup {
    @Autowired
    private Clinicians clinicians;

    public ClinicianSecurityGroup(Clinicians clinicians) {
        this();
        this.clinicians = clinicians;
    }

    public ClinicianSecurityGroup() {
        add(Role.CLINICIAN_DOCTOR, Role.CLINICIAN_STUDY_NURSE);
    }

    public AuthenticatedUser getAuthenticatedUser(String username, String password) {
        Clinician clinician = clinicians.findByUserNameAndPassword(username, password);
        if (clinician == null) return null;
        return createUser(username, password, clinician.getClinicId());
    }

}
