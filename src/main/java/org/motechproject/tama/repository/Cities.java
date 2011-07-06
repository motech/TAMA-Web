package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.domain.City;

import java.util.List;

public class Cities extends AbstractCouchRepository<City> {

    public Cities(CouchDbConnector db) {
        super(City.class, db);
        initStandardDesignDocument();
    }

    public void saveAll(List<City> cities) {
         for(City city : cities){
             this.add(city);
         }
    }

    public List<City> getAllCities() {
         return this.getAll();
    }
}
