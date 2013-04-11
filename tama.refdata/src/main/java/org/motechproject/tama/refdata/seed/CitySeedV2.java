package main.java.org.motechproject.tama.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.refdata.domain.City;
import org.motechproject.tama.refdata.objectcache.AllCitiesCache;
import org.motechproject.tama.refdata.repository.AllCities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CitySeedV2 {

    @Autowired
    private AllCities allCities;

    @Autowired
    private AllCitiesCache allCitiesCache;

    @Seed(version = "2.0", priority = 0)
    public void load() {
        allCities.add(City.newCity("Ahmedabad"));
        allCities.add(City.newCity("Bangalore"));
        allCities.add(City.newCity("Bikaner"));
        allCities.add(City.newCity("Gurgaon"));
        allCities.add(City.newCity("Jaipur"));
        allCities.add(City.newCity("Lucknow"));
        allCities.add(City.newCity("Mangalore"));
        allCities.add(City.newCity("Mysore"));
        allCities.add(City.newCity("New Delhi"));
        allCities.add(City.newCity("Other"));
        allCitiesCache.refresh();
    }
}

