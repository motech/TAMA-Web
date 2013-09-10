package org.motechproject.tama.refdata.seed;

import org.motechproject.tama.refdata.domain.City;
import org.motechproject.tama.refdata.objectcache.AllCitiesCache;
import org.motechproject.tama.refdata.repository.AllCities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CitySeedV3 {
    @Autowired
    private AllCities allCities;

    @Autowired
    private AllCitiesCache allCitiesCache;

    @org.motechproject.deliverytools.seed.Seed(version = "3.0", priority = 0)
    public void load() {
        allCities.add(City.newCity("Nagpur"));
        allCitiesCache.refresh();
    }
}
