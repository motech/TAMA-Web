package org.motechproject.tama.repository;

import org.apache.commons.lang.StringUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Company;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class Clinics extends AbstractCouchRepository<Clinic> {

    @Autowired
    private Cities cities;


    public Clinics(CouchDbConnector db, Cities cities) {
        super(Clinic.class, db);
        this.cities = cities;
        initStandardDesignDocument();
    }

    public List<Clinic> findClinicEntries(int i, int sizeNo) {
        return getAll();
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
