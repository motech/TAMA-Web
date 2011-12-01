package org.motechproject.tamadomain.repository;


import org.motechproject.tamadomain.domain.Administrator;
import org.motechproject.tamadomain.domain.Clinician;
import org.motechproject.tamadomain.domain.TAMAUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllTAMAUsers {

    @Autowired
    AllClinicians allClinicians;

    @Autowired
    AllAdministrators allAdministrators;

    public void update(TAMAUser user){
       if(user instanceof Clinician){
           allClinicians.updatePassword((Clinician)user);
       }else if(user instanceof Administrator){
           allAdministrators.updatePassword((Administrator)user);
       }

    }
}
