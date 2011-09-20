package org.motechproject.tama.user.service;

import org.motechproject.ivr.service.UserService;
import org.motechproject.tama.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TamaUserService implements UserService {

    private AllPatients allPatients;

    @Autowired
    public TamaUserService(AllPatients allPatients) {
        this.allPatients = allPatients;
    }

    public boolean isRegisteredUser(String callerId) {
           return (callerId != null && (allPatients.findByMobileNumber(callerId) != null));
    }
}
