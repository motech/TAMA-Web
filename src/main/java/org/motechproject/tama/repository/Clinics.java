package org.motechproject.tama.repository;

import org.apache.commons.lang.StringUtils;
import org.ektorp.CouchDbConnector;
import org.motechproject.tama.domain.Clinic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Clinics extends AbstractCouchRepository<Clinic> {

    private Cities cities;

    @Autowired
    public Clinics(@Qualifier("tamaDbConnector") CouchDbConnector db, Cities cities) {
        super(Clinic.class, db);
        this.cities = cities;
        initStandardDesignDocument();
    }

    @Override
    public List<Clinic> getAll() {
        List<Clinic> clinicList = super.getAll();
        for(Clinic clinic : clinicList) {
            if (!StringUtils.isEmpty(clinic.getCityId()))
                clinic.setCity(cities.get(clinic.getCityId()));
        }
        return clinicList;
    }

    @Override
    public Clinic get(String id) {
        Clinic clinic = super.get(id);
        if (!StringUtils.isEmpty(clinic.getCityId()))
            clinic.setCity(cities.get(clinic.getCityId()));
        return clinic;
    }
}
