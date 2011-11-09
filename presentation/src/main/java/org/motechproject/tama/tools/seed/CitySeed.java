package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.City;
import org.motechproject.tama.repository.AllCities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CitySeed extends Seed {
    @Autowired
    private AllCities allCities;

    @Override
    public void load() {
        allCities.add(City.newCity("Pune"));
        allCities.add(City.newCity("Mumbai"));
        allCities.add(City.newCity("Chennai"));
        allCities.add(City.newCity("Chirala"));
        allCities.add(City.newCity("Hyderabad"));
        allCities.add(City.newCity("Manipur"));
    }
}