package org.motechproject.tama.facility.service;


import com.sun.corba.se.pept.protocol.ClientInvocationInfo;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class ClinicService {

    private AllClinics allClinics;

    @Autowired
    public ClinicService(AllClinics allClinics) {
        this.allClinics = allClinics;
    }

    public List<Clinic> getAllClinics() {
        return allClinics.getAll();
    }

    public Clinic findIfClinicContactsExistsAndUpdate(Clinic clinic) {
        Clinic dbClinic = allClinics.get(clinic.getId());
        if (dbClinic.getId().equals(clinic.getId())) {
            List<Clinic.ClinicianContact> dbClinicContacts = dbClinic.getClinicianContacts();
            if (!(CollectionUtils.isEmpty(dbClinicContacts) && CollectionUtils.isEmpty(clinic.getClinicianContacts()))) {
                if (dbClinicContacts.size() == clinic.getClinicianContacts().size()) {
                    for (int i = 0; i < dbClinicContacts.size(); i++) {
                        if (dbClinicContacts.get(i) != null) {
                            if (dbClinicContacts.get(i).getId() != null) {
                                clinic.getClinicianContacts().get(i).setId(dbClinicContacts.get(i).getId());
                            }

                        }

                    }
                } else if (dbClinicContacts.size() > clinic.getClinicianContacts().size()) {
                    for (int i = 0; i < clinic.getClinicianContacts().size(); i++) {
                        if (dbClinicContacts.get(i) != null) {
                            if (dbClinicContacts.get(i).getName().equals(clinic.getClinicianContacts().get(i).getName()) &&
                                    dbClinicContacts.get(i).getPhoneNumber().equals(clinic.getClinicianContacts().get(i).getPhoneNumber())) {
                                if (dbClinicContacts.get(i).getId() != null) {
                                    clinic.getClinicianContacts().get(i).setId(dbClinicContacts.get(i).getId());
                                }
                            }
                        }

                    }
                } else {
                    for (int i = 0; i < dbClinicContacts.size(); i++) {
                        if (dbClinicContacts.get(i) != null) {

                            if (dbClinicContacts.get(i).getId() != null) {
                                if (clinic.getClinicianContacts().get(i) != null) {
                                    clinic.getClinicianContacts().get(i).setId(dbClinicContacts.get(i).getId());
                                }
                            }

                        }

                    }
                }
            }
        }
        return clinic;
    }
}
