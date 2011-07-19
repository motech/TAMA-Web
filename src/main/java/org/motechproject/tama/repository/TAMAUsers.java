package org.motechproject.tama.repository;


import org.motechproject.tama.domain.Administrator;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.domain.TAMAUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TAMAUsers {

    @Autowired
    Clinicians clinicians;

    @Autowired
    Administrators administrators;

    public void update(TAMAUser user){
       if(user instanceof Clinician){
           clinicians.updatePassword((Clinician)user);
       }else if(user instanceof Administrator){
           administrators.updatePassword((Administrator)user);
       }

    }
}
