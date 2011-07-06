package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.City;
import org.motechproject.tama.repository.Cities;
import org.springframework.beans.factory.annotation.Autowired;

public class CitySeed extends Seed {
    @Autowired
    private Cities cities;

    @Override
    public void load() {
        cities.add(new City("Pune"));
        cities.add(new City("Mumbai"));
        cities.add(new City("Chennai"));
        cities.add(new City("Chirala"));
        cities.add(new City("Hyderabad"));
        cities.add(new City("Manipur"));
    }
}
