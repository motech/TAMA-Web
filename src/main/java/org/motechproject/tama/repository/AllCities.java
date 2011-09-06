package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.domain.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllCities extends AbstractCouchRepository<City> {

    @Autowired
    public AllCities(@Qualifier("tamaDbConnector") CouchDbConnector db) {
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
