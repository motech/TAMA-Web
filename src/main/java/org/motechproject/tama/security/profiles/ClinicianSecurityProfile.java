package org.motechproject.tama.security.profiles;

import org.motechproject.tama.repository.Clinicians;
import org.motechproject.tama.security.Role;
import org.springframework.beans.factory.annotation.Autowired;

public class ClinicianSecurityProfile extends AbstractProfile {
    @Autowired
    private Clinicians clinicians;

    public ClinicianSecurityProfile(Clinicians clinicians) {
        this();
        this.clinicians = clinicians;
    }

    public ClinicianSecurityProfile() {
        addRoles(Role.CLINICIAN_DOCTOR, Role.CLINICIAN_STUDY_NURSE);
    }

    @Override
    protected boolean authenticate(String username, String password) {
        return clinicians.findByUserNameAndPassword(username, password) != null;
    }

}
