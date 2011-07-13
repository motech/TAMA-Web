package org.motechproject.tama.security.profiles;

import org.apache.log4j.Logger;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.repository.Clinicians;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.Role;
import org.springframework.beans.factory.annotation.Autowired;

public class ClinicianSecurityGroup extends AbstractSecurityGroup {
    @Autowired
    private Clinicians clinicians;

    public ClinicianSecurityGroup() {
        add(Role.CLINICIAN_DOCTOR, Role.CLINICIAN_STUDY_NURSE);
    }

    public ClinicianSecurityGroup(Clinicians clinicians) {
        this();
        this.clinicians = clinicians;
    }

    @Override
    public AuthenticatedUser getAuthenticatedUser(String username, String password) {
        Clinician clinician = clinicians.findByUserNameAndPassword(username, password);
        if (clinician == null) return null;
        return createUser(clinician, clinician.getClinicId());
    }

}
