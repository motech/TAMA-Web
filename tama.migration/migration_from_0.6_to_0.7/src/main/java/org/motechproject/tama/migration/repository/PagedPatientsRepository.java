package org.motechproject.tama.migration.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.motechproject.tama.common.repository.AllAuditRecords;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;
import org.motechproject.tama.refdata.objectcache.AllGendersCache;
import org.motechproject.tama.refdata.objectcache.AllHIVTestReasonsCache;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.refdata.objectcache.AllModesOfTransmissionCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagedPatientsRepository extends AllPatients implements Paged<Patient> {

    @Autowired
    public PagedPatientsRepository(@Qualifier("tamaDbConnector") CouchDbConnector db,
                                   AllClinics allClinics,
                                   AllGendersCache allGenders,
                                   AllIVRLanguagesCache allIVRLanguages,
                                   AllUniquePatientFields allUniquePatientFields,
                                   AllHIVTestReasonsCache allHIVTestReasons,
                                   AllModesOfTransmissionCache allModesOfTransmission,
                                   AllAuditRecords allAuditRecords) {
        super(db, allClinics, allGenders, allIVRLanguages, allUniquePatientFields, allHIVTestReasons, allModesOfTransmission, allAuditRecords);
    }

    @Override
    public List<Patient> get(int skip, int limit) {
        ViewQuery query = createQuery("all").includeDocs(true).skip(skip).limit(limit);
        List<Patient> patients = db.queryView(query, Patient.class);
        for (Patient patient : patients) {
            loadPatientDependencies(patient, true);
        }
        return patients;
    }
}
