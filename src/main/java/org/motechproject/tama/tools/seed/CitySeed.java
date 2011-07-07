package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.City;
import org.motechproject.tama.repository.Cities;
import org.springframework.beans.factory.annotation.Autowired;

public class CitySeed extends Seed {
    @Autowired
    private Cities cities;

    @Override
    public void load() {
        cities.add(City.newCity("Pune"));
        cities.add(City.newCity("Mumbai"));
        cities.add(City.newCity("Chennai"));
        cities.add(City.newCity("Chirala"));
        cities.add(City.newCity("Hyderabad"));
        cities.add(City.newCity("Manipur"));
    }
}
