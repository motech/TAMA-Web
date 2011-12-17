package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.tama.refdata.domain.City;
import org.motechproject.tamacommon.repository.AbstractCouchRepository;
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
        for (City city : cities) {
            this.add(city);
        }
    }

    public List<City> getAllCities() {
        return this.getAll();
    }

    @View(name = "find_by_name", map = "function(doc) {if (doc.documentType =='City' && doc.name) {emit(doc.name, doc._id);}}")
    public City findByName(String name) {
        ViewQuery q = createQuery("find_by_name").key(name).includeDocs(true);
        List<City> cities = db.queryView(q, City.class);
        return cities.size() == 0 ? null : cities.get(0);
    }
}
