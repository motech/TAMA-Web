package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.City;
import org.motechproject.tama.refdata.repository.AllCities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllCitiesCache extends Cachable<City> {

    @Autowired
    public AllCitiesCache(AllCities allCities) {
        super(allCities);
    }

    @Override
    protected String getKey(City city) {
        return city.getId();
    }
}
