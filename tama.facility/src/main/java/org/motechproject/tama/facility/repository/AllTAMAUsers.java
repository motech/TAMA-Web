package org.motechproject.tama.facility.repository;


import org.motechproject.tama.common.domain.TAMAUser;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.refdata.domain.Administrator;
import org.motechproject.tama.refdata.domain.Analyst;
import org.motechproject.tama.refdata.repository.AllAdministrators;
import org.motechproject.tama.refdata.repository.AllAnalysts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllTAMAUsers {

    @Autowired
    AllClinicians allClinicians;

    @Autowired
    AllAdministrators allAdministrators;

    @Autowired
    AllAnalysts allAnalysts;

    public void update(TAMAUser user, String userName) {
        if (user instanceof Clinician) {
            allClinicians.updatePassword((Clinician) user, userName);
        } else if (user instanceof Administrator) {
            allAdministrators.updatePassword((Administrator) user);
        } else if (user instanceof Analyst) {
            allAnalysts.updatePassword((Analyst) user);
        }
    }

    public Clinician getClinician(String id) {
        return allClinicians.get(id);
    }
}
