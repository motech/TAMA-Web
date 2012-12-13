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

    public static final String CLINICIAN = "clinician";
    public static final String ANALYST = "analyst";

    AllClinicians allClinicians;
    AllAdministrators allAdministrators;
    AllAnalysts allAnalysts;

    @Autowired
    public AllTAMAUsers(AllClinicians allClinicians, AllAdministrators allAdministrators, AllAnalysts allAnalysts) {
        this.allClinicians = allClinicians;
        this.allAdministrators = allAdministrators;
        this.allAnalysts = allAnalysts;
    }

    public void update(TAMAUser user, String userName) {
        if (user instanceof Clinician) {
            allClinicians.updatePassword((Clinician) user, userName);
        } else if (user instanceof Administrator) {
            allAdministrators.updatePassword((Administrator) user);
        } else if (user instanceof Analyst) {
            allAnalysts.updatePassword((Analyst) user);
        }
    }

    public TAMAUser getUser(String id, String userType) {
        if (CLINICIAN.equalsIgnoreCase(userType)) {
            return allClinicians.get(id);
        } else if (ANALYST.equalsIgnoreCase(userType)) {
            return allAnalysts.get(id);
        }
        return null;
    }
}
