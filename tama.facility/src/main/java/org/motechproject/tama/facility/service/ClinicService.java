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
                    clinic = setTheDocumentIdOfTheAlreadyPersistedClinicContact(dbClinicContacts,dbClinicContacts,clinic);
                } else if (dbClinicContacts.size() > clinic.getClinicianContacts().size()) {
                    clinic = setTheDocumentIdOfTheAlreadyPersistedClinicContact(clinic.getClinicianContacts(),dbClinicContacts,clinic);
                } else {
                    clinic = setTheDocumentIdOfTheAlreadyPersistedClinicContact(dbClinicContacts,dbClinicContacts,clinic);
                }
            }
        }
        return clinic;
    }


    public Clinic setTheDocumentIdOfTheAlreadyPersistedClinicContact(List<Clinic.ClinicianContact> listUsedToLoop,List<Clinic.ClinicianContact> listToSetValuesInto,Clinic clinic)
    {
        for (int i = 0; i < listUsedToLoop.size(); i++) {
            clinic.getClinicianContacts().get(i).setId(listToSetValuesInto.get(i).getId());
        }
        return clinic;
     }


}
