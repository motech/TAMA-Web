package org.motechproject.tama.facility.repository;

import org.apache.commons.lang.StringUtils;
import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AllAuditRecords;
import org.motechproject.tama.common.repository.AuditableCouchRepository;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.refdata.objectcache.AllCitiesCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllClinics extends AuditableCouchRepository<Clinic> {

    private AllCitiesCache allCities;

    @Autowired
    public AllClinics(@Qualifier("tamaDbConnector") CouchDbConnector db, AllCitiesCache allCities, AllAuditRecords allAuditRecords) {
        super(Clinic.class, db, allAuditRecords);
        this.allCities = allCities;
        initStandardDesignDocument();
    }

    @Override
    public List<Clinic> getAll() {
        List<Clinic> clinicList = super.getAll();
        for (Clinic clinic : clinicList) {
            if (!StringUtils.isEmpty(clinic.getCityId()))
                clinic.setCity(allCities.getBy(clinic.getCityId()));
        }
        return clinicList;
    }

    @Override
    public Clinic get(String id) {
        Clinic clinic = super.get(id);
        if (!StringUtils.isEmpty(clinic.getCityId()))
            clinic.setCity(allCities.getBy(clinic.getCityId()));
        return clinic;
    }
}
