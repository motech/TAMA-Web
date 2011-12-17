package org.motechproject.tama.security.profiles;

import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.repository.AllClinicians;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClinicianSecurityGroup extends AbstractSecurityGroup {
    @Autowired
    private AllClinicians clinicians;

    public ClinicianSecurityGroup() {
        add(Role.CLINICIAN_DOCTOR, Role.CLINICIAN_STUDY_NURSE);
    }

    public ClinicianSecurityGroup(AllClinicians clinicians) {
        this();
        this.clinicians = clinicians;
    }

    @Override
    public AuthenticatedUser getAuthenticatedUser(String username, String password) {
        Clinician clinician = clinicians.findByUserNameAndPassword(username, password);
        if (clinician == null) return null;
        return userFor(clinician);
    }

}
