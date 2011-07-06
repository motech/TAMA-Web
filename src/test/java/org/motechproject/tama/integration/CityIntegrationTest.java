package org.motechproject.tama.integration;

import org.junit.Test;
import org.motechproject.tama.domain.City;
import org.motechproject.tama.integration.domain.SpringIntegrationTest;
import org.motechproject.tama.repository.Cities;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class CityIntegrationTest extends SpringIntegrationTest {

    @Autowired
    private Cities cities;

    @Test
    public void testShouldListCities() {
        int priorSize = cities.getAllCities().size();
        cities.saveAll(new ArrayList<City>() {
            {
                add(new City());
                add(new City());
            }
        });
       assertEquals(priorSize + 2, cities.getAllCities().size());
      for (City city:cities.getAll()){
          markForDeletion(city);
      }
    }
}
