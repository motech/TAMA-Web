package org.motechproject.tama.repository;


import org.motechproject.tama.domain.Administrator;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.domain.TAMAUser;
import org.springframework.beans.factory.annotation.Autowired;

public class TAMAUsers {

    @Autowired
    Clinicians clinicians;

    @Autowired
    Administrators administrators;

    public TAMAUsers(){

    }

    public void put(TAMAUser user){
       if(user instanceof Clinician){
           clinicians.update((Clinician)user);
       }else if(user instanceof Administrator){
           administrators.update((Administrator)user);
       }
    }
}
